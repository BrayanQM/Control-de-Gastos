Diagnóstico: ¿Por qué falla la configuración por defecto de Android
Studio Koala?
Cuando creas un proyecto nuevo "Empty Compose Activity" en Android Studio 2024.1.2 Koala
Feature Drop, las plantillas automáticas generan incompatibilidades críticas si intentas escalar el
proyecto:
1. El choque Kotlin vs Compose Compiler: Tu archivo original usa kotlin = "1.9.0" con
kotlinCompilerExtensionVersion = "1.5.1". Esta combinación genera un error
inmediato de compilación porque el compilador de Compose no entiende esa versión de
Kotlin.
2. El bloqueo de Java 8 (1.8): Las versiones modernas de androidx.lifecycle
(necesarias para StateFlow en MVI) y las futuras librerías de Koin y Navigation exigen
bytecode de Java 17. Si mantienes Java 8, Gradle fallará al intentar resolver las
dependencias de las sesiones 2 y 3.
3. Versión del BOM de Compose desactualizada: La versión 2024.04.01 limita el uso de
componentes de Material 3 avanzados que se requieren para diseñar interfaces modernas
de control de gastos.

Manual de Solución Paso a Paso
Sigue estos pasos en orden para limpiar y configurar tu proyecto base de manera óptima.

Paso 1: Configurar el Catálogo de Versiones (libs.versions.toml)
Abre el archivo gradle/libs.versions.toml y reemplaza por completo su contenido con el
siguiente bloque. Aquí actualizamos Kotlin a la versión 1.9.24 (la más estable y compatible con
Koin), elevamos el BOM de Compose y preparamos las versiones clave.
[versions]
agp = "8.6.0"
kotlin = "1.9.24"
#
Versión estable y compatible
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.8.2" #
Necesario para MVI/StateFlow
activityCompose = "1.9.0"
composeBom = "2024.06.00" #
BOM actualizado para Material 3
[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref
= "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx",
version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose",
version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref =
"composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }

Paso 2: Actualizar el archivo build.gradle.kts a nivel de Proyecto
Este archivo se queda limpio, pero es fundamental verificar que los plugins apunten a los alias
correctos que acabamos de actualizar en el archivo anterior.
Abre tu archivo build.gradle.kts (Project) de la raíz y asegúrate de que tenga exactamente
esta estructura:

kotlin

// Archivo de construcción a nivel de proyecto (Top-level)
// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
alias(libs.plugins.android.application) apply false
alias(libs.plugins.kotlin.android) apply false
}

Paso 3: Configurar el archivo build.gradle.kts a nivel de Módulo (:app)
Aquí es donde aplicamos el salto definitivo a Java 17 y ajustamos el Compose Compiler
(1.5.14) para que sea perfectamente compatible con Kotlin 1.9.24.
Abre el archivo app/build.gradle.kts e integra por completo esta configuración:
plugins {
alias(libs.plugins.android.application)
alias(libs.plugins.kotlin.android)
}
android {
namespace = "com.marco.borrarborrar"
compileSdk = 34
defaultConfig {
applicationId = "com.marco.borrarborrar"
minSdk = 24
targetSdk = 34
versionCode = 1
versionName = "1.0"
testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
vectorDrawables {
useSupportLibrary = true
}
}
buildTypes {
release {
isMinifyEnabled = false
proguardFiles(
getDefaultProguardFile("proguard-android-optimize.txt"),
"proguard-rules.pro"
)
}
}
// 1. ACTUALIZACIÓN A JAVA 17 (Obligatorio para evitar errores de bytecode en Koin y
Navigation)
compileOptions {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
// 2. OBJETIVO DE LA JVM CONFIGURADO A 17
kotlinOptions {
jvmTarget = "17"
}
buildFeatures {
compose = true
}
// 3. SINCRONIZACIÓN DE COMPILADOR (1.5.14 es el único que entiende a Kotlin 1.9.24)
composeOptions {
kotlinCompilerExtensionVersion = "1.5.14"
}
packaging {
resources {

excludes += "/META-INF/{AL2.0,LGPL2.1}"
}
}
}
dependencies {
// Implementaciones base seguras vinculadas al Version Catalog
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)
// Configuración limpia de la plataforma Compose vía BOM
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.ui)
implementation(libs.androidx.ui.graphics)
implementation(libs.androidx.ui.tooling.preview)
implementation(libs.androidx.material3)
// Pruebas (Test)
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)
// Herramientas de depuración
debugImplementation(libs.androidx.ui.tooling)
debugImplementation(libs.androidx.ui.test.manifest)
}

Paso 4: Ajustar el JDK en la interfaz de Android Studio Koala
Para evitar que la terminal de Android Studio o el proceso interno de Gradle utilicen una versión
de Java obsoleta de tu sistema operativo, debes configurar el entorno gráfico:
1. En la barra superior de Android Studio ve a:
o Windows/Linux: File ➔ Settings
o Mac: Android Studio ➔ Preferences
2. En el menú de la izquierda, navega por la ruta: Build, Execution, Deployment ➔ Build
Tools ➔ Gradle.
3. Busca el apartado Gradle JDK.
4. Despliega la lista y selecciona estrictamente la opción que dice jbr-17 (JetBrains Runtime
versión 17) o cualquier JDK 17 instalado en tu máquina.
5. Haz clic en Apply y luego en OK.

Paso 5: Sincronización final del proyecto
Para asegurar que Gradle limpie el caché viejo y aplique la arquitectura limpia para el taller:
1. En la barra de herramientas superior, haz clic en el botón del elefante con una flecha azul
(Sync Project with Gradle Files).
2. Espera a que la barra de carga finalice sin errores.
3. Si experimentas algún residuo de compilación anterior, ve al menú superior de Android
Studio: Build ➔ Clean Project y después Build ➔ Rebuild Project.
Con estos 5 pasos, tu base está blindada y lista para recibir el código de cualquier arquitectura.

