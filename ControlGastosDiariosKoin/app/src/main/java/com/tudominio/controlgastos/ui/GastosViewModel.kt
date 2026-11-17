package com.tudominio.controlgastos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tudominio.controlgastos.data.GastosRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// El repositorio ahora se RECIBE por constructor (lo inyecta Koin).
// Ya no se crea dentro del ViewModel.
class GastosViewModel(
    private val repositorio: GastosRepositorio // INYECTADA desde afuera por constructor
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state: StateFlow<GastosState> = _state.asStateFlow()

    init {
        // Al crear el ViewModel, cargamos los gastos automáticamente
        handleIntent(GastosIntent.CargarGastos)
    }

    fun handleIntent(intent: GastosIntent) {
        when (intent) {
            // Intents existentes de la Sesión 1
            is GastosIntent.IncrementarGasto -> incrementarGasto()
            is GastosIntent.DecrementarGasto -> decrementarGasto()
            is GastosIntent.EstablecerLimite -> establecerLimite(intent.nuevoLimite)

            // NUEVOS Intents de la Sesión 3
            is GastosIntent.CargarGastos -> cargarGastos()
            is GastosIntent.AgregarGasto -> agregarGasto(intent.monto, intent.descripcion)
            is GastosIntent.EliminarGasto -> eliminarGasto(intent.id)
        }
    }

    // ========== MÉTODOS EXISTENTES (Sesión 1) ==========
    private fun incrementarGasto() {
        _state.update { estado ->
            val nuevoMonto = estado.montoActual + 10.0

            if (nuevoMonto > estado.limite) {
                estado.copy(
                    mensajeError = "¡Has superado el límite de ${estado.limite}!"
                )
            } else {
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
                mensajeError = if (nuevoMonto > estado.limite) estado.mensajeError else null
            )
        }
    }

    private fun establecerLimite(nuevoLimite: Double) {
        _state.update { it.copy(limite = nuevoLimite.coerceAtLeast(0.0)) }
    }

    // ========== NUEVOS MÉTODOS (Sesión 3) ==========
    private fun cargarGastos() {
        viewModelScope.launch {
            // Mostrar indicador de carga
            _state.update { it.copy(isLoading = true) }

            // Obtener datos del repositorio (simula operación asíncrona)
            val gastos = repositorio.obtenerGastos()
            val total = repositorio.obtenerTotalGastos()

            // Actualizar el estado con los datos obtenidos
            _state.update { estado ->
                estado.copy(
                    listaGastos = gastos,
                    totalGastos = total,
                    isLoading = false
                )
            }
        }
    }

    private fun agregarGasto(monto: Double, descripcion: String) {
        viewModelScope.launch {
            // Guardar en el repositorio
            repositorio.guardarGasto(monto, descripcion)

            // Recargar la lista para mostrar el nuevo gasto
            cargarGastos()

            // También actualizar el monto actual de la Sesión 1
            _state.update { estado ->
                estado.copy(
                    montoActual = estado.montoActual + monto
                )
            }
        }
    }

    private fun eliminarGasto(id: Int) {
        viewModelScope.launch {
            // Eliminar del repositorio
            repositorio.eliminarGasto(id)

            // Recargar la lista actualizada
            cargarGastos()
        }
    }
}
