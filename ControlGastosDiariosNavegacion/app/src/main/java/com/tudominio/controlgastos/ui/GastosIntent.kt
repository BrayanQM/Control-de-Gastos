package com.tudominio.controlgastos.ui

sealed class GastosIntent {
    // Intents existentes de la Sesión 1
    object IncrementarGasto : GastosIntent()
    object DecrementarGasto : GastosIntent()
    data class EstablecerLimite(val nuevoLimite: Double) : GastosIntent()

    // Intents de la Sesión 3 (repositorio)
    object CargarGastos : GastosIntent() // Cargar lista desde el repositorio
    data class AgregarGasto(val monto: Double, val descripcion: String) : GastosIntent() // Guardar nuevo gasto
    data class EliminarGasto(val id: Int) : GastosIntent() // Eliminar un gasto existente

    // NUEVOS Intents para autenticación (Sesión 4)
    data class Login(val email: String, val password: String) : GastosIntent()
    data object Logout : GastosIntent()
}
