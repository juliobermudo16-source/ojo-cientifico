# Ojo Científico

**Software educativo para el desarrollo de la observación científica y el registro morfológico en niños de 8 a 12 años.**

> *Observa. Registra. Descubre.*

Ojo Científico es una aplicación Android nativa, **totalmente offline**, en la que el niño trabaja como un pequeño investigador: observa muestras ilustradas, registra sus características morfológicas en un cuaderno científico, las compara, las clasifica y descubre los patrones que se repiten entre organismos aparentemente distintos.

No es un cuestionario disfrazado. El sistema **almacena las características reales de cada muestra** y evalúa lo que el niño marca contra esos datos: las semejanzas, las diferencias, los patrones y los grupos de clasificación **se calculan**, nunca están escritos dentro de la actividad.

---

## Índice

- [Público objetivo](#público-objetivo)
- [Qué hace la aplicación](#qué-hace-la-aplicación)
- [Contenido incluido](#contenido-incluido)
- [Cómo funciona la evaluación](#cómo-funciona-la-evaluación)
- [Progresión y gamificación](#progresión-y-gamificación)
- [Privacidad y funcionamiento offline](#privacidad-y-funcionamiento-offline)
- [Accesibilidad](#accesibilidad)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Cómo compilar](#cómo-compilar)
- [Tests](#tests)
- [Subir el proyecto a GitHub](#subir-el-proyecto-a-github)
- [Generación automática del APK](#generación-automática-del-apk)
- [Estado de verificación](#estado-de-verificación)
- [Documentación técnica](#documentación-técnica)

---

## Público objetivo

Niños y niñas de **8 a 12 años** (tercer ciclo de primaria). El lenguaje no infantiliza al usuario: se le habla como a un investigador principiante, con frases cortas y vocabulario científico real (*simetría bilateral*, *nervaduras*, *estructura filamentosa*).

También sirve como herramienta de aula para trabajar la competencia de observación en Ciencias de la Naturaleza.

---

## Qué hace la aplicación

El ciclo de juego es siempre el mismo:

```
ENTRAR AL LABORATORIO
        ↓
RECIBIR UNA MISIÓN
        ↓
OBSERVAR (lupa, categoría a categoría)
        ↓
REGISTRAR CARACTERÍSTICAS
        ↓
COMPARAR / CLASIFICAR / BUSCAR PATRONES
        ↓
RECIBIR FEEDBACK EDUCATIVO
        ↓
GANAR XP · INSIGNIA · DESCUBRIMIENTO
        ↓
DESBLOQUEAR LA SIGUIENTE MISIÓN
```

### Áreas funcionales

| Área | Qué se hace en ella |
|---|---|
| **Laboratorio** | Pantalla principal: misión actual, rango, XP y acceso a todas las zonas |
| **Misiones** | Plan de la expedición por niveles, con estado real de desbloqueo |
| **Muestrario** | Observación libre de cualquier muestra desbloqueada |
| **Observación** | Registro morfológico paso a paso con lupa de aumento |
| **Comparación** | Semejanzas y diferencias entre dos muestras |
| **Clasificación** | Reparto de muestras en grupos según un criterio |
| **Patrones** | Búsqueda de lo que comparten tres o más muestras |
| **Cuaderno científico** | Todas las fichas guardadas, con las marcas y la nota de campo |
| **Colección** | Tarjetas de descubrimiento con nombre científico y dato curioso |
| **Insignias** | Diez logros con su avance parcial visible |
| **Vuelve a observar** | Repaso de las muestras cuyas características se escaparon |
| **Progreso** | Rango, estadísticas reales e historial de expedición |
| **Configuración** | Alias, chapa y ajustes de accesibilidad |

---

## Contenido incluido

La instalación inicial ya viene completa. No hay pantallas vacías esperando contenido.

| Elemento | Cantidad |
|---|---|
| Muestras ilustradas | **16** |
| Características morfológicas del catálogo | **90** repartidas en **9 categorías** |
| Misiones encadenadas | **19** en 3 niveles |
| Criterios de clasificación | **5** |
| Insignias científicas | **10** |
| Tarjetas de colección | **16** (una por muestra) |

**Las 16 muestras:** hoja de helecho, mariposa monarca, caracol de jardín, hormiga obrera, pez payaso, flor de girasol, semilla de diente de león, pluma de ave, estrella de mar, escarabajo joya, seta del bosque, cristal de cuarzo, telaraña orbicular, copo de nieve, célula vegetal y rana arborícola.

**Las 9 categorías morfológicas:** forma, color, textura, simetría, tamaño, partes visibles, estructura, número y rasgo particular.

### Ilustraciones

Todas las imágenes son **originales y vectoriales**, dibujadas con `Compose Canvas`: las 16 muestras, el personaje guía, las 10 insignias y los instrumentos de laboratorio (lupa, cuaderno, microscopio, muestrario, mapa, probeta). El icono de la aplicación y la pantalla de arranque son *vector drawables* propios.

Esto significa que **no hay ni una sola URL externa**, ni descargas, ni imágenes que dependan de la resolución del dispositivo. Y, sobre todo, cada dibujo respeta los rasgos que el niño debe poder observar: la estrella de mar tiene cinco brazos porque el dato dice `numero_5`, el copo de nieve tiene seis porque dice `numero_6`.

### Personaje guía

**Iris**, una exploradora joven con gafas de aumento en la frente, presenta las misiones, da pistas y celebra los descubrimientos. Tiene cuatro gestos (neutro, animando, pensando, celebrando) y habla siempre en frases de una o dos líneas.

---

## Cómo funciona la evaluación

Cada muestra guarda en la base de datos dos conjuntos de características:

- **rasgos verdaderos** — lo que la muestra realmente tiene;
- **distractores** — características plausibles de la misma categoría que la muestra *no* tiene.

En pantalla se mezclan ambos, de modo que marcar es una decisión real y no un «marca todo».

```
precisión = aciertos / total_esperado − (marcas_de_más × 0,5) / total_esperado
```

Marcar de más también penaliza, pero la mitad que no observar: observar de menos es un error más grave para un científico que un exceso de celo.

| Precisión | Estrellas |
|---|---|
| ≥ 95 % | ★★★ |
| ≥ 75 % | ★★ |
| ≥ 50 % | ★ |
| < 50 % | — |

El resto de actividades se derivan de esos mismos conjuntos:

| Actividad | Cómo se obtiene la respuesta correcta |
|---|---|
| Semejanzas | `rasgos(A) ∩ rasgos(B)` |
| Diferencias | `rasgos(A) − rasgos(B)` |
| Patrón común | intersección de los rasgos de **todas** las muestras |
| Clasificación | función `criterio.grupoDe(muestra)` aplicada a los datos de la muestra |

### Feedback

Nunca se dice solo «correcto» o «incorrecto». Cada resultado nombra lo observado, cuantifica lo que falta y ofrece una pista sobre la **categoría** donde mirar, sin dar la respuesta:

> **¡Buen ojo!** Registraste 4 de 6 características de la mariposa monarca, entre ellas «alas». Aún quedan 2 características por descubrir.
>
> *Pista sobre número: cuenta las partes que se repiten.*

Los tests comprueban explícitamente que el feedback nunca contiene términos que descalifiquen al niño.

---

## Progresión y gamificación

Tres rangos que se desbloquean con XP acumulado:

| Nivel | Rango | XP | Qué se trabaja |
|---|---|---|---|
| 1 | **Observador** | 0 | Observación guiada con pocas categorías |
| 2 | **Investigador** | 200 | Registro completo, comparación y clasificación |
| 3 | **Descubridor** | 520 | Análisis de rasgos y búsqueda de patrones |

- El XP depende de la precisión real; repetir una misión ya superada da un 35 % del XP.
- Una misión se da por superada con **2 estrellas**; repetirla nunca empeora el mejor resultado guardado.
- Las insignias se conceden a partir de estadísticas leídas de la base de datos, y solo se anuncian una vez.
- Una tarjeta de colección se revela al observar la muestra con **≥ 75 %** de precisión.

**No hay** rankings online, compras, anuncios, vidas, castigos ni presión social.

---

## Privacidad y funcionamiento offline

- El manifiesto **no declara el permiso `INTERNET`**. La aplicación no puede conectarse aunque quisiera.
- Sin Firebase, sin backend, sin analítica, sin login, sin nube, sin anuncios.
- No se pide nombre real, correo, teléfono, dirección ni ubicación. Solo un **alias** y una chapa de color.
- Todo el progreso vive en una base de datos SQLite local del propio dispositivo.
- La pantalla de configuración permite borrar todo el progreso en cualquier momento.

---

## Accesibilidad

- Objetivos táctiles de **48–56 dp** como mínimo.
- Interruptor de **texto grande** (escala tipográfica ×1,18) y de **alto contraste**.
- Interruptor para **desactivar las animaciones**.
- Las ilustraciones importantes llevan `contentDescription` en español.
- El estado de cada característica marcada se comunica con **icono + forma + texto**, no solo con color: acierto (✓ círculo), olvidado (! cuadrado), sobrante (× círculo).
- Los porcentajes y estrellas se anuncian como texto para los lectores de pantalla.

---

## Tecnologías

| Componente | Versión |
|---|---|
| Kotlin | 2.0.20 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.9 (wrapper incluido) |
| JDK | 17 |
| Jetpack Compose (BOM) | 2024.09.02 |
| Material 3 | vía BOM |
| Navigation Compose | 2.8.0 |
| Room | 2.6.1 (con KSP) |
| Coroutines | 1.8.1 |
| Lifecycle / ViewModel | 2.8.5 |
| Robolectric (tests) | 4.13 |
| `minSdk` / `targetSdk` / `compileSdk` | 24 / 34 / 34 |

Todas las versiones están **fijadas** en `gradle/libs.versions.toml`. No se usa ninguna versión dinámica.

---

## Arquitectura

**MVVM + Repository**, con tres capas separadas y las reglas educativas fuera de la interfaz:

```
ui/  ───────────►  viewmodel/  ───────────►  repository/  ──────►  Room
(Compose)          (StateFlow)               (única fuente)        (SQLite)
     ▲                                             │
     └──────────── domain/ (lógica pura) ◄─────────┘
```

- **`domain/`** — modelos y lógica educativa en **Kotlin puro**, sin una sola dependencia de Android. Aquí viven el catálogo de características, los evaluadores, el sistema de XP, el motor de insignias, el motor de feedback y el planificador de repaso. Es completamente testeable sin UI ni emulador.
- **`data/`** — entidades Room, DAOs, contenido semilla y el `OjoRepository`, que es el único punto de acceso a los datos y quien coordina persistencia y recompensas.
- **`ui/`** — tema, ilustraciones Canvas, componentes reutilizables, pantallas y ViewModels.

Ningún Composable contiene lógica educativa, y no se usan listas en memoria como sustituto de Room.

---

## Estructura del proyecto

```
Ojo Científico/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/ojocientifico/app/
│       │   │   ├── data/
│       │   │   │   ├── local/        (entidades, DAOs, base de datos, mapeadores)
│       │   │   │   ├── seed/         (16 muestras y 19 misiones)
│       │   │   │   └── repository/   (OjoRepository)
│       │   │   ├── domain/
│       │   │   │   ├── model/        (Rasgos, Muestra, Misión, Resultados, Insignia, Ficha)
│       │   │   │   └── logica/       (Evaluadores, Progresión, Feedback, Insignias, Repaso)
│       │   │   ├── ui/
│       │   │   │   ├── theme/        (paleta, tipografía, formas)
│       │   │   │   ├── ilustraciones/(Canvas: muestras, Iris, insignias, laboratorio)
│       │   │   │   ├── componentes/  (tarjetas, chips, botones, barras)
│       │   │   │   ├── pantallas/    (12 pantallas)
│       │   │   │   ├── viewmodel/    (App, Panel, Actividad)
│       │   │   │   └── nav/          (rutas y NavHost)
│       │   │   ├── MainActivity.kt
│       │   │   └── OjoCientificoApp.kt
│       │   ├── res/                  (icono adaptativo, splash, temas)
│       │   └── AndroidManifest.xml
│       └── test/                     (94 tests)
├── database/
│   └── schemas/                      (esquema Room exportado)
├── docs/                             (documentación técnica)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── .github/workflows/build-apk.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew  ·  gradlew.bat
└── README.md
```

---

## Cómo compilar

### Requisitos

- **JDK 17** (obligatorio; JDK 21+ no está soportado por AGP 8.5).
- **Android SDK** con la plataforma `android-34` y `build-tools 34.0.0`.
- No hace falta instalar Gradle: el proyecto incluye el *wrapper*.

### Configurar la ruta del SDK

Crea un archivo `local.properties` en la raíz (está en `.gitignore`, no se sube):

```properties
sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
```

En Linux o macOS:

```properties
sdk.dir=/home/tu_usuario/Android/Sdk
```

> Usa **barras normales** (`/`) o barras invertidas dobles (`\\`). Una sola barra invertida se interpreta como escape y la ruta quedará inválida.

### Comandos

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

En Windows con `cmd` o PowerShell, usa `gradlew.bat` en lugar de `./gradlew`.

### Dónde queda el APK

```
app/build/outputs/apk/debug/app-debug.apk
```

Para instalarlo en un dispositivo conectado por USB con la depuración activada:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Nota sobre rutas con tildes

La carpeta de este proyecto se llama *Ojo Científico*, con tilde. El Android Gradle Plugin avisa de que las rutas con caracteres no ASCII pueden dar problemas en Windows, por lo que `gradle.properties` incluye:

```properties
android.overridePathCheck=true
```

Por el mismo motivo, `org.gradle.jvmargs` **no** fuerza `-Dfile.encoding=UTF-8`: en Windows debe coincidir con `sun.jnu.encoding`, o los procesos hijos de Gradle no resuelven las rutas con tilde. La codificación UTF-8 de los fuentes se declara explícitamente en `app/build.gradle.kts`.

Si prefieres evitar el asunto por completo, mueve el proyecto a una ruta sin tildes (por ejemplo `E:\OjoCientifico`) y podrás borrar esa línea.

---

## Tests

**94 tests**, todos ejecutables en la JVM sin emulador:

| Suite | Tests | Qué cubre |
|---|---|---|
| `EvaluadoresTest` | 23 | Observación, comparación, patrones, clasificación y casos límite |
| `ProgresionTest` | 28 | XP, rangos, desbloqueos, insignias y repaso |
| `ContenidoSemillaTest` | 17 | Coherencia del contenido: ids, referencias, ciclos, dificultad y codificación |
| `PersistenciaTest` | 17 | Room real (Robolectric): siembra, fichas, XP, colección, reinicio |
| `MotorFeedbackTest` | 9 | Tono, pistas y lenguaje respetuoso |

Casos límite cubiertos: selección vacía, marcas fuera del universo, ids inexistentes, notas vacías y de 1000 caracteres, doble comprobación, actividad repetida, XP negativo, precisión fuera de rango, base de datos nueva, reinicio, listas vacías y siembra repetida.

Merece la pena destacar `ContenidoSemillaTest`: valida que **ninguna misión sea imposible ni trivial** (que la respuesta exista y que no sea todo el universo de opciones), que la cadena de requisitos no tenga ciclos, que cada nivel dé XP suficiente para desbloquear el siguiente, y que **las tildes y las eñes** sobrevivan intactas en todo el contenido.

```bash
./gradlew testDebugUnitTest
```

El informe HTML queda en `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## Subir el proyecto a GitHub

El proyecto está listo para subirse tal cual. **No incluye ningún repositorio inicializado**: eso lo decides tú.

```bash
git init
```

```bash
git add .
```

```bash
git commit -m "Ojo Científico: versión inicial"
```

```bash
git branch -M main
```

```bash
git remote add origin https://github.com/TU_USUARIO/ojo-cientifico.git
```

```bash
git push -u origin main
```

`.gitignore` ya excluye `local.properties`, las carpetas `build/`, `.gradle/` y los archivos de IDE. El `gradle-wrapper.jar` **sí** se sube: es necesario para que el workflow funcione.

---

## Generación automática del APK

El archivo `.github/workflows/build-apk.yml` genera el APK automáticamente.

**Cuándo se ejecuta:** en cada `push`, en cada *pull request* y manualmente desde la pestaña **Actions → Compilar APK de Ojo Científico → Run workflow**.

**Qué hace, en orden:**

1. Descarga el código.
2. Configura **JDK 17** (Temurin).
3. Configura Gradle con caché.
4. Regenera el *wrapper* si faltara el `.jar`.
5. Da permisos de ejecución a `gradlew`.
6. `./gradlew clean`
7. `./gradlew testDebugUnitTest` — **si un test falla, el workflow se detiene aquí y no se genera el APK**.
8. `./gradlew lintDebug` y publica un resumen con el número de errores y avisos. Los avisos no rompen la compilación; los errores sí.
9. `./gradlew assembleDebug`
10. Renombra el resultado a **`Ojo-Cientifico-debug.apk`**.
11. Lo sube como artefacto de GitHub Actions.

**Artefactos publicados:**

| Nombre | Contenido | Retención |
|---|---|---|
| `Ojo-Cientifico-debug-apk` | El APK instalable | 30 días |
| `Ojo-Cientifico-informe-tests` | Informe HTML de los tests | 14 días |
| `Ojo-Cientifico-informe-lint` | Informe de lint (HTML y XML) | 14 días |

**Dónde descargarlo:** pestaña **Actions** → la ejecución más reciente → sección **Artifacts** al final de la página → `Ojo-Cientifico-debug-apk`.

El APK de depuración se firma con la clave de depuración que genera el propio Android SDK: **no se usa ningún secreto ni servicio externo**.

---

## Estado de verificación

Compilado y verificado localmente con JDK 17.0.20.1, Gradle 8.9, AGP 8.5.2 y Android SDK 34:

| Comando | Resultado |
|---|---|
| `./gradlew clean` | ✅ correcto |
| `./gradlew testDebugUnitTest` | ✅ **94 tests, 0 fallos** |
| `./gradlew lintDebug` | ✅ **0 errores**, avisos informativos |
| `./gradlew assembleDebug` | ✅ **APK generado (17,4 MB)** |

La aplicación **no** ha sido ejecutada en un dispositivo ni en un emulador durante el desarrollo: la verificación es de compilación, tests y análisis estático.

---

## Documentación técnica

En la carpeta [`docs/`](docs/):

- [`arquitectura.md`](docs/arquitectura.md) — capas, flujo de datos y decisiones de diseño.
- [`base-de-datos.md`](docs/base-de-datos.md) — las 14 tablas de Room y su relación.
- [`contenido-educativo.md`](docs/contenido-educativo.md) — catálogo morfológico, muestras y misiones.
- [`compilacion.md`](docs/compilacion.md) — requisitos, comandos y resolución de problemas.

---

## Licencia y uso

Proyecto educativo. Las ilustraciones, el personaje, la paleta y la identidad visual de Ojo Científico son originales de este proyecto.
