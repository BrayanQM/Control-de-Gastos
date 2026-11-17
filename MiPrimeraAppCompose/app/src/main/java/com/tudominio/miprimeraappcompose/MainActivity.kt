package com.tudominio.miprimeraappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tudominio.miprimeraappcompose.ui.theme.MiPrimeraAppComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiPrimeraAppComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaContador()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContador() {
    // Estado: se mantiene entre recomposiciones
    var contador by remember { mutableStateOf(0) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var historial by remember { mutableStateOf(listOf<Int>()) }

    // Scaffold proporciona estructura base (AppBar, FAB, etc.)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Primera App Compose") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tarjeta que muestra el contador
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
                    Text(
                        text = "Valor actual",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$contador",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (contador > -10) {
                            contador--
                            historial = historial + contador
                            mensaje = null
                        } else {
                            mensaje = "¡Límite mínimo alcanzado (-10)!"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("-1")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (contador < 10) {
                            contador++
                            historial = historial + contador
                            mensaje = null
                        } else {
                            mensaje = "¡Límite máximo alcanzado (10)!"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de reset
            Button(
                onClick = {
                    contador = 0
                    mensaje = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reiniciar")
            }

            // Mensaje de límite
            if (mensaje != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Historial
            if (historial.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Historial:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = historial.joinToString(" → "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaContadorPreview() {
    MiPrimeraAppComposeTheme {
        PantallaContador()
    }
}
