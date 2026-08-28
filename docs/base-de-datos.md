# Base de datos

`ojo_cientifico.db` · Room 2.6.1 · versión de esquema **1**

El esquema exportado está en [`database/schemas/com.ojocientifico.app.data.local.OjoDatabase/1.json`](../database/schemas/com.ojocientifico.app.data.local.OjoDatabase/1.json), generado automáticamente por KSP en cada compilación.

Toda la base es **local**. No hay sincronización, ni copia en la nube, ni identificadores de usuario.

## Las 14 tablas

### Catálogo (contenido sembrado)

Se rellena en la primera apertura desde `data/seed/`. No cambia con el uso.

#### `opcion_rasgo` — las 90 características observables

| Columna | Tipo | Notas |
|---|---|---|
| `id` | TEXT | PK. Por ejemplo `simetria_bilateral` |
| `categoria` | TEXT | Nombre del enum `CategoriaRasgo` |
| `etiqueta` | TEXT | «Simetría bilateral» |
| `pista` | TEXT | Texto que ayuda sin dar la respuesta |

#### `muestra` — las 16 muestras

| Columna | Tipo | Notas |
|---|---|---|
| `id` | TEXT | PK. Por ejemplo `mariposa` |
| `nombre` | TEXT | «Mariposa monarca» |
| `nombreCientifico` | TEXT | «Danaus plexippus» |
| `reino` | TEXT | Enum `ReinoMuestra` |
| `habitat` | TEXT | Enum `Habitat` |
| `ilustracion` | TEXT | Enum `ClaveIlustracion` que elige el dibujo Canvas |
| `descripcion` | TEXT | Descripción observable |
| `datoCurioso` | TEXT | Se revela al desbloquear la tarjeta |
| `nivelRequerido` | INTEGER | 1, 2 o 3 |

#### `rasgo_muestra` — la verdad morfológica

Es la tabla más importante de la aplicación: contiene lo que cada muestra **es**.

| Columna | Tipo | Notas |
|---|---|---|
| `muestraId` | TEXT | PK compuesta · FK → `muestra.id` (CASCADE) |
| `opcionId` | TEXT | PK compuesta · indexado |
| `verdadero` | INTEGER | `1` = la muestra tiene ese rasgo · `0` = distractor |

Guardar verdaderos y distractores en la misma tabla, distinguidos por una bandera, permite que la pantalla pida «todo lo que se puede mostrar de esta muestra» en una sola consulta, y que la evaluación filtre por `verdadero = 1`.

#### `mision` — las 19 misiones

| Columna | Tipo | Notas |
|---|---|---|
| `id` | TEXT | PK. Por ejemplo `m2_04` |
| `titulo`, `consigna`, `instruccionGuia` | TEXT | Textos que ve el niño |
| `tipo` | TEXT | `OBSERVACION`, `COMPARACION`, `CLASIFICACION`, `PATRON` |
| `nivel`, `orden` | INTEGER | Indexados. Definen la secuencia |
| `muestrasIds` | TEXT | Lista separada por comas |
| `categorias` | TEXT | Lista separada por comas |
| `modoComparacion` | TEXT? | Solo en comparaciones |
| `criterio` | TEXT? | Solo en clasificaciones |
| `xpBase` | INTEGER | Recompensa máxima |
| `requiere` | TEXT? | Misión previa obligatoria |

### Progreso

#### `perfil` — fila única (`id = 1`)

`alias`, `avatar`, `xp`, `creadoMillis`. Ningún dato personal.

#### `progreso_mision`

| Columna | Notas |
|---|---|
| `misionId` | PK |
| `completada` | Se pone a `1` con 2 estrellas o más y **nunca vuelve a `0`** |
| `mejorEstrellas`, `mejorPrecision` | Se conserva siempre el mejor histórico |
| `intentos` | Cuántas veces se ha jugado |
| `ultimaFechaMillis` | |

La lógica de «conservar lo mejor» vive en el `@Transaction registrarIntento(...)` del DAO, no en la interfaz.

#### `insignia`

`id` (PK), `desbloqueada`, `fechaMillis`. La sentencia de desbloqueo lleva `AND desbloqueada = 0`, de modo que una insignia se anuncia una sola vez aunque se recalcule mil veces.

#### `descubrimiento`

`muestraId` (PK), `desbloqueado`, `fechaMillis`. Misma protección.

#### `dia_actividad`

Una sola columna `fecha` (PK, formato `yyyy-MM-dd`). Insertar con `OnConflictStrategy.IGNORE` hace que contar días distintos sea `SELECT COUNT(*)`.

#### `configuracion` — fila única (`id = 1`)

`sonidoActivo`, `vibracionActiva`, `animacionesActivas`, `textoGrande`, `altoContraste`, `onboardingHecho`.

### Registro

#### `ficha` — el cuaderno científico

| Columna | Notas |
|---|---|
| `id` | PK autogenerada |
| `muestraId`, `misionId` | Indexado el primero. `misionId` es nulo en exploración libre |
| `fechaMillis` | Indexado |
| `aciertos`, `totalEsperado`, `marcasDeMas`, `estrellas` | El resultado tal como fue |
| `nota` | Nota de campo del niño, máximo 280 caracteres |

#### `observacion` — qué marcó exactamente

| Columna | Notas |
|---|---|
| `id` | PK autogenerada |
| `fichaId` | FK → `ficha.id` (CASCADE), indexado |
| `opcionId` | Indexado |
| `correcta` | |

Se escribe junto con la ficha en un `@Transaction`: o se guarda todo, o no se guarda nada.

De aquí sale la estadística «características distintas registradas»:

```sql
SELECT COUNT(DISTINCT opcionId) FROM observacion WHERE correcta = 1
```

#### `rasgo_fallado` — alimenta «Vuelve a observar»

`muestraId` + `opcionId` como PK compuesta, más `veces` y `ultimaFechaMillis`. Acertar un rasgo lo borra de esta tabla; volver a fallarlo incrementa el contador.

#### `historial`

`id`, `tipo`, `referencia`, `aciertos`, `fallos`, `estrellas`, `xpGanado`, `fechaMillis` (indexado). Alimenta la pantalla de progreso y los contadores por tipo de actividad.

## Relaciones

```
muestra ──1:N──► rasgo_muestra ──N:1──► opcion_rasgo
   │                                        ▲
   │                                        │
   └──1:1──► descubrimiento                 │
   │                                        │
   └──1:N──► ficha ──1:N──► observacion ────┘
   │
   └──1:N──► rasgo_fallado

mision ──1:1──► progreso_mision
       └──0:1──► requiere (autorreferencia)

perfil · configuracion   (fila única cada una)
insignia · dia_actividad · historial   (independientes)
```

Solo hay claves foráneas donde el borrado en cascada aporta algo: `rasgo_muestra → muestra` y `observacion → ficha`.

## Consultas que sostienen la gamificación

Ninguna recompensa se concede a partir de memoria. `OjoRepository.estadisticas()` construye el `EstadisticasExplorador` con estas lecturas:

| Estadística | Origen |
|---|---|
| XP | `perfil.xp` |
| Misiones completadas | `COUNT(*) FROM progreso_mision WHERE completada = 1` |
| Misiones perfectas | `COUNT(*) FROM progreso_mision WHERE mejorEstrellas >= 3` |
| Fichas | `COUNT(*) FROM ficha` |
| Observaciones / comparaciones / clasificaciones / patrones | `COUNT(*) FROM historial WHERE tipo = ?` |
| Descubrimientos | `COUNT(*) FROM descubrimiento WHERE desbloqueado = 1` |
| Características distintas | `COUNT(DISTINCT opcionId) FROM observacion WHERE correcta = 1` |
| Categorías exploradas | `SELECT DISTINCT opcionId` agrupado por categoría en el dominio |
| Días de expedición | `COUNT(*) FROM dia_actividad` |

## Reinicio

`OjoRepository.reiniciarProgreso()` borra `ficha` (y en cascada `observacion`), `rasgo_fallado`, `historial`, `progreso_mision` y `dia_actividad`; pone a cero `perfil.xp`; y devuelve `insignia` y `descubrimiento` a su estado bloqueado.

Las cuatro tablas de catálogo quedan intactas: la aplicación sigue teniendo sus 16 muestras y sus 19 misiones. El test `reiniciar borra el progreso pero conserva el catalogo` lo verifica.

## Migraciones

La base está en la versión 1 y se construye con `fallbackToDestructiveMigration()`. Si en una versión futura el progreso pasa a considerarse irremplazable, habrá que sustituirlo por migraciones reales; el esquema exportado en `database/schemas/` existe precisamente para poder escribirlas comparando versiones.
