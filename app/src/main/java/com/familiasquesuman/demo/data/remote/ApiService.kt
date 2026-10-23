package com.familiasquesuman.demo.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("auth/registro")
    suspend fun registro(@Body datos: RegistroIn): TokensDto

    @POST("auth/login")
    suspend fun login(@Body datos: LoginIn): TokensDto

    @POST("auth/google")
    suspend fun google(@Body datos: GoogleIn): TokensDto

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") auth: String, @Body datos: RefreshIn): MensajeDto

    // Perfil / familia
    @GET("me")
    suspend fun perfil(): UsuarioDto

    @PATCH("me")
    suspend fun editarPerfil(@Body datos: PerfilPatch): UsuarioDto

    @Multipart
    @PUT("me/foto")
    suspend fun subirFoto(@Part archivo: MultipartBody.Part): UsuarioDto

    @GET("me/familia")
    suspend fun familia(): FamiliaDto

    @PATCH("me/familia")
    suspend fun editarFamilia(@Body datos: FamiliaPatch): FamiliaDto

    @POST("me/familia/integrantes")
    suspend fun agregarIntegrante(@Body datos: IntegranteIn): IntegranteDto

    @PATCH("me/familia/integrantes/{id}")
    suspend fun editarIntegrante(@Path("id") id: String, @Body datos: IntegranteIn): IntegranteDto

    @DELETE("me/familia/integrantes/{id}")
    suspend fun eliminarIntegrante(@Path("id") id: String): MensajeDto

    @DELETE("me")
    suspend fun eliminarCuenta(): MensajeDto

    @GET("me/insignias")
    suspend fun insignias(): InsigniasDto

    // Catálogo
    @GET("sync")
    suspend fun sync(@Query("ciudad") ciudad: String, @Query("desde") desde: String?): SyncDto

    @GET("actividades/{id}")
    suspend fun actividad(@Path("id") id: String): ActividadDto

    @GET("centros/{id}")
    suspend fun centro(@Path("id") id: String): CentroDto

    @GET("proyectos/{id}")
    suspend fun proyecto(@Path("id") id: String): ProyectoDto

    // Inscripciones
    @GET("me/inscripciones")
    suspend fun misInscripciones(): List<InscripcionDto>

    @POST("actividades/{id}/inscripciones")
    suspend fun inscribirse(
        @Path("id") actividadId: String,
        @Header("Idempotency-Key") clave: String,
        @Body datos: InscripcionIn
    ): InscripcionDto

    @POST("me/inscripciones/{id}/cancelar")
    suspend fun cancelarInscripcion(@Path("id") id: String): InscripcionDto

    // Encuestas
    @GET("encuestas/{momento}")
    suspend fun preguntas(@Path("momento") momento: String): EncuestaPreguntasDto

    @POST("me/inscripciones/{id}/encuesta")
    suspend fun responderEncuesta(
        @Path("id") inscripcionId: String,
        @Header("Idempotency-Key") clave: String,
        @Body datos: EncuestaIn
    ): EncuestaDto

    @GET("me/inscripciones/{id}/encuestas")
    suspend fun encuestasRespondidas(@Path("id") inscripcionId: String): List<EncuestaDto>

    // Donaciones
    @GET("me/donaciones")
    suspend fun misDonaciones(): List<DonacionDto>

    @POST("me/donaciones")
    suspend fun registrarDonacion(@Header("Idempotency-Key") clave: String, @Body datos: DonacionIn): DonacionDto

    // Notificaciones
    @GET("me/notificaciones")
    suspend fun notificaciones(): List<NotificacionDto>

    @POST("me/notificaciones/leidas")
    suspend fun marcarLeidas(): MensajeDto

    // Testimonios
    @GET("testimonios")
    suspend fun testimonios(@Query("actividad_id") actividadId: String? = null, @Query("limit") limit: Int = 50): PaginaDto<TestimonioDto>

    @GET("me/testimonios")
    suspend fun misTestimonios(): List<TestimonioDto>

    @Multipart
    @POST("me/testimonios")
    suspend fun crearTestimonio(
        @Part("actividad_id") actividadId: RequestBody,
        @Part("texto") texto: RequestBody,
        @Part foto: MultipartBody.Part?
    ): TestimonioDto

    @DELETE("me/testimonios/{id}")
    suspend fun borrarTestimonio(@Path("id") id: String): MensajeDto

    // Likes y comentarios: tipo = actividad | campana | proyecto | testimonio
    @PUT("likes/{tipo}/{id}")
    suspend fun darLike(@Path("tipo") tipo: String, @Path("id") id: String): LikeDto

    @DELETE("likes/{tipo}/{id}")
    suspend fun quitarLike(@Path("tipo") tipo: String, @Path("id") id: String): LikeDto

    @GET("comentarios/{tipo}/{id}")
    suspend fun comentarios(@Path("tipo") tipo: String, @Path("id") id: String, @Query("limit") limit: Int = 50): PaginaDto<ComentarioDto>

    @POST("comentarios/{tipo}/{id}")
    suspend fun comentar(@Path("tipo") tipo: String, @Path("id") id: String, @Body datos: ComentarioIn): ComentarioDto

    @DELETE("comentarios/{id}")
    suspend fun borrarComentario(@Path("id") id: String): MensajeDto

    @POST("reportes")
    suspend fun reportar(@Body datos: ReporteIn): MensajeDto

    // Mensajes familia ↔ asociación
    @GET("conversaciones")
    suspend fun conversaciones(): List<ConversacionDto>

    @POST("conversaciones")
    suspend fun iniciarConversacion(@Body datos: ConversacionIn): ConversacionDto

    @GET("conversaciones/{id}/mensajes")
    suspend fun mensajes(@Path("id") id: String, @Query("desde") desde: String?, @Query("limit") limit: Int = 200): List<MensajeChatDto>

    @POST("conversaciones/{id}/mensajes")
    suspend fun enviarMensaje(@Path("id") id: String, @Body datos: MensajeIn): MensajeChatDto

    @POST("conversaciones/{id}/bloquear")
    suspend fun bloquearConversacion(@Path("id") id: String): MensajeDto

    // Gestión (asociación / admin)
    @GET("gestion/centros")
    suspend fun misCentros(): List<CentroDto>

    @GET("gestion/centros/{id}/actividades")
    suspend fun actividadesDeMiCentro(@Path("id") centroId: String): List<ActividadDto>

    @POST("gestion/centros/{id}/actividades")
    suspend fun crearActividad(@Path("id") centroId: String, @Body datos: ActividadIn): ActividadDto

    @PATCH("gestion/actividades/{id}")
    suspend fun editarActividad(@Path("id") id: String, @Body datos: ActividadPatch): ActividadDto

    @Multipart
    @PUT("gestion/actividades/{id}/imagen")
    suspend fun imagenActividad(@Path("id") id: String, @Part archivo: MultipartBody.Part): ActividadDto

    @GET("gestion/actividades/{id}/asistentes")
    suspend fun asistentes(@Path("id") id: String): List<AsistenteListaDto>

    @POST("gestion/actividades/{id}/asistencia")
    suspend fun marcarAsistencia(@Path("id") id: String, @Body datos: AsistenciaIn): MensajeDto

    @GET("gestion/centros/{id}/campanas")
    suspend fun campanasDeMiCentro(@Path("id") centroId: String): List<CampanaDto>

    @POST("gestion/centros/{id}/campanas")
    suspend fun crearCampana(@Path("id") centroId: String, @Body datos: CampanaIn): CampanaDto

    @PATCH("gestion/campanas/{id}")
    suspend fun editarCampana(@Path("id") id: String, @Body datos: CampanaPatch): CampanaDto

    @GET("gestion/centros/{id}/proyectos")
    suspend fun proyectosDeMiCentro(@Path("id") centroId: String): List<ProyectoDto>

    @POST("gestion/centros/{id}/proyectos")
    suspend fun crearProyecto(@Path("id") centroId: String, @Body datos: ProyectoIn): ProyectoDto

    @PATCH("gestion/proyectos/{id}")
    suspend fun editarProyecto(@Path("id") id: String, @Body datos: ProyectoPatch): ProyectoDto

    @GET("gestion/resumen")
    suspend fun resumenAdmin(): ResumenAdminDto

    // Moderación (admin)
    @GET("admin/testimonios")
    suspend fun testimoniosPorModerar(@Query("estado") estado: String = "pendiente", @Query("limit") limit: Int = 50): PaginaDto<TestimonioDto>

    @POST("admin/testimonios/{id}/moderar")
    suspend fun moderarTestimonio(@Path("id") id: String, @Body datos: ModeracionIn): TestimonioDto

    @GET("admin/comentarios")
    suspend fun comentariosPorModerar(@Query("estado") estado: String = "pendiente", @Query("limit") limit: Int = 50): PaginaDto<ComentarioDto>

    @POST("admin/comentarios/{id}/moderar")
    suspend fun moderarComentario(@Path("id") id: String, @Body datos: ModeracionIn): ComentarioDto
}
