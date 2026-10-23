package com.familiasquesuman.demo

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.familiasquesuman.demo.data.AppContainer

class FamiliasApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        AppContainer.inicializar(this)
    }

    // Las imágenes se sirven por la API con token, así que Coil usa el mismo OkHttp autenticado
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient { AppContainer.apiClient.okHttp }
            .crossfade(true)
            .build()
}
