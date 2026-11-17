package com.tudominio.controlgastos.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Gasto(
    val id: Int,
    val monto: Double,
    val descripcion: String,
    val fecha: String
) {
    companion object {
        fun obtenerFechaActual(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(Date())
        }
    }
}
