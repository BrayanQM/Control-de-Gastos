package com.tudominio.controlgastos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tudominio.controlgastos.data.Gasto

@Composable
fun PantallaGastos(
    viewModel: GastosViewModel,
    alIrAConfiguracion: () -> Unit
) {
    // escuha la inyeccion
    val estado by viewModel.state.collectAsState()
    val nombreUsuario by viewModel.usuarioNombre.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado dinámico con Logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Control de Gastos Diarios",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Bienvenido, $nombreUsuario",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            // llama Logout presionar button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botón de configuración
                IconButton(onClick = alIrAConfiguracion) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ir a Configuración"
                    )
                }
                // Botón de cerrar sesión
                IconButton(onClick = { viewModel.handleIntent(GastosIntent.Logout) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar sesión"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (estado.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                    text = "Gasto actual: \$${estado.montoActual}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Total acumulado: \$${estado.totalGastos}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Límite: \$${estado.limite}",
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // llama agregar gastos al presionar button
            Button(onClick = { viewModel.handleIntent(GastosIntent.DecrementarGasto) }) {
                Text("- \$10")
            }
            Button(onClick = { viewModel.handleIntent(GastosIntent.IncrementarGasto) }) {
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

        Text(
            text = "Historial de gastos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

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
fun TarjetaGasto(gasto: Gasto, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = gasto.descripcion, style = MaterialTheme.typography.titleMedium)
                Text(text = gasto.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "\$${gasto.monto}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onEliminar) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar gasto")
            }
        }
    }
}
