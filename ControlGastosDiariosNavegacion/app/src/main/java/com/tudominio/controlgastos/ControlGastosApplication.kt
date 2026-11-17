package com.tudominio.controlgastos

import android.app.Application
import com.tudominio.controlgastos.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ControlGastosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Iniciar Koin con nuestra configuración
        startKoin {
            // Contexto de Android necesario para algunas funciones
            androidContext(this@ControlGastosApplication)
            // Los módulos que definimos
            modules(appModule)
        }
    }
}
