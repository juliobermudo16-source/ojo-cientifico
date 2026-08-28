# Arquitectura de Ojo Científico

## Principio rector

**Las reglas educativas viven en `domain/` y no dependen de Android.**

Esto no es purismo: es lo que permite que 77 de los 94 tests se ejecuten en la JVM en segundos, sin emulador. Si la lógica de evaluación estuviera dentro de un `@Composable`, no habría forma barata de comprobar que la comparación entre un caracol y un escarabajo devuelve lo correcto.

## Capas

```
┌──────────────────────────────────────────────────────────┐
│  ui/                                                     │
│  ├── pantallas/     Composables. Sin lógica educativa.   │
│  ├── componentes/   Piezas reutilizables.                │
│  ├── ilustraciones/ Dibujo con Canvas.                   │
│  ├── theme/         Paleta, tipografía, formas.          │
│  ├── nav/           NavHost y rutas.                     │
│  └── viewmodel/     Estado observable (StateFlow).       │
└───────────────────────────┬──────────────────────────────┘
                            │ StateFlow
┌───────────────────────────▼──────────────────────────────┐
│  data/repository/OjoRepository                           │
│  Punto único de acceso. Coordina persistencia,           │
│  cálculo de recompensas y siembra inicial.               │
└──────────┬─────────────────────────────┬─────────────────┘
           │                             │
┌──────────▼──────────┐      ┌───────────▼──────────────────┐
│  data/local/        │      │  domain/                     │
│  Room: entidades,   │      │  Kotlin puro. Sin Android.   │
│  DAOs, mapeadores   │      │  Modelos + lógica educativa. │
└─────────────────────┘      └──────────────────────────────┘
```

### `domain/model/`

| Archivo | Contenido |
|---|---|
| `Rasgos.kt` | `CategoriaRasgo` (9 categorías) y `CatalogoRasgos` con las 90 características. Fuente de verdad del contenido. |
| `Muestra.kt` | `Muestra` con sus rasgos verdaderos y sus distractores; `ReinoMuestra`, `Habitat`, `ClaveIlustracion`. |
| `Mision.kt` | `Mision`, `TipoMision`, `RangoExplorador`, `ModoComparacion` y `CriterioClasificacion` (que **calcula** el grupo de cada muestra). |
| `Resultados.kt` | `ResultadoSeleccion` y `ResultadoClasificacion`: calculan aciertos, omisiones, sobrantes, precisión y estrellas en el propio constructor. |
| `Insignia.kt` | `EstadisticasExplorador` y el catálogo de 10 insignias, cada una con su función de medida. |
| `Ficha.kt` | `FichaCientifica`, `RasgoFallado`, `EntradaHistorial`, `Configuracion`. |

### `domain/logica/`

| Archivo | Responsabilidad |
|---|---|
| `Evaluadores.kt` | `EvaluadorObservacion`, `EvaluadorComparacion`, `DetectorPatrones`, `EvaluadorClasificacion`. |
| `Progresion.kt` | `SistemaXp`, `CalculadoraProgreso`, `DesbloqueoMisiones`. |
| `MotorFeedback.kt` | Convierte un resultado en un mensaje educativo con pista. |
| `MotorInsignias.kt` | Concede insignias y construye el panel con avance parcial. |
| `Repaso.kt` | `PlanificadorRepaso`: qué volver a observar y por qué. |

Todos son `object` sin estado: entran datos, salen datos. Eso los hace triviales de testear y de razonar.

## Flujo de una actividad completa

Tomemos la misión `m1_02` («Alas bajo la lupa»):

1. **`Navegacion.kt`** crea un `ActividadViewModel` y llama a `iniciarMision("m1_02")`.
2. El ViewModel pide al repositorio la misión y sus muestras. El repositorio las sirve desde su caché en memoria del catálogo (contenido fijo, se consulta constantemente).
3. Según `mision.tipo` construye un `EstadoActividad.Observacion`, cuyo campo `opciones` sale de `EvaluadorObservacion.universo(muestra, categorias)` — la mezcla de rasgos verdaderos y distractores.
4. **`PantallaActividad.kt`** dibuja el visor con lupa, el selector de categorías y los chips. Cada toque llama a `vm.alternarOpcion(id)`, que solo modifica el `Set` de la selección.
5. Al pulsar «Guardar ficha», `comprobar()` llama a `EvaluadorObservacion.evaluar(...)`, que devuelve un `ResultadoSeleccion` con todo calculado.
6. El ViewModel entrega ese resultado a `repositorio.registrarObservacion(...)`, que en una sola pasada:
   - guarda la `FichaEntity` y sus `ObservacionEntity` en una transacción;
   - acumula los rasgos que se escaparon en `rasgo_fallado` y borra los que ya se acertaron;
   - desbloquea el descubrimiento si la precisión llega al 75 %;
   - y llama a `cerrarActividad(...)`.
7. **`cerrarActividad`** es el paso común a las cuatro actividades: escribe el historial, suma el XP calculado por `SistemaXp`, marca el día de expedición, guarda el mejor resultado de la misión, recalcula las `EstadisticasExplorador` **leyéndolas de la base de datos** y pregunta al `MotorInsignias` qué se ha conseguido.
8. Devuelve un `ResumenMision` que la pantalla de resultado dibuja.

El punto importante del paso 7: las insignias no se conceden a partir de variables en memoria, sino de un `SELECT COUNT(...)` real. Si el proceso muere y se reabre la aplicación, las cuentas siguen cuadrando.

## ViewModels

Solo tres, deliberadamente:

- **`AppViewModel`** — preferencias, alias y reinicio. Vive mientras vive la aplicación porque el tema depende de él.
- **`PanelViewModel`** — todo el estado de *lectura*: laboratorio, misiones, cuaderno, colección, insignias, repaso y progreso. Se comparte entre esas pantallas para no repetir seis veces la misma combinación de flujos.
- **`ActividadViewModel`** — la actividad en curso, con un `sealed interface EstadoActividad` que cubre los cuatro tipos y la pantalla de resultado.

## Decisiones y sus motivos

**Catálogo en `domain` y no solo en la base de datos.**
`CatalogoRasgos` y `MuestrasSemilla` son código Kotlin. La base de datos se siembra desde ahí. Así el contenido se puede validar en tests sin abrir una base de datos, y `ContenidoSemillaTest` puede afirmar que ninguna misión es imposible.

**Caché en memoria del catálogo.**
`OjoRepository` mantiene `muestrasCache` y `misionesCache`. Es contenido inmutable que se consulta en cada recomposición; ir a SQLite cada vez sería gasto sin ganancia. El progreso, en cambio, **nunca** se cachea: siempre se lee de Room.

**Ilustraciones con `Canvas` en lugar de archivos.**
Un PNG de una mariposa es opaco: nadie puede comprobar que tiene seis patas. Un dibujo en Canvas es código, se revisa en el *pull request* y escala a cualquier densidad sin pesar nada. Además elimina de raíz la tentación de tirar de una URL.

**`fallbackToDestructiveMigration()`.**
La base contiene contenido sembrado (regenerable) y progreso local. Ante un cambio de esquema es preferible regenerar que bloquear a un niño de nueve años con un error de migración. Si en el futuro el progreso pasa a ser irremplazable, habrá que escribir migraciones reales.

**Listas como texto separado por comas en `MisionEntity`.**
`muestrasIds` y `categorias` son listas cortas y estables. Un `TypeConverter` con JSON añadiría una dependencia para guardar `"forma,color"`.

## Qué está simplificado

Documentado abiertamente, según la regla de no sustituir funciones reales por decorados:

- **Clasificación por toque en lugar de arrastre.** Se selecciona la muestra y luego el grupo. Es funcionalmente equivalente al *drag and drop*, más accesible para lectores de pantalla y más fiable en pantallas pequeñas. La evaluación es idéntica.
- **Sonido y vibración.** Los interruptores existen y su preferencia se persiste, pero no hay archivos de audio incluidos. La aplicación nunca depende del sonido para entenderse (requisito de accesibilidad), así que su ausencia no afecta a ninguna actividad.
- **La lupa amplía toda la ilustración**, no una región bajo el dedo. Cumple su función pedagógica (mirar más de cerca) sin la complejidad de un *shader* de máscara.
