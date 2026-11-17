package com.tudominio.controlgastos.ui

sealed class GastosIntent {
    object IncrementarGasto : GastosIntent()
    object DecrementarGasto : GastosIntent()
    data class EstablecerLimite(val nuevoLimite: Double) : GastosIntent()
}
