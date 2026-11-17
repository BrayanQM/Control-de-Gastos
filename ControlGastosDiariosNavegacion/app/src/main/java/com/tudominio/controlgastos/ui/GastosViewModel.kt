package com.tudominio.controlgastos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tudominio.controlgastos.data.GastosRepositorio
import com.tudominio.controlgastos.data.SesionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GastosViewModel(
    // Inyección por constructor: repositorio y gestor de sesión
    private val repositorio: GastosRepositorio,
    private val sesionManager: SesionManager
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state: StateFlow<GastosState> = _state.asStateFlow()

    // Estado de autenticación para la navegación
    private val _estaAutenticado = MutableStateFlow(false)
    val estaAutenticado: StateFlow<Boolean> = _estaAutenticado.asStateFlow()

    // Nombre del usuario logueado
    private val _usuarioNombre = MutableStateFlow("")
    val usuarioNombre: StateFlow<String> = _usuarioNombre.asStateFlow()

    init {
        // Verificar si hay sesión guardada al iniciar
        verificarSesion()
        // Cargar gastos si está autenticado
        cargarGastosSiAutenticado()
    }

    private fun verificarSesion() {
        viewModelScope.launch {
            sesionManager.estaAutenticado.collect { autenticado ->
                _estaAutenticado.value = autenticado
            }
        }
        viewModelScope.launch {
            sesionManager.usuarioNombre.collect { nombre ->
                _usuarioNombre.value = nombre ?: "Usuario"
            }
        }
    }

    private fun cargarGastosSiAutenticado() {
        viewModelScope.launch {
            sesionManager.estaAutenticado.collect { autenticado ->
                if (autenticado) {
                    handleIntent(GastosIntent.CargarGastos)
                }
            }
        }
    }

    fun handleIntent(intent: GastosIntent) {
        when (intent) {
            // Intents existentes de la Sesión 1
            is GastosIntent.IncrementarGasto -> incrementarGasto()
            is GastosIntent.DecrementarGasto -> decrementarGasto()
            is GastosIntent.EstablecerLimite -> establecerLimite(intent.nuevoLimite)

            // Intents de la Sesión 3
            is GastosIntent.CargarGastos -> cargarGastos()
            is GastosIntent.AgregarGasto -> agregarGasto(intent.monto, intent.descripcion)
            is GastosIntent.EliminarGasto -> eliminarGasto(intent.id)

            // NUEVOS Intents para autenticación
            is GastosIntent.Login -> login(intent.email, intent.password)
            is GastosIntent.Logout -> logout()
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

    // ========== MÉTODOS DE LA SESIÓN 3 ==========
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

    // ========== NUEVOS MÉTODOS DE AUTENTICACIÓN ==========
    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // SIMULACIÓN: en producción, llamar a una API real o Firebase
            delay(1000) // Simular latencia de red

            if (email == "demo@controlgastos.com" && password == "123456") {
                // Login exitoso
                sesionManager.guardarSesion(
                    usuarioId = "123",
                    usuarioNombre = "Usuario Demo",
                    token = "fake-jwt-token-123456"
                )
                _state.update { it.copy(isLoading = false, mensajeError = null) }
            } else {
                // Login fallido
                _state.update {
                    it.copy(
                        isLoading = false,
                        mensajeError = "Email o contraseña incorrectos"
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sesionManager.cerrarSesion()
            // Limpiar el estado local
            _state.update {
                GastosState() // Estado inicial - Resetea el estado completo de la app
            }
        }
    }
}
