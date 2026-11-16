# AGENTS.md

## Project
- Single-module Android app (`:app`), Gradle Kotlin DSL, version catalog in `gradle/libs.versions.toml`.
- Kotlin + Jetpack Compose (Material 3). Root namespace/app ID: `com.example.split_screen`.
- Real entrypoint: `app/src/main/java/com/example/split_screen/MainActivity.kt`; manifest: `app/src/main/AndroidManifest.xml`.
- SDK/toolchain: `minSdk 26`, `targetSdk 37`, `compileSdk { version = release(37) }`, Java 11 (`app/build.gradle.kts`).
- Key versions (`gradle/libs.versions.toml`): AGP `9.5.1`, Kotlin `2.2.10`, Compose BOM `2026.02.01`.

## Commands (Windows, repo root)
- Build debug APK: `.\gradlew.bat :app:assembleDebug`
- JVM unit tests: `.\gradlew.bat :app:testDebugUnitTest`
- Single JVM test class: `.\gradlew.bat :app:testDebugUnitTest --tests "com.example.split_screen.ExampleUnitTest"`
- Single JVM test method: `.\gradlew.bat :app:testDebugUnitTest --tests "com.example.split_screen.ExampleUnitTest.addition_isCorrect"`
- Instrumented tests (needs emulator/device): `.\gradlew.bat :app:connectedDebugAndroidTest`
- Install debug on device: `.\gradlew.bat :app:installDebug`
- Full verification/build: `.\gradlew.bat build`

## Repo notes / quirks
- Not a git repo. No CI, no lint/format/typecheck tasks configured beyond default Gradle/Android tasks.
- `org.gradle.configuration-cache=true` in `gradle.properties`; prefer running via wrapper.
- Release build enables optimization with package scope `androidx.**`, `kotlin.**`, `kotlinx.**` (`app/build.gradle.kts`).
- `app/src/main/keepRules/rules.keep` exists; preserve/update if adding shrinking/keep-rule-sensitive code.
- Tests present are template examples only: `app/src/test/.../ExampleUnitTest.kt`, `app/src/androidTest/.../ExampleInstrumentedTest.kt` (asserts package name).
- `local.properties` is machine-local (SDK path etc.); do not treat as portable project config.

## Reglas del proyecto (obligatorias)

### Proceso
- Antes de código: plan escrito + aprobación explícita ("aprobado"). Sin aprobación = sin cambios en archivos.
- Dudas: preguntar siempre. No asumir. No inventar.

### Arquitectura
- Lógica separada de UI (Compose). Componentes simples, reutilizables, una responsabilidad.
- Un archivo por componente relevante. Constantes en archivo dedicado; no repetir valores.

### Seguridad
- Secretos/claves/URLs solo en env / `local.properties` / `BuildConfig`. Nunca en código fuente.
- Revisar antes de commit: ningún valor expuesto.

### Librerías
- Solo librerías con muchas descargas recientes y no deprecadas.
- Antes de instalar: pedir aprobación (nombre, motivo, versión). Sin aprobación = sin dependencia nueva.

### Pruebas
- Test para cada funcionalidad.
- `.\gradlew.bat :app:testDebugUnitTest` (JVM); `connectedDebugAndroidTest` (instrumentado, necesita emulador/dispositivo).

### Contexto
- App split-screen Android: dividir pantalla en 2, ejecutar 2 apps simultáneas.
- minSdk 26, targetSdk 37, Java 11, Compose Material 3.

### Workflow / PRs
- Dividir tareas en tandas.
- No iniciar siguiente tanda hasta cerrar PR de la tanda anterior.
- PR máximo 300 líneas.
- Todos los PR a rama `dev`.
- Correr solo tests relacionados con lo cambiado, no toda la suite.
- Antes de abrir PR: verificar que pasen tests nuevos y tests relacionados.
- Antes de abrir PR: security revisa vulnerabilidades; si falla, devuelve al agente origen y no se abre PR.

### Agentes
- back: lógica de negocio, servicios, datos.
- front: UI Compose, componentes, pantallas.
- security: evalúa código y vulnerabilidades antes de avanzar; si hay error, devuelve al agente origen.
- Sin aprobación de security no se avanza a siguiente tanda ni PR.
