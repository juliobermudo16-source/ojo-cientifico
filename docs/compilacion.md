# Compilación

## Requisitos

| Herramienta | Versión | Nota |
|---|---|---|
| JDK | **17** | Obligatorio. AGP 8.5.2 no soporta JDK 22+ |
| Android SDK | plataforma `android-34` | Y `build-tools 34.0.0` |
| Gradle | 8.9 | **No hace falta instalarlo**: viene el wrapper |
| Espacio en disco | ~2 GB | Caché de dependencias en `~/.gradle` |

Comprueba tu JDK:

```bash
java -version
```

Si tienes varias versiones instaladas, apunta `JAVA_HOME` a la 17 antes de compilar.

## Preparar el entorno

### 1. Ruta del SDK

Crea `local.properties` en la raíz del proyecto (está en `.gitignore`; nunca se sube):

```properties
sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
```

En Linux o macOS:

```properties
sdk.dir=/home/tu_usuario/Android/Sdk
```

> **Cuidado con las barras invertidas.** En un archivo `.properties`, `\U` se interpreta como escape y desaparece: `C:\Users\...` se convierte en `C:Users...`, una ruta inválida, y el error que verás es un confuso *«El nombre de archivo, el nombre de directorio o la sintaxis de la etiqueta del volumen no son correctos»*. Usa `/` o duplica las barras (`\\`).

### 2. Instalar la plataforma si falta

Con las *command line tools* del SDK:

```bash
sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

## Los cuatro comandos

```bash
./gradlew clean
```

```bash
./gradlew testDebugUnitTest
```

```bash
./gradlew lintDebug
```

```bash
./gradlew assembleDebug
```

En Windows sin bash, sustituye `./gradlew` por `gradlew.bat`.

### Resultados

| Qué | Dónde |
|---|---|
| APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Informe de tests | `app/build/reports/tests/testDebugUnitTest/index.html` |
| Informe de lint | `app/build/reports/lint-results-debug.html` |
| Esquema de Room | `database/schemas/` |

### Instalar en un dispositivo

Con depuración USB activada:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Configuración de lint

En `app/build.gradle.kts`:

```kotlin
lint {
    abortOnError = false
    warningsAsErrors = false
    checkReleaseBuilds = false
    disable += setOf("MissingTranslation", "UnusedResources")
}
```

Los avisos no rompen la compilación, pero el workflow de CI **sí falla si lint encuentra errores** (severidad `Error`), y publica un resumen con el recuento.

`MissingTranslation` está desactivado porque la aplicación es monolingüe en español por diseño: todos los textos educativos viven en el código de dominio, no en `strings.xml`.

## El caso de las rutas con tildes

La carpeta del proyecto se llama *Ojo Científico*. Eso obliga a dos ajustes en `gradle.properties`, ambos documentados en el propio archivo:

```properties
android.overridePathCheck=true
org.gradle.jvmargs=-Xmx2560m
```

**El primero** desactiva la comprobación del Android Gradle Plugin, que rechaza rutas no ASCII por precaución.

**El segundo es más sutil.** La tentación es escribir `-Dfile.encoding=UTF-8`, pero en Windows eso rompe el arranque de los procesos hijos de Gradle: `file.encoding` pasa a ser UTF-8 mientras `sun.jnu.encoding` (que es la que el sistema usa para resolver rutas) sigue siendo la del sistema. El classpath que Gradle escribe para el *test executor* se codifica con una y se lee con la otra, la `í` se corrompe y el error que aparece es:

```
Error: no se ha encontrado o cargado la clase principal
worker.org.gradle.process.internal.worker.GradleWorkerMain
```

La codificación UTF-8 de los fuentes se declara donde corresponde, en `app/build.gradle.kts`:

```kotlin
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
```

Si mueves el proyecto a una ruta sin tildes (`E:\OjoCientifico`), puedes borrar la línea `android.overridePathCheck` y el asunto desaparece.

## Problemas frecuentes

**«Unsupported class file major version»**
Estás usando un JDK distinto del 17. Ajusta `JAVA_HOME`.

**«SDK location not found»**
Falta `local.properties` o su ruta es incorrecta. Revisa las barras invertidas.

**«Failed to find Platform SDK with path: platforms;android-34»**
Instala la plataforma con `sdkmanager "platforms;android-34"`.

**«Unable to establish loopback connection»**
Gradle no puede abrir su canal interno. En Windows suele deberse a que `java.io.tmpdir` apunta a una ruta muy larga: los sockets AF_UNIX tienen un límite de unos 108 caracteres. Apunta `TMP` y `TEMP` a algo corto, por ejemplo `C:\Temp`.

**La aplicación no muestra el contenido nuevo que acabo de añadir**
La siembra solo corre cuando la tabla `muestra` está vacía. Desinstala la aplicación o borra sus datos.

## Reproducir la compilación

Estado verificado con esta configuración:

```
JDK       Temurin 17.0.20.1+1
Gradle    8.9
AGP       8.5.2
Kotlin    2.0.20
SDK       platform 34, build-tools 34.0.0
SO        Windows 11
```

| Comando | Resultado |
|---|---|
| `./gradlew clean` | correcto |
| `./gradlew testDebugUnitTest` | 94 tests, 0 fallos |
| `./gradlew lintDebug` | 0 errores |
| `./gradlew assembleDebug` | APK de 17,4 MB |

La primera compilación tarda varios minutos porque descarga AGP, Kotlin, Compose y Room. Las siguientes son bastante más rápidas gracias a la caché de Gradle.
