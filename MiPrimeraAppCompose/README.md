# MiPrimeraAppCompose (1_app)

Primera app del laboratorio **"Mi primera app con Compose"**, reconstruida siguiendo las instrucciones de `1_app.md`.

> Tema: Jetpack Compose, estado, layouts y Material Design 3

## Funcionalidades implementadas

- `Scaffold` con `TopAppBar` y título.
- Tarjeta Material 3 que muestra el valor actual del contador.
- Botón **-1** en color de error y botón **+1**.
- Límite inferior `-10` y límite superior `10`.
- Mensaje de error al intentar superar cualquiera de los límites.
- Botón **Reiniciar** que vuelve el contador a `0` y limpia el mensaje.
- Historial de valores visitados mostrado como `1 → 2 → 3`.
- Vista previa de Compose (`@Preview`).

## Estructura del proyecto

```
MiPrimeraAppCompose/
├── app/
│   ├── src/main/java/com/tudominio/miprimeraappcompose/
│   │   ├── MainActivity.kt
│   │   └── ui/theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   ├── src/main/res/
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Requisitos

- Android Studio (AGP 9.5.0, Gradle 9.6.0)
- Kotlin 2.4.21
- JDK 17+
- SDK: `compileSdk = 37`, `targetSdk = 37`, `minSdk = 24` (Android 7.0)

## Compilar e instalar

```bash
./gradlew assembleDebug

adb install -r app/build/outputs/apk/debug/app-debug.apk

adb shell am start -n com.tudominio.miprimeraappcompose/.MainActivity
```

## Pruebas manuales

| Acción | Resultado esperado |
|---|---|
| Abrir la app | Contador en `0` |
| Presionar "+1" | El contador sube y se agrega el valor al historial |
| Presionar "-1" | El contador baja y se agrega el valor al historial |
| Llegar a `10` y presionar "+1" | Muestra "¡Límite máximo alcanzado (10)!" |
| Llegar a `-10` y presionar "-1" | Muestra "¡Límite mínimo alcanzado (-10)!" |
| Presionar "Reiniciar" | El contador vuelve a `0` y desaparece el mensaje |

## Documentación del laboratorio

La transcripción en Markdown del PDF está en
[`docs/Mi-primera-app-compose.md`](docs/Mi-primera-app-compose.md).
