package com.familiasquesuman.demo.data.remote

import com.familiasquesuman.demo.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiException(val status: Int, mensaje: String) : Exception(mensaje)

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
val jsonApi = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    encodeDefaults = true
}

private interface RefreshService {
    @POST("auth/refresh")
    fun refresh(@Body datos: RefreshIn): Call<TokensDto>
}

class ApiClient(private val sesion: SesionStore) {

    val host: String = BuildConfig.API_HOST
    private val baseUrl = "$host/api/v1/"
    private val tipoJson = "application/json".toMediaType()

    private val clienteBase: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
        .build()

    private val refreshService: RefreshService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(clienteBase)
        .addConverterFactory(jsonApi.asConverterFactory(tipoJson))
        .build()
        .create(RefreshService::class.java)

    val okHttp: OkHttpClient = clienteBase.newBuilder()
        .addInterceptor(AuthInterceptor(sesion))
        .authenticator(RenovadorToken(sesion, refreshService))
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttp)
        .addConverterFactory(jsonApi.asConverterFactory(tipoJson))
        .build()
        .create(ApiService::class.java)
}

private class AuthInterceptor(private val sesion: SesionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sesion.accessToken
        val req = if (token != null && chain.request().header("Authorization") == null) {
            chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        } else chain.request()
        return chain.proceed(req)
    }
}

// Ante un 401 renueva el access token con el refresh token (rotado) y reintenta una vez
private class RenovadorToken(private val sesion: SesionStore, private val refreshService: RefreshService) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val ruta = response.request.url.encodedPath
        if (ruta.contains("/auth/") && !ruta.endsWith("/auth/logout")) return null
        if (response.priorResponse != null) return null

        synchronized(this) {
            val actual = sesion.accessToken ?: return null
            val usado = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (usado != actual) return conToken(response.request, actual)

            val refresh = sesion.refreshToken ?: return null
            val resultado = try {
                refreshService.refresh(RefreshIn(refresh)).execute()
            } catch (e: IOException) {
                return null
            }
            val nuevos = resultado.body()
            if (!resultado.isSuccessful || nuevos == null) {
                if (resultado.code() == 401) sesion.limpiar()
                return null
            }
            sesion.guardarTokens(nuevos.accessToken, nuevos.refreshToken)
            return conToken(response.request, nuevos.accessToken)
        }
    }

    private fun conToken(req: Request, token: String): Request =
        req.newBuilder().header("Authorization", "Bearer $token").build()
}

// Convierte errores HTTP/red en ApiException con mensaje para la persona usuaria
suspend fun <T> llamarApi(bloque: suspend () -> T): T = try {
    bloque()
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    val cuerpo = e.response()?.errorBody()?.string()
    val mensaje = cuerpo?.let { runCatching { jsonApi.decodeFromString<ErrorDto>(it).error?.mensaje }.getOrNull() }
    throw ApiException(
        e.code(),
        mensaje ?: when (e.code()) {
            401 -> "Tu sesión expiró. Inicia sesión de nuevo."
            403 -> "No tienes permiso para hacer esto."
            404 -> "No encontramos lo que buscas."
            429 -> "Demasiados intentos. Espera un momento."
            else -> "El servidor respondió con un error (${e.code()})."
        }
    )
} catch (e: IOException) {
    throw ApiException(0, "Sin conexión. Revisa tu internet e inténtalo de nuevo.")
} catch (e: SerializationException) {
    throw ApiException(-1, "El servidor respondió algo inesperado. Intenta más tarde.")
}
