package com.tudominio.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tudominio.controlgastos.navigation.Pantalla
import com.tudominio.controlgastos.ui.GastosViewModel
import com.tudominio.controlgastos.ui.PantallaGastos
import com.tudominio.controlgastos.ui.PantallaLogin
import com.tudominio.controlgastos.ui.theme.ControlGastosDiariosTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlGastosDiariosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavegacion()
                }
            }
        }
    }
}

@Composable
fun AppNavegacion() {
    // Controlador de rutas protegidas
    val viewModel: GastosViewModel = koinViewModel()
    val estaAutenticado by viewModel.estaAutenticado.collectAsState()
    val navController = rememberNavController()

    // Selección de la pantalla base reactiva a la persistencia del DataStore
    val destinoInicial = if (estaAutenticado) Pantalla.Gastos.route
    else Pantalla.Login.route

    NavHost(
        navController = navController,
        startDestination = destinoInicial
    ) {
        // Ruta pública
        composable(Pantalla.Login.route) {
            PantallaLogin(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Ruta protegida: valida tokens y credenciales
        composable(Pantalla.Gastos.route) {
            if (!estaAutenticado) {
                LaunchedEffect(Unit) {
                    navController.navigate(Pantalla.Login.route) {
                        popUpTo(Pantalla.Gastos.route) { inclusive = true }
                    }
                }
            } else {
                PantallaGastos(
                    viewModel = viewModel,
                    alIrAConfiguracion = {
                        navController.navigate(Pantalla.Configuracion.route)
                    }
                )
            }
        }

        // Ruta protegida de configuración
        composable(Pantalla.Configuracion.route) {
            if (!estaAutenticado) {
                LaunchedEffect(Unit) {
                    navController.navigate(Pantalla.Login.route) {
                        popUpTo(Pantalla.Configuracion.route) { inclusive = true }
                    }
                }
            } else {
                Text("Pantalla de Configuración - En Construcción")
            }
        }
    }
}
