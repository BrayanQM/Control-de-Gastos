REPOSITORIO DE USUARIOS E INYECCIÓN DE DEPENDENCIAS
App: Control de Gastos Diarios
Tema: Inyección de dependencias con Koin
Configuración Inicial en IDE: Configuración INICIAL.pdf
Paso 1: Agregar dependencia de Koin
1.1 Abrir el archivo gradle/libs.versions.toml
Agregar la versión de Koin al final del bloque [versions]:
[versions]
agp = "8.6.0"
kotlin = "1.9.24"
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.8.2"
activityCompose = "1.9.0"
composeBom = "2024.06.00"
koin = "3.5.3" # 👈 Agregado para la Sesión 2

1.2 Agregar las librerías de Koin al bloque [libraries]
[libraries]
# ... librerías existentes de la Sesión 1 ...
# 👈 SOLUCIÓN PREVENTIVA: Librería para disponer de Icons.Default.Delete
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name =
"material-icons-extended" }
# 👈 LIBRERÍAS KOIN SOLICITADAS EN PASO 1.2
koin-android = { group = "io.insert-koin", name = "koin-android", version.ref = "koin" }
koin-compose = { group = "io.insert-koin", name = "koin-androidx-compose", version.ref =
"koin" }
koin-core = { group = "io.insert-koin", name = "koin-core", version.ref = "koin" }

1.3 Abrir el archivo app/build.gradle.kts
Agregar las dependencias de Koin en el bloque dependencies:
dependencies {
// ... dependencias existentes de la Sesión 1 ...

implementation(libs.androidx.lifecycle.viewmodel.compose) // 👈 Agregado de forma segura
// Iconos extendidos añadidos para compilar TarjetaGasto sin errores
implementation(libs.androidx.compose.material.icons.extended)
// Koin: Inyección de dependencias (Paso 1.3)
implementation(libs.koin.android)
implementation(libs.koin.compose)
implementation(libs.koin.core)
}

// Verificar en la sección
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
kotlinOptions {
/////
jvmTarget = "17"
}
buildFeatures {
compose = true
}
composeOptions {
kotlinCompilerExtensionVersion = "1.5.14"
}

1.4 Sincronizar el proyecto
Hacer clic en "Sync Now" (aparece en la barra superior después de modificar los archivos).
Verificación: No debe haber errores en rojo en la consola de Build.

Paso 2: Crear los paquetes necesarios
En el proyecto, crear la siguiente estructura de paquetes (carpetas):
com.tudominio.controlgastos/
├── data/
← NUEVO (para modelos y repositorios)
├── di/
← NUEVO (para inyección de dependencias)
├── ui/
← (ya existe de la Sesión 1)
└── MainActivity.kt

Paso 3: Crear el modelo de datos
3.1 Crear el archivo Gasto.kt
Botón derecho sobre el paquete data → New → Kotlin Class/File → escribir Gasto → seleccionar File →
Enter

3.2 Escribir el código del modelo
package com.tudominio.controlgastos.data
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
data class Gasto(
val id: Int,
val monto: Double,
val descripcion: String,
val fecha: String
) {
companion object {
fun obtenerFechaActual(): String {
return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
.format(Date())
}
}
}

Explicación:
• id: Identificador único para cada gasto
• monto: Cantidad de dinero gastado
• descripcion: Breve texto explicativo (ej: "Café", "Almuerzo")
• fecha: Cuándo ocurrió el gasto
• companion object: Método de utilidad para obtener la fecha actual

Paso 4: Crear el repositorio
4.1 Crear la interfaz del repositorio
En el paquete data, crear un archivo GastosRepositorio.kt:
package com.tudominio.controlgastos.data
interface GastosRepositorio {
fun obtenerGastos(): List<Gasto>
fun guardarGasto(monto: Double, descripcion: String)
fun obtenerTotalGastos(): Double
fun eliminarGasto(id: Int)
}

Explicación de la interfaz:

•

obtenerGastos() → Devuelve todos los gastos guardados

•

guardarGasto() → Agrega un nuevo gasto

•

obtenerTotalGastos() → Calcula la suma de todos los montos

•

eliminarGasto() → Borra un gasto por su ID

4.2 Crear la implementación simulada del repositorio
En el mismo archivo GastosRepositorio.kt, agregar la implementación:
class GastosRepositorioSimulado : GastosRepositorio {
// Lista mutable que simula una base de datos en memoria
private val gastos = mutableListOf<Gasto>()
private var contadorId = 1
init {
// Datos de ejemplo al iniciar la app
gastos.add(Gasto(contadorId++, 25.50, "Café y empanadas", Gasto.obtenerFechaActual()))
gastos.add(Gasto(contadorId++, 45.00, "Almuerzo ejecutivo", Gasto.obtenerFechaActual()))
gastos.add(Gasto(contadorId++, 120.00, "Supermercado", Gasto.obtenerFechaActual()))
gastos.add(Gasto(contadorId++, 15.00, "Taxi", Gasto.obtenerFechaActual()))
}
override fun obtenerGastos(): List<Gasto> {
// Simula una pequeña demora de red (útil para ver loading states)
Thread.sleep(1000)
return gastos.toList() // Retorna una copia para evitar modificaciones externas
}
override fun guardarGasto(monto: Double, descripcion: String) {
val nuevoGasto = Gasto(
id = contadorId++,
monto = monto,
descripcion = descripcion,
fecha = Gasto.obtenerFechaActual()
)
gastos.add(0, nuevoGasto) // Lo agrega al principio (más reciente primero)
}
override fun obtenerTotalGastos(): Double {
return gastos.sumOf { it.monto }
}
override fun eliminarGasto(id: Int) {
gastos.removeAll { it.id == id }
}
}

Explicación línea por línea:
•

mutableListOf<Gasto>() → Lista que puede crecer y encogerse (simula una BD)

•

contadorId → Genera IDs únicos automáticamente

•

init { } → Bloque que se ejecuta al crear el repositorio (carga datos de ejemplo)

•

Thread.sleep(100) → Simula latencia de red (útil para pruebas)

•

toList() → Retorna una copia inmutable para seguridad

Refactorización de la Capa de Presentación (MVI)

Paso 5: Actualizar el State (Variables de Estado)
5.1 Modificar GastosState.kt
Abrir el archivo ui/GastosState.kt y agregar las tres nuevas propiedades que controlarán la lista
dinámica y los loaders de la UI:

package com.tudominio.controlgastos.ui
import com.tudominio.controlgastos.data.Gasto
data class GastosState(
val montoActual: Double = 0.0,
val limite: Double = 500.0,
val mensajeError: String? = null,
// NUEVAS PROPIEDADES para la Sesión 2
val listaGastos: List<Gasto> = emptyList(),
val totalGastos: Double = 0.0,
val isLoading: Boolean = false
)

Qué cambió:
Propiedad nueva

Tipo

listaGastos

List<Gasto>

totalGastos

Double

isLoading

Boolean

//importación del nuevo modelo

// Lista de gastos guardados
// Suma de todos los gastos
// Indica si está cargando

Propósito
Muestra todos los gastos guardados
Suma acumulada de todos los montos
Muestra un indicador de carga mientras se obtienen datos

Paso 6: Actualizar los Intents (Eventos de la UI)
6.1 Modificar GastosIntent.kt
Abrir el archivo ui/GastosIntent.kt y agregar nuevos Intents:
package com.tudominio.controlgastos.ui
sealed class GastosIntent {
// Intents existentes de la Sesión 1
object IncrementarGasto : GastosIntent()
object DecrementarGasto : GastosIntent()
data class EstablecerLimite(val nuevoLimite: Double) : GastosIntent()
// NUEVOS Intents para la Sesión 2 (repositorio)
object CargarGastos : GastosIntent()
// Cargar lista desde el repositorio
data class AgregarGasto(val monto: Double, val descripcion: String) : GastosIntent()
// Guardar nuevo gasto
data class EliminarGasto(val id: Int) : GastosIntent()
// Eliminar un gasto existente
}

Qué cambió:
Intent nuevo
AgregarGasto

Datos que lleva
Ninguno
monto, descripcion

EliminarGasto

id

CargarGastos

Propósito
Solicita al ViewModel que obtenga los gastos del repositorio
Guarda un nuevo gasto en el repositorio
Borra un gasto específico por su identificador

Paso 7: REFACTORIZAR el controlador ViewModel
7.1 Modificar GastosViewModel.kt
Abrir el archivo ui/GastosViewModel.kt y REEMPLAZAR todo el contenido:
package com.tudominio.controlgastos.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tudominio.controlgastos.data.GastosRepositorio
import com.tudominio.controlgastos.data.GastosRepositorioSimulado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// ⚠️ CAMBIO IMPORTANTE: El repositorio ahora se RECIBE por constructor
// Ya no se crea dentro del ViewModel

class GastosViewModel(
private val repositorio: GastosRepositorio
) : ViewModel() {

// ← INYECTADA desde afuera por constructor

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
// NUEVOS Intents de la Sesión 2
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

// ========== NUEVOS MÉTODOS (Sesión 2) ==========
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

Cambios clave explicados:
Antes (Sesión 1)

Ahora (Sesión 2)

class GastosViewModel : ViewModel()

class GastosViewModel(private val repositorio:
GastosRepositorio)

El ViewModel creaba sus propios
datos
Sin datos persistentes
Sin operaciones asíncronas

El ViewModel recibe el repositorio por constructor
Los datos se guardan en el repositorio
Uso de viewModelScope.launch para operaciones en segundo
plano

Paso 8: Configurar Contenedor de inyección Koin
8.1 Crear el módulo de inyección Koin
En el paquete di, crear un archivo AppModule.kt:
package com.tudominio.controlgastos.di
import com.tudominio.controlgastos.data.GastosRepositorio
import com.tudominio.controlgastos.data.GastosRepositorioSimulado
import com.tudominio.controlgastos.ui.GastosViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
// single = UNA SOLA instancia para toda la aplicación
// <GastosRepositorio> = le decimos a Koin qué interfaz estamos implementando
single<GastosRepositorio> {
GastosRepositorioSimulado() // Cómo se crea
}
// viewModel = crea ViewModels respetando su ciclo de vida
// get() = pídele a Koin que me dé la dependencia que necesita este ViewModel
viewModel {
GastosViewModel(get()) // get() obtiene el repositorio que definimos arriba
}
}

Explicación del módulo:
• single { } → Koin crea una sola instancia en memoria y la reutiliza donde sea necesario
• viewModel { } → Koin crea ViewModels y los asocia al ciclo de vida de Android
• get() → Koin busca automáticamente la dependencia del tipo correcto

8.2 Crear la clase Application
En el paquete raíz (com.tudominio.controlgastos), crear un archivo ControlGastosApplication.kt:
package com.tudominio.controlgastos
import android.app.Application
import com.tudominio.controlgastos.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
class ControlGastosApplication : Application() {
override fun onCreate() {
super.onCreate()
// Iniciar Koin con nuestra configuración
startKoin {
// Contexto de Android necesario para algunas funciones
androidContext(this@ControlGastosApplication)
// Los módulos que definimos
modules(appModule)
}
}
}

8.3 Registrar la Application en el manifiesto
Abrir AndroidManifest.xml (en la carpeta app/manifests) y modificar la etiqueta <application>:
<application
android:name=".ControlGastosApplication" ← AGREGAR ESTA LÍNEA
android:allowBackup="true"
android:icon="@mipmap/ic_launcher"
android:label="@string/app_name"
android:supportsRtl="true"
android:theme="@style/Theme.ControlGastosDiarios">
<!-- El resto del manifiesto queda igual -->
</application>

Explicación: Esta línea le dice a Android que use nuestra clase ControlGastosApplication como punto
de entrada, donde iniciamos Koin.

Paso 9: Actualizar la UI
9.1 Modificar MainActivity.kt
Abrir el archivo MainActivity.kt y REEMPLAZAR todo el contenido:

package com.tudominio.controlgastos
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.tudominio.controlgastos.data.Gasto
import com.tudominio.controlgastos.ui.GastosIntent
import com.tudominio.controlgastos.ui.GastosViewModel
import com.tudominio.controlgastos.ui.theme.ControlGastosDiariosTheme
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
// ⚠️ CAMBIO IMPORTANTE: Ya no usamos viewModel() sino koinViewModel()
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

modifier = Modifier.fillMaxSize(),
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
imageVector = androidx.compose.material.icons.Icons.Default.Delete,
contentDescription = "Eliminar gasto"
)
}
}
}
}

Explicación de nuevos elementos de Compose:
Elemento
¿Qué hace?
Card
Contenedor con bordes redondeados y sombra
LazyColumn
Lista que carga elementos de forma eficiente (como RecyclerView)
items(lista)
Itera sobre la lista y crea un elemento por cada ítem
CircularProgressIndicator
Muestra un círculo giratorio (indicador de carga)
IconButton
Botón con icono en lugar de texto
Icon
Muestra un icono (en este caso, de eliminar)

Paso 10: Ejecutar y verificar
10.1 Limpiar y reconstruir
•
•

Build → Clean Project
Build → Rebuild Project

10.2 Ejecutar la app
1. Seleccionar el emulador o dispositivo
2. Presionar el botón Run

10.3 Probar todas las funcionalidades
Acción
Abrir la app
Presionar "+ $10"
Presionar "Agregar"
Presionar el ícono de eliminar
(basura)
El "Total acumulado"
El "Gasto actual"

Resultado esperado
Muestra lista de gastos de ejemplo (Café, Almuerzo, etc.)
Aumenta el "Gasto actual" y el "Total acumulado"
Agrega un nuevo gasto de $10 con descripción "Gasto
rápido"
Elimina ese gasto de la lista
Debe reflejar la suma correcta de todos los gastos
Sigue funcionando como en la Sesión 1

Estructura recomendada para el repositorio:
ControlGastosDiarios/
├── app/
│ ├── src/main/java/.../
│ │ ├── data/
│ │ │ ├── Gasto.kt
│ │ │ └── GastosRepositorio.kt
│ │ ├── di/
│ │ │ └── AppModule.kt
│ │ ├── ui/
│ │ │ ├── GastosState.kt
│ │ │ ├── GastosIntent.kt
│ │ │ └── GastosViewModel.kt
│ │ ├── ControlGastosApplication.kt
│ │ └── MainActivity.kt
│ └── ...
└── README.md (con captura de pantalla)

Conexión con la siguiente entrega
Lo que construimos hoy (repositorio con Koin) será la base para la Ultima Parte, donde agregaremos:
• Sistema de autenticación (login de usuarios)
• Navegación protegida (solo usuarios autenticados ven los gastos)
• Asignación de usuarios (cada usuario ve sus propios gastos)
• Verificaciones de interfaz (según el rol del usuario)

