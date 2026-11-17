package com.tudominio.controlgastos.navigation

// Rutas tipadas usando sealed class
// Type-Safe Navigation: errores en compilación, no en runtime
sealed class Pantalla(val route: String) {
    // Pantalla de login (pública, sin autenticación)
    object Login : Pantalla("login")

    // Pantalla principal de gastos (requiere autenticación)
    object Gastos : Pantalla("gastos")

    // Pantalla de configuración (requiere autenticación)
    object Configuracion : Pantalla("configuracion")
}

// Constantes para usar en el NavHost
object Rutas {
    const val LOGIN = "login"
    const val GASTOS = "gastos"
    const val CONFIGURACION = "configuracion"
}
