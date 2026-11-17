package com.tudominio.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tudominio.controlgastos.ui.GastosIntent
import com.tudominio.controlgastos.ui.GastosViewModel
import com.tudominio.controlgastos.ui.theme.ControlGastosDiariosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlGastosDiariosTheme {
                // Un Surface es un contenedor que maneja el fondo y la elevación
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaGastos()
                }
            }
        }
    }
}

@Composable
fun PantallaGastos() {
    val viewModel: GastosViewModel = viewModel()
    // Observamos el estado; "by" nos permite acceder a las propiedades directamente
    val estado by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text( // Título de la App
            text = "Control del Gastos Diarios",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(96.dp))

        // Tarjeta que muestra el monto actual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Monto Actual
                Text(
                    text = "Gasto Actual: $${estado.montoActual}",
                    style = MaterialTheme.typography.headlineSmall
                )
                // Límite Actual
                Text(
                    text = "Limite: $${estado.limite}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Botones en fila horizontal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.handleIntent(GastosIntent.DecrementarGasto) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("- \$10")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { viewModel.handleIntent(GastosIntent.IncrementarGasto) },
                modifier = Modifier.weight(1f)
            ) {
                Text("+ \$10")
            }
        }

        // Mensaje de error (solo aparece si no es null)
        if (estado.mensajeError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = estado.mensajeError!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
