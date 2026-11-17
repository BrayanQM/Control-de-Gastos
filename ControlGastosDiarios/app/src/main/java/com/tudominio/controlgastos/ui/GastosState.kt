package com.tudominio.controlgastos.ui

data class GastosState(
    val montoActual: Double = 0.0,
    val limite: Double = 500.0,
    val mensajeError: String? = null
)
