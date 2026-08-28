# Contenido educativo

## Objetivo pedagógico

Desarrollar dos capacidades concretas:

1. **Observación científica** — mirar con método, categoría a categoría, en lugar de mirar «en general».
2. **Registro morfológico** — nombrar lo observado con vocabulario preciso y dejarlo anotado para poder volver a ello.

De ahí que la aplicación no pregunte «¿qué animal es este?», sino «¿qué forma tiene?», «¿cuántas partes iguales cuentas?», «¿qué comparten estas tres muestras?».

## Las 9 categorías morfológicas

| Categoría | Pregunta que se le hace al niño | Opciones |
|---|---|---|
| Forma | ¿Qué forma general tiene? | 12 |
| Color | ¿Qué colores observas? | 12 |
| Textura | ¿Cómo se vería o se sentiría su superficie? | 12 |
| Simetría | ¿Cómo se reparten sus partes? | 4 |
| Tamaño | ¿Qué tamaño tiene comparado con tu mano? | 5 |
| Partes visibles | ¿Qué partes puedes distinguir? | 23 |
| Estructura | ¿Cómo está construida por dentro o por fuera? | 9 |
| Número | ¿Cuántos elementos iguales cuentas? | 6 |
| Rasgo particular | ¿Qué tiene de especial esta muestra? | 8 |

**Total: 90 características.** Cada una lleva una pista redactada para orientar sin resolver: la pista de *simetría bilateral* es «Si la doblas por la mitad, los dos lados coinciden», no «la mariposa la tiene».

### Progresión de categorías por rango

| Rango | Categorías que se piden |
|---|---|
| 1 · Observador | forma, color, tamaño, partes |
| 2 · Investigador | + textura, simetría |
| 3 · Descubridor | las nueve |

Un niño de ocho años empieza por lo que se ve de un vistazo. La simetría y la estructura, que exigen un paso de abstracción, llegan después.

## Las 16 muestras

| Muestra | Nombre científico | Grupo | Rango | Con qué se trabaja |
|---|---|---|---|---|
| Hoja de helecho | *Dryopteris filix-mas* | Planta | 1 | Ramificación, nervaduras, repetición |
| Mariposa monarca | *Danaus plexippus* | Animal | 1 | Simetría bilateral, alas, colores de aviso |
| Caracol de jardín | *Cornu aspersum* | Animal | 1 | Espiral, crecimiento por capas |
| Hormiga obrera | *Formica rufa* | Animal | 1 | Segmentación, seis patas, escala diminuta |
| Pez payaso | *Amphiprion ocellaris* | Animal | 1 | Escamas, aletas, adaptación al agua |
| Flor de girasol | *Helianthus annuus* | Planta | 1 | Simetría radial, «muchos elementos» |
| Semilla de diente de león | *Taraxacum officinale* | Planta | 2 | Filamentos, dispersión por viento |
| Pluma de ave | Pluma pennácea | Animal | 2 | Estructura ramificada de filamentos |
| Estrella de mar | *Asterias rubens* | Animal | 2 | Cinco brazos, radial, textura granulada |
| Escarabajo joya | *Chrysolina fastuosa* | Animal | 2 | Caparazón, brillo estructural |
| Seta del bosque | *Amanita muscaria* | Hongo | 2 | Sombrero, laminillas, un reino distinto |
| Cristal de cuarzo | SiO₂ | Mineral | 2 | Estructura cristalina, **no vivo** |
| Telaraña orbicular | *Araneus diadematus* | Construcción | 3 | Red radial, **obra de un animal** |
| Copo de nieve | Cristal de hielo | Mineral | 3 | Seis brazos, ramificación, no vivo |
| Célula vegetal | *Elodea canadensis* | Microscópico | 3 | Estructura celular, otra escala |
| Rana arborícola | *Agalychnis callidryas* | Animal | 3 | Camuflaje **y** aviso a la vez |

La selección no es decorativa. Está construida para que ciertas comparaciones sean posibles:

- **Caracol vs. escarabajo**: los dos llevan una cubierta encima, pero una crece por capas y la otra es un caparazón segmentado.
- **Cristal, copo y telaraña**: tres cosas que parecen «hechas» pero no están vivas, con matices distintos (dos son minerales, la tercera es obra de un ser vivo).
- **Copo, estrella de mar y girasol**: un mineral, un animal y una planta que comparten simetría radial y forma estrellada. El patrón atraviesa los reinos.
- **Hormiga, mariposa y escarabajo**: la definición de insecto, deducida por el propio niño.
- **Rana**: tiene a la vez `rasgo_camuflaje` y `rasgo_colores_aviso`, porque en la realidad usa las dos estrategias.

## Las 19 misiones

### Nivel 1 · Observador

| Id | Título | Tipo | XP |
|---|---|---|---|
| `m1_01` | Tu primera muestra | Observación (forma, color) | 30 |
| `m1_02` | Alas bajo la lupa | Observación (+ partes) | 35 |
| `m1_03` | La casa en espiral | Observación (forma, tamaño, partes) | 35 |
| `m1_04` | Muy pequeña, muy completa | Observación (4 categorías) | 40 |
| `m1_05` | Dos insectos, un parecido | Comparación · semejanzas | 45 |
| `m1_06` | Plantas a un lado, animales al otro | Clasificación · por reino | 50 |

La primera misión pide dos categorías sobre una hoja. Se resuelve en menos de un minuto y termina con una ficha guardada: el niño ve inmediatamente que lo que hace queda registrado.

### Nivel 2 · Investigador

| Id | Título | Tipo | XP |
|---|---|---|---|
| `m2_01` | Bajo el agua | Observación (+ textura) | 45 |
| `m2_02` | El sol de los jardines | Observación (+ simetría) | 50 |
| `m2_03` | Un caparazón que brilla | Observación (5 categorías) | 50 |
| `m2_04` | ¿Qué hace insecto a un insecto? | Comparación · semejanzas | 55 |
| `m2_05` | Lo que solo tiene el caracol | Comparación · diferencias | 55 |
| `m2_06` | Ordena por simetría | Clasificación · por simetría | 60 |

### Nivel 3 · Descubridor

| Id | Título | Tipo | XP |
|---|---|---|---|
| `m3_01` | El mundo del microscopio | Observación (9 categorías) | 60 |
| `m3_02` | Ojos rojos en la hoja | Observación (9 categorías) | 60 |
| `m3_03` | El patrón de la estrella | Patrón | 65 |
| `m3_04` | Hecho de hilos | Patrón | 65 |
| `m3_05` | Vivo o no vivo | Clasificación · ser vivo | 70 |
| `m3_06` | Rana contra pez | Comparación · diferencias | 70 |
| `m3_07` | La regla de los insectos | Patrón | 80 |

`m3_07` es el cierre de la expedición: al encontrar el patrón completo (seis patas, antenas, ojos, simetría bilateral, cuerpo segmentado), el niño ha construido por sí mismo la definición de insecto a partir de tres ejemplos.

## Los 5 criterios de clasificación

Cada criterio es una función que **deduce** el grupo correcto de los datos de la muestra:

| Criterio | Grupos | Cómo se decide |
|---|---|---|
| Por simetría | bilateral · radial · otra | Presencia de `simetria_bilateral` o `simetria_radial` |
| Ser vivo | vivo · no vivo | Presencia de `rasgo_no_es_ser_vivo` o `rasgo_construido_por_animal` |
| Por reino | planta · animal · otro | Campo `reino` de la muestra |
| Por tamaño | diminuto · cabe en la mano · grande | Rasgo de la categoría tamaño |
| Por estructura | partes repetidas · hecha de hilos · compacta | Rasgos de estructura |

Como el grupo se calcula, añadir una muestra nueva al catálogo la clasifica automáticamente en todos los criterios, sin tocar ninguna misión.

## Las 10 insignias

| Insignia | Se consigue con |
|---|---|
| Explorador atento | 3 observaciones |
| Detective de detalles | 5 misiones con 3 estrellas |
| Maestro de las formas | 25 características distintas registradas |
| Observador de estructuras | las 9 categorías exploradas |
| Comparador experto | 4 comparaciones |
| Clasificador certero | 3 clasificaciones |
| Cazador de patrones | 3 patrones |
| Cuaderno de campo | 8 fichas guardadas |
| Gran descubridor | 10 descubrimientos |
| Expedición constante | investigar 4 días distintos |

Las metas están calibradas contra el contenido real: hay exactamente 4 comparaciones, 3 clasificaciones y 3 patrones en la expedición, de modo que completarla concede esas tres insignias. «Gran descubridor» y «Maestro de las formas» exigen salir de las misiones y explorar el muestrario por cuenta propia.

`ContenidoSemillaTest` verifica que estas metas son alcanzables con el contenido incluido.

## Diseño del feedback

Tres reglas, comprobadas por tests:

1. **Nunca solo «correcto» o «incorrecto».** El mensaje nombra la muestra, cuantifica lo acertado y cita una característica concreta.
2. **La pista señala la categoría, no la respuesta.** Si se escapó `numero_6`, la pista dice «Pista sobre número: cuenta las partes que se repiten».
3. **Nunca se descalifica al niño.** `MotorFeedbackTest` comprueba que en ningún caso aparecen palabras como «mal», «fallaste» o «no sabes». El error se enuncia como información («Marcaste 2 características que esta muestra no tiene») y siempre se puede reintentar.

Además, la pantalla de resultado desglosa el registro en tres bloques con **forma e icono distintos**, no solo color: lo observado bien (✓), lo que se escapó (!) y lo que no correspondía (×).

## Ampliar el contenido

Para añadir una muestra nueva:

1. Añade una entrada a `MuestrasSemilla.todas` con sus rasgos y distractores.
2. Añade una constante a `ClaveIlustracion` y su función de dibujo en `IlustracionMuestra.kt`.
3. Ejecuta `./gradlew testDebugUnitTest`.

`ContenidoSemillaTest` comprobará automáticamente que todos los identificadores existen, que ningún distractor contradice un rasgo verdadero, que declara forma, color, tamaño y simetría, y que tiene suficientes características para ser observable. La muestra queda clasificada sola en los cinco criterios.

Para una misión nueva, añádela a `MisionesSemilla.todas`; los tests verificarán que su respuesta existe, que no es trivial y que la cadena de requisitos sigue sin ciclos.

> Al añadir contenido, borra los datos de la aplicación o desinstálala antes de volver a instalarla: la siembra solo se ejecuta cuando la tabla `muestra` está vacía.
