package com.tudominio.controlgastos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tudominio.controlgastos.navigation.Pantalla

@Composable
fun PantallaLogin(
    navController: NavController,
    viewModel: GastosViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val estado by viewModel.state.collectAsState()
    val estaAutenticado by viewModel.estaAutenticado.collectAsState()

    LaunchedEffect(estaAutenticado) {
        if (estaAutenticado) {
            navController.navigate(Pantalla.Gastos.route) {
                popUpTo(Pantalla.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Control de Gastos",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("demo@controlgastos.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = estado.mensajeError != null && email.isNotEmpty()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            placeholder = { Text("123456") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = estado.mensajeError != null && password.isNotEmpty()
        )

        if (estado.mensajeError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = estado.mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (estado.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.handleIntent(GastosIntent.Login(email, password)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Iniciar sesión")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Credenciales de prueba:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Email: demo@controlgastos.com",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Contraseña: 123456",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
