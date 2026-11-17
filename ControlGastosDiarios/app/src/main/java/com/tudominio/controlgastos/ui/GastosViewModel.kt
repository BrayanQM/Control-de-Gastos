package com.tudominio.controlgastos.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GastosViewModel : ViewModel() {
    // Privado: solo este ViewModel puede modificarlo
    private val _state = MutableStateFlow(GastosState())
    // Público: la UI solo puede leerlo, no modificarlo
    val state: StateFlow<GastosState> = _state.asStateFlow()

    fun handleIntent(intent: GastosIntent) {
        when (intent) {
            is GastosIntent.IncrementarGasto -> incrementarGasto()
            is GastosIntent.DecrementarGasto -> decrementarGasto()
            is GastosIntent.EstablecerLimite -> establecerLimite(intent.nuevoLimite)
        }
    }

    private fun incrementarGasto() {
        _state.update { estado ->
            val nuevoMonto = estado.montoActual + 10.0

            if (nuevoMonto > estado.limite) {
                // Si supera el límite, mostramos error y NO actualizamos el monto
                estado.copy(
                    mensajeError = "¡Has superado el límite de ${estado.limite}!"
                )
            } else {
                // Si está dentro del límite, actualizamos el monto y limpiamos error
                estado.copy(
                    montoActual = nuevoMonto,
                    mensajeError = null
                )
            }
        }
    }

    private fun decrementarGasto() {
        _state.update { estado ->
            val nuevoMonto = (estado.montoActual - 10.0).coerceAtLeast(0.0)

            estado.copy(
                montoActual = nuevoMonto,
                // Si el nuevo monto está dentro del límite, limpiamos el error
                mensajeError = if (nuevoMonto > estado.limite) estado.mensajeError else null
            )
        }
    }

    private fun establecerLimite(nuevoLimite: Double) {
        _state.update { estado ->
            estado.copy(
                limite = nuevoLimite.coerceAtLeast(0.0),
                // Si el nuevo límite resuelve un error previo, lo limpiamos
                mensajeError = if (estado.montoActual <= nuevoLimite) null else estado.mensajeError
            )
        }
    }
}
