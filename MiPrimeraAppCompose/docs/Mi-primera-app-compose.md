Laboratorio: "Mi primera app con Compose"
Tema: Jetpack Compose, estado, layouts y Material Design 3
Requisito
Estado
Android Studio Koala (2024.1.1) o superior instalado
☐
JDK 17 o superior configurado
☐
SDK Android API 34 o superior instalado
☐
Emulador Android configurado o dispositivo físico
☐
Conocimientos de Kotlin
☐
Entorno a utilizar: Android Studio (no Kotlin Playground). Esta app debe, necesariamente correr
en un emulador/dispositivo real.

Paso 1: Crear el proyecto
1.1 Abrir Android Studio
Hacer clic en "New Project"

1.2 Seleccionar plantilla
Seleccionar "Empty Activity (Compose)"

1.3 Configurar el proyecto
Campo
Name
Package name
Save location
Minimum SDK
Language

Valor a ingresar
MiPrimeraAppCompose
com.tudominio.miprimeraappcompose

Dejar por defecto o elegir tu carpeta
API 24 (Android 7.0)
Kotlin

1.4 Finalizar
Hacer clic en "Finish" y esperar a que el proyecto se construya
Configurar Gradle (Module):
[versions]
agp = "8.6.0"
kotlin = "1.9.0"
# AJUSTA estas tres para compatibilidad con API 34:
coreKtx = "1.13.1"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
# Estas están perfectas:
lifecycleRuntimeKtx = "2.7.0"
activityCompose = "1.8.2"
composeBom = "2024.04.01"

Verificación: La estructura del proyecto debe verse así:
MiPrimeraAppCompose/
├── app/
│
├── src/main/java/.../MainActivity.kt
│
├── src/main/res/
│
└── build.gradle.kts
├── gradle/
└── build.gradle.kts

Paso 2: Explorar el código generado
2.1 Abrir MainActivity.kt
Buscar el archivo en app/src/main/java/com/tudominio/miprimeraappcompose/MainActivity.kt

2.2 Analizar el código existente
class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

setContent {
MiPrimeraAppComposeTheme {
Surface(
modifier = Modifier.fillMaxSize(),
color = MaterialTheme.colorScheme.background
) {
Greeting("Android")
}
}
}
}
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
Text(
text = "Hello $name!",
modifier = modifier
)
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
MiPrimeraAppComposeTheme {
Greeting("Android")
}
}

Explicación:
•

setContent { } → Define la UI de la actividad

•

MiPrimeraAppComposeTheme → Tema Material Design 3

•

Surface → Contenedor con fondo

•

@Composable → Función que define UI

•

@Preview → Vista previa en el editor (sin ejecutar)

2.3 Ejecutar la app
1. Seleccionar el emulador o dispositivo
2. Presionar el botón Run ▶️
Resultado esperado: Pantalla con texto "Hello Android!"

Paso 3: Crear un contador simple
3.1 Modificar el contenido de setContent
Reemplazar el contenido dentro de setContent { }:
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

3.2 Crear la función PantallaContador()
Agregar después de la función Greeting:
@Composable
fun PantallaContador() {
// Estado: el contador se mantiene entre recomposiciones
var contador by remember { mutableStateOf(0) }
// Layout vertical
Column(
modifier = Modifier
.fillMaxSize()
.padding(16.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.Center
) {
// Texto del contador
Text(
text = "Contador: $contador",
style = MaterialTheme.typography.displayMedium
)
// Espaciador
Spacer(modifier = Modifier.height(16.dp))
// Botones en fila horizontal
Row(
horizontalArrangement = Arrangement.spacedBy(16.dp)
) {
Button(
onClick = { contador-- }
) {
Text("-1")
}
Button(
onClick = { contador++ }
) {
Text("+1")
}
}
}
}

3.3 Agregar imports necesarios
Si hay errores, agregar al inicio del archivo:
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

3.4 Ejecutar la app

Presionar Run ▶️
Resultado esperado:
• Pantalla con texto "Contador: 0"
• Botones "-1" y "+1"
• Al presionar "+1", el número aumenta
• Al presionar "-1", el número disminuye

Paso 4: Mejorar la UI con Material Design
4.1 Agregar un título y mejorar el diseño
Reemplazar la función PantallaContador():
@Composable
fun PantallaContador() {
var contador by remember { mutableStateOf(0) }
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
// Contenido principal con el padding de la AppBar
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
onClick = { contador-- },
modifier = Modifier.weight(1f),
colors = ButtonDefaults.buttonColors(
containerColor = MaterialTheme.colorScheme.error
)

) {
Text("-1")
}
Spacer(modifier = Modifier.width(16.dp))
Button(
onClick = { contador++ },
modifier = Modifier.weight(1f)
) {
Text("+1")
}
}
Spacer(modifier = Modifier.height(16.dp))
// Botón de reset
Button(
onClick = { contador = 0 },
modifier = Modifier.fillMaxWidth()
) {
Text("Reiniciar")
}
}
}
}

4.2 Agregar imports faltantes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

4.3 Ejecutar la app

Presionar Run ▶️
Resultado esperado:
• AppBar con título "Mi Primera App Compose"
• Tarjeta que muestra el contador con estilo
• Botón "-1" rojo, botón "+1" normal
• Botón "Reiniciar" que vuelve el contador a 0

Paso 5: Agregar funcionalidades adicionales
5.1 Agregar límites al contador
Modificar los onClick de los botones:
Button(
onClick = {
if (contador > -10) contador-},
// ... resto igual
) {
Text("-1")
}
Button(
onClick = {
if (contador < 10) contador++
},
// ... resto igual
) {
Text("+1")
}

// Límite inferior -10

// Límite superior 10

5.2 Agregar mensaje cuando se alcanza el límite
Agregar un nuevo estado para el mensaje:
var contador by remember { mutableStateOf(0) }
var mensaje by remember { mutableStateOf<String?>(null) }

Modificar los onClick:

Button(
onClick = {
if (contador > -10) {
contador-mensaje = null
} else {
mensaje = "¡Límite mínimo alcanzado (-10)!"
}
}
) {
Text("-1")
}
Button(
onClick = {
if (contador < 10) {
contador++
mensaje = null
} else {
mensaje = "¡Límite máximo alcanzado (10)!"
}
}
) {
Text("+1")
}
Button(
onClick = {
contador = 0
mensaje = null
}
) {
Text("Reiniciar")
}

Agregar el mensaje debajo de los botones:
// Mensaje de límite
if (mensaje != null) {
Spacer(modifier = Modifier.height(16.dp))
Text(
text = mensaje!!,
color = MaterialTheme.colorScheme.error,
style = MaterialTheme.typography.bodyMedium
)
}

5.3 Agregar historial de cambios (desafío)
Agregar estado para el historial:
var contador by remember { mutableStateOf(0) }
var historial by remember { mutableStateOf(listOf<Int>()) }

Actualizar en cada cambio:
kotlin

Button(
onClick = {
if (contador < 10) {
contador++
historial = historial + contador
mensaje = null
} else {
mensaje = "¡Límite máximo alcanzado (10)!"
}
}
) {
Text("+1")
}

Mostrar el historial:

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

Paso 6: Código completo final a presentar
6.1 Ejecutar la app final
Presionar Run ▶️
Verificación final de funcionamiento:
• La app tiene AppBar con título
• La tarjeta muestra el contador correctamente
• Los botones -1 y +1 funcionan
• El contador no baja de -10
• El contador no sube de 10
• Aparece mensaje al alcanzar límites
• El botón Reiniciar funciona
• Los colores siguen el tema Material Design

Posibles errores y soluciones
Error
Unresolved reference:
mutableStateOf
Unresolved reference:
remember
Unresolved reference:
Scaffold

El contador no se actualiza
La UI no se ve como esperas
Preview no se muestra
El emulador no arranca

Posible causa
Import faltante

Solución
Agregar import androidx.compose.runtime.*

Import faltante

Agregar import androidx.compose.runtime.*

Dependencia faltante
Olvidaste by en by

Verificar que material3 esté
en build.gradle.kts
Usar var contador by remember {

remember

mutableStateOf(0) }

Modifier en orden
incorrecto
@Preview sin tema
RAM insuficiente

Revisar el orden de los modificadores
Agregar @Preview(showBackground = true)
Reducir RAM del emulador a 1GB

