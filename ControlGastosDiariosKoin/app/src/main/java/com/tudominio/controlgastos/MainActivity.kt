package com.tudominio.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tudominio.controlgastos.data.Gasto
import com.tudominio.controlgastos.ui.GastosIntent
import com.tudominio.controlgastos.ui.GastosViewModel
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
                    PantallaGastos()
                }
            }
        }
    }
}

@Composable
fun PantallaGastos() {
    // Ya no usamos viewModel() sino koinViewModel():
    // Koin se encarga de crear el ViewModel e inyectar el repositorio
    val viewModel: GastosViewModel = koinViewModel()
    val estado by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Control de Gastos Diarios",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de carga
        if (estado.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tarjeta con resumen de gastos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gasto actual: $${estado.montoActual}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Total acumulado: $${estado.totalGastos}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Límite: $${estado.limite}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (estado.mensajeError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = estado.mensajeError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.handleIntent(GastosIntent.DecrementarGasto) }
            ) {
                Text("- \$10")
            }
            Button(
                onClick = { viewModel.handleIntent(GastosIntent.IncrementarGasto) }
            ) {
                Text("+ \$10")
            }
            Button(
                onClick = {
                    viewModel.handleIntent(
                        GastosIntent.AgregarGasto(10.0, "Gasto rápido")
                    )
                }
            ) {
                Text("Agregar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Título de la lista
        Text(
            text = "Historial de gastos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de gastos (LazyColumn = RecyclerView en Compose)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(estado.listaGastos) { gasto ->
                TarjetaGasto(
                    gasto = gasto,
                    onEliminar = {
                        viewModel.handleIntent(GastosIntent.EliminarGasto(gasto.id))
                    }
                )
            }
        }
    }
}

@Composable
fun TarjetaGasto(
    gasto: Gasto,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Podríamos agregar acción de editar aquí */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = gasto.descripcion,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${gasto.fecha}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "$${gasto.monto}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar gasto"
                )
            }
        }
    }
}
