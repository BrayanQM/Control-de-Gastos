ASIGNACIÓN DE ACCESO Y VERIFICACIÓN DE PANTALLAS
App: Control de Gastos Diarios
Tema: Navegación, Autenticación y Protección de rutas
El código proporcionado es acumulativo y parte del código de la sesión anterior: Repositorio de
usuarios e inyección de dependencias

Paso 1: Agregar dependencias
1.1 Abrir el archivo gradle/libs.versions.toml
Agregar las nuevas versiones al final del bloque [versions]:
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
koin = "3.5.3" # 👈 Agregado para la Anterior implementacion
# ... versiones existentes de las Partes 1 y 2 ...
navigationCompose = "2.7.7"
datastore = "1.1.1"

1.2 Agregar las librerías al bloque [libraries]

[libraries]
# ... librerías existentes ...
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", ver
sion.ref = "navigationCompose" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences"
, version.ref = "datastore" }

1.3 Abrir el archivo app/build.gradle.kts

Agregar las nuevas dependencias en el bloque dependencies:
dependencies {
// ... dependencias existentes de las implementaciones 2 y 3 ...
// Navigation Compose
implementation(libs.androidx.navigation.compose)
// DataStore (persistencia de sesión)
implementation(libs.androidx.datastore.preferences)
}

1.4 Sincronizar el proyecto
Hacer clic en "Sync Now".
Verificación: No debe haber errores en rojo.

Paso 2: Crear el paquete de navegación
Crear un nuevo paquete para la navegación:
com.tudominio.controlgastos/
├── data/
← (existe)
├── di/
← (existe)
├── navigation/ ← NUEVO
├── ui/
← (existe)
├── ControlGastosApplication.kt
└── MainActivity.kt

Paso 3: Definir las rutas tipadas
3.1 Crear el archivo Pantallas.kt
Botón derecho sobre el paquete navigation → New → Kotlin Class/File → escribir Pantallas →
seleccionar File → Enter

3.2 Escribir el código de las rutas
package com.tudominio.controlgastos.navigation
// Rutas tipadas usando sealed class
// Type-Safe Navigation: errores en compilación, no en runtime
sealed class Pantalla(val route: String) {
// Pantalla de login (pública, sin autenticación)
object Login : Pantalla("login")
// Pantalla principal de gastos (requiere autenticación)
object Gastos : Pantalla("gastos")
// Pantalla de configuración (requiere autenticación)
object Configuracion : Pantalla("configuracion")
}
// Constantes para usar en el NavHost
object Rutas {
const val LOGIN = "login"
const val GASTOS = "gastos"
const val CONFIGURACION = "configuracion"
}

Explicación:
•

sealed class Pantalla → Todas las pantallas posibles de la app

•

route: String → Identificador único para cada pantalla

•

object Login → Pantalla sin parámetros

•

object Gastos → Pantalla principal

•

object Configuracion → Pantalla adicional (opcional)

•

object Rutas → Constantes para evitar errores de tipeo

Paso 4: Implementar DataStore para sesión
4.1 Crear el archivo SesionManager.kt
Botón derecho sobre el paquete data → New → Kotlin Class/File → escribir SesionManager →
seleccionar Class → Enter

4.2 Escribir el código del gestor de sesión
package com.tudominio.controlgastos.data
//Cargar manualmente
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
// que se importen con el código
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
// Extensión de Context para crear el DataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")
class SesionManager(private val context: Context) {
// Claves para guardar datos en DataStore
companion object {
private val KEY_ESTA_AUTENTICADO = booleanPreferencesKey("esta_autenticado")
private val KEY_USUARIO_ID = stringPreferencesKey("usuario_id")
private val KEY_USUARIO_NOMBRE = stringPreferencesKey("usuario_nombre")
private val KEY_TOKEN = stringPreferencesKey("token")
}
// Flow que indica si el usuario está autenticado
val estaAutenticado: Flow<Boolean> = context.dataStore.data
.map { preferences ->
preferences[KEY_ESTA_AUTENTICADO] ?: false
}
// Flow con el ID del usuario (nullable)
val usuarioId: Flow<String?> = context.dataStore.data
.map { preferences ->
preferences[KEY_USUARIO_ID]
}
// Flow con el nombre del usuario (nullable)
val usuarioNombre: Flow<String?> = context.dataStore.data
.map { preferences ->
preferences[KEY_USUARIO_NOMBRE]
}
// Guardar sesión después de login exitoso
suspend fun guardarSesion(usuarioId: String, usuarioNombre: String, token: String) {
context.dataStore.edit { preferences ->
preferences[KEY_ESTA_AUTENTICADO] = true
preferences[KEY_USUARIO_ID] = usuarioId
preferences[KEY_USUARIO_NOMBRE] = usuarioNombre
preferences[KEY_TOKEN] = token
}
}
// Cerrar sesión (borrar todos los datos)
suspend fun cerrarSesion() {
context.dataStore.edit { preferences ->
preferences.clear() // Borra todas las claves
}
}
// Obtener token de forma síncrona (para usar en llamadas a API)
suspend fun obtenerToken(): String? {
var token: String? = null
context.dataStore.data
.map { preferences -> preferences[KEY_TOKEN] }
.collect { token = it}
return token
}
}

Explicación línea por línea:
•

preferencesDataStore(name = "sesion") → Crea un DataStore con nombre "sesion"

•

booleanPreferencesKey() → Define una clave para guardar booleanos

•

Flow<Boolean> → Emite automáticamente cuando cambia el estado de autenticación

•

suspend fun guardarSesion() → Guarda datos (debe llamarse desde coroutine)

•

preferences.clear() → Borra toda la sesión (cierre de sesión)

Paso 5: Actualizar el ViewModel para manejar autenticación
5.1 Modificar GastosViewModel.kt
Abrir el archivo ui/GastosViewModel.kt y AGREGAR nuevas propiedades y métodos:
import androidx.lifecycle.viewModelScope
import com.tudominio.controlgastos.data.GastosRepositorio
import com.tudominio.controlgastos.data.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class GastosViewModel(
// NUEVO: recibimos también el SesionManager
private val repositorio: GastosRepositorio, // 👈Inyección por constructor lista EVO
private val sesionManager: SesionManager // ← Dependencia de sesion inyectada en la anteri
or implementacion
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
if (autenticado) {
// Obtener nombre del usuario
sesionManager.usuarioNombre.collect { nombre ->
_usuarioNombre.value = nombre ?: "Usuario"
}
}
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
// ... Intents existentes de implementación 2 y 3 ...
is GastosIntent.IncrementarGasto -> incrementarGasto()
is GastosIntent.DecrementarGasto -> decrementarGasto()
is GastosIntent.EstablecerLimite -> establecerLimite(intent.nuevoLimite)
is GastosIntent.CargarGastos -> cargarGastos()

is GastosIntent.AgregarGasto -> agregarGasto(intent.monto, intent.descripcion)
is GastosIntent.EliminarGasto -> eliminarGasto(intent.id)
// NUEVOS Intents para autenticación
is GastosIntent.Login -> login(intent.email, intent.password)
is GastosIntent.Logout -> logout()
}
}
// PROCESAMIENTO ASINCRONO DE LA SESION 2 <- aquí (cargarGastos, agregarGasto, eliminarGast
o)
// ========== NUEVOS MÉTODOS DE AUTENTICACIÓN ==========
private fun login(email: String, password: String) {
viewModelScope.launch {
_state.update { it.copy(isLoading = true) }
// SIMULACIÓN: en producción, llamar a una API real o Firebase
// Por ahora, validamos credenciales fijas para demostración
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
// Necesario para el delay de simulación
import kotlinx.coroutines.delay

5.2 Agregar los nuevos Intents
Abrir ui/GastosIntent.kt y agregar:

// NUEVOS Intents para autenticación
data class Login(val email: String, val password: String) : GastosIntent()
object Logout : GastosIntent()
// 👈 Nuevos flujos de la Sesion 3 <- ESTE
class Login(val email: String, val password: String) : GastosIntent()
data object Logout : GastosIntent()

Paso 6: Actualizar Koin para incluir SesionManager
6.1 Modificar AppModule.kt
Abrir el archivo di/AppModule.kt y actualizar:
package com.tudominio.controlgastos.di
val appModule = module {
single { SesionManager(androidContext()) }
//Mantiene una única instancia viva de repositorio en memoria
single<GastosRepositorio> { GastosRepositorioSimulado() }
//Resuelve automaticamente ambas dependencias
// por constructor de forma secuencial
viewModel {
GastosViewModel(
repositorio = get(),
sesionManager = get()
)
}
}

Paso 7: Crear la pantalla de Login
7.1 Crear el archivo PantallaLogin.kt
Botón derecho sobre el paquete ui → New → Kotlin Class/File → escribir PantallaLogin →
seleccionar File → Enter

7.2 Escribir el código de la pantalla de login
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
Text(text = "🔐 Credenciales de prueba:", style =
MaterialTheme.typography.bodySmall)
Text(text = "Email: demo@controlgastos.com", style =
MaterialTheme.typography.bodySmall)
Text(text = "Contraseña: 123456", style = MaterialTheme.typography.bodySmall)
}
}
}
}

Paso 8: Crear la pantalla de Gastos
8.1 Crear el archivo PantallaGastos.kt
Botón derecho sobre el paquete ui → New → Kotlin Class/File → escribir PantallaGastos →
seleccionar File → Enter

8.2 Escribir el código de la pantalla de gastos
@Composable
fun PantallaGastos(
viewModel: GastosViewModel,
// 👈 Recibe el ViewModel por parámetro (Paso 9.2)
alIrAConfiguracion: () -> Unit // 👈 CAMBIADO: Agregamos el parámetro para que conecte con
el NavHost
) {
val estado by viewModel.state.collectAsState()
val nombreUsuario by viewModel.usuarioNombre.collectAsState()
Column(
modifier = Modifier
.fillMaxSize()
.padding(16.dp),
horizontalAlignment = Alignment.CenterHorizontally
) {
// Encabezado dinámico con Logout (Paso 8.1)
Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceBetween,
verticalAlignment = Alignment.CenterVertically
) {
Column(modifier = Modifier.weight(1f)) { // 👈 CAMBIADO: Evita que los botones
aplasten el texto
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
// 👇 CAMBIADO: Metemos los dos botones en una fila horizontal
Row(verticalAlignment = Alignment.CenterVertically) {
// ⚙️ NUEVO BOTÓN DE CONFIGURACIÓN
IconButton(onClick = alIrAConfiguracion) {
Icon(
imageVector = Icons.Default.Settings, // 👈 Nota: Asegúrate de
importar androidx.compose.material.icons.filled.Settings
contentDescription = "Ir a Configuración"
)
}
// Botón de cerrar sesión original
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
Button(onClick = { viewModel.handleIntent(GastosIntent.DecrementarGasto) }) {
Text("- \$10")
}
Button(onClick = { viewModel.handleIntent(GastosIntent.IncrementarGasto) }) {
Text("+ \$10")
}
Button(onClick = { viewModel.handleIntent(GastosIntent.AgregarGasto(10.0, "Gasto
rápido")) }) {
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

Paso 9: Configurar el NavHost con navegación protegida
9.1 Modificar MainActivity.kt completamente
Reemplazar todo el contenido de MainActivity.kt:
package com.tudominio.controlgastos
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import com.tudominio.controlgastos.navigation.Pantalla
import com.tudominio.controlgastos.ui.PantallaGastos
import com.tudominio.controlgastos.ui.PantallaLogin
import com.tudominio.controlgastos.ui.theme.ControlGastosDiariosTheme
class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
BorrarBorrarTheme {
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

// Ruta protegida: Valida tokens y credenciales
composable(Pantalla.Gastos.route) {
if (!estaAutenticado) {
LaunchedEffect(Unit) {
navController.navigate(Pantalla.Login.route) {
popUpTo(Pantalla.Gastos.route) { inclusive = true }
}
}
} else {
// 👇 LUGAR EXACTO DE LA MODIFICACIÓN
PantallaGastos(
viewModel = viewModel,
alIrAConfiguracion = {
navController.navigate(Pantalla.Configuracion.route)
} // 👈 Agregamos esta función aquí para darle la orden de viajar
)
}
}
// Configuracion Adicional de Ruta Protegida
composable(Pantalla.Configuracion.route) {
if (!estaAutenticado) {
LaunchedEffect(Unit) {
navController.navigate(Pantalla.Login.route) {
popUpTo(Pantalla.Configuracion.route) { inclusive = true}
}
}
} else {
Text("Pantalla de Configuración - En Construcción")
}
}
}
}

9.2 Asegurar que PantallaGastos recibe el ViewModel
Modificar la función PantallaGastos para que reciba el ViewModel como parámetro:
@Composable
fun PantallaGastos(viewModel: GastosViewModel) {
// El resto del código igual, pero sin crear el ViewModel aquí
val estado by viewModel.state.collectAsState()
// ... resto del código
}

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
Abrir la app (primera vez)
Intentar acceder sin login
Ingresar email demo@controlgastos.com y
contraseña 123456
Presionar "Cerrar sesión"
Cerrar la app y volver a abrir
Presionar botón "Atrás" en gastos
o

Estructura final del repositorio:
ControlGastosDiarios/
├── app/
│ ├── src/main/java/.../
│ │ ├── data/
│ │ │ ├── Gasto.kt
│ │ │ ├── GastosRepositorio.kt
│ │ │ └── SesionManager.kt
← NUEVO
│ │ ├── di/
│ │ │ └── AppModule.kt
│ │ ├── navigation/
← NUEVO
│ │ │ └── Pantallas.kt
│ │ ├── ui/
│ │ │ ├── GastosState.kt
│ │ │ ├── GastosIntent.kt
│ │ │ ├── GastosViewModel.kt
│ │ │ ├── PantallaLogin.kt
← NUEVO
│ │ │ └── PantallaGastos.kt
(antes MainActivity)
│ │ ├── ControlGastosApplication.kt
│ │ └── MainActivity.kt
│ └── ...
└── README.md (con capturas de pantalla)

Resultado esperado
Muestra pantalla de Login
No se puede ver la pantalla de gastos
Login exitoso, navega a pantalla de gastos
Vuelve a pantalla de login, los datos se borran
Si había sesión guardada, va directo a gastos
Sale de la app (no vuelve al login)

