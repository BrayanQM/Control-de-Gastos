package com.tudominio.controlgastos.ui

import com.tudominio.controlgastos.data.Gasto

data class GastosState(
    val montoActual: Double = 0.0,
    val limite: Double = 500.0,
    val mensajeError: String? = null,
    // NUEVAS PROPIEDADES para la Sesión 3
    val listaGastos: List<Gasto> = emptyList(), // Lista de gastos guardados
    val totalGastos: Double = 0.0,              // Suma de todos los gastos
    val isLoading: Boolean = false              // Indica si está cargando
)
