package com.tudominio.controlgastos.di

import com.tudominio.controlgastos.data.GastosRepositorio
import com.tudominio.controlgastos.data.GastosRepositorioSimulado
import com.tudominio.controlgastos.ui.GastosViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // single = UNA SOLA instancia para toda la aplicación
    // <GastosRepositorio> = le decimos a Koin qué interfaz estamos implementando
    single<GastosRepositorio> {
        GastosRepositorioSimulado() // Cómo se crea
    }

    // viewModel = crea ViewModels respetando su ciclo de vida
    // get() = pídele a Koin que me dé la dependencia que necesita este ViewModel
    viewModel {
        GastosViewModel(get()) // get() obtiene el repositorio que definimos arriba
    }
}
