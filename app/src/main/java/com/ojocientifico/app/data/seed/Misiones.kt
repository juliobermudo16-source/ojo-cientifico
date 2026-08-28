package com.ojocientifico.app.data.seed

import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.ModoComparacion
import com.ojocientifico.app.domain.model.TipoMision

/**
 * Las 19 misiones de la expedición, encadenadas en tres rangos.
 *
 * Nivel 1 — Observador: observación guiada con pocas categorías.
 * Nivel 2 — Investigador: registro completo, comparación y clasificación.
 * Nivel 3 — Descubridor: análisis de rasgos y búsqueda de patrones.
 */
object MisionesSemilla {

    val todas: List<Mision> = listOf(

        // ------------------------- NIVEL 1: OBSERVADOR -------------------------
        Mision(
            id = "m1_01",
            titulo = "Tu primera muestra",
            consigna = "Observa la hoja de helecho y marca su forma y su color.",
            instruccionGuia = "Antes de tocar nada, mira. Empieza por lo más grande: la forma y el color.",
            tipo = TipoMision.OBSERVACION,
            nivel = 1,
            orden = 1,
            muestrasIds = listOf("hoja_helecho"),
            categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR),
            xpBase = 30
        ),
        Mision(
            id = "m1_02",
            titulo = "Alas bajo la lupa",
            consigna = "Registra la forma, los colores y las partes visibles de la mariposa.",
            instruccionGuia = "Usa la lupa y acércate a las alas. ¿Cuántas partes distintas eres capaz de nombrar?",
            tipo = TipoMision.OBSERVACION,
            nivel = 1,
            orden = 2,
            muestrasIds = listOf("mariposa"),
            categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR, CategoriaRasgo.PARTES),
            xpBase = 35,
            requiere = "m1_01"
        ),
        Mision(
            id = "m1_03",
            titulo = "La casa en espiral",
            consigna = "Observa el caracol: su forma, su tamaño y las partes que distingas.",
            instruccionGuia = "Fíjate en cómo se enrolla su concha. Esa forma tiene nombre propio en ciencia.",
            tipo = TipoMision.OBSERVACION,
            nivel = 1,
            orden = 3,
            muestrasIds = listOf("caracol"),
            categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.TAMANO, CategoriaRasgo.PARTES),
            xpBase = 35,
            requiere = "m1_02"
        ),
        Mision(
            id = "m1_04",
            titulo = "Muy pequeña, muy completa",
            consigna = "Haz un registro completo de la hormiga: forma, color, tamaño y partes.",
            instruccionGuia = "Es diminuta, pero tiene tantas partes como un animal grande. Cuenta sus patas.",
            tipo = TipoMision.OBSERVACION,
            nivel = 1,
            orden = 4,
            muestrasIds = listOf("hormiga"),
            categorias = listOf(
                CategoriaRasgo.FORMA, CategoriaRasgo.COLOR,
                CategoriaRasgo.TAMANO, CategoriaRasgo.PARTES
            ),
            xpBase = 40,
            requiere = "m1_03"
        ),
        Mision(
            id = "m1_05",
            titulo = "Dos insectos, un parecido",
            consigna = "Marca las características que la mariposa y la hormiga tienen en común.",
            instruccionGuia = "Parecen muy distintas, ¿verdad? Cuenta sus patas y mira su cabeza otra vez.",
            tipo = TipoMision.COMPARACION,
            nivel = 1,
            orden = 5,
            muestrasIds = listOf("mariposa", "hormiga"),
            categorias = listOf(CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO, CategoriaRasgo.SIMETRIA),
            modoComparacion = ModoComparacion.SEMEJANZAS,
            xpBase = 45,
            requiere = "m1_04"
        ),
        Mision(
            id = "m1_06",
            titulo = "Plantas a un lado, animales al otro",
            consigna = "Coloca cada muestra en el grupo al que pertenece.",
            instruccionGuia = "Una pista: las plantas no se mueven para buscar comida, la fabrican con la luz.",
            tipo = TipoMision.CLASIFICACION,
            nivel = 1,
            orden = 6,
            muestrasIds = listOf("hoja_helecho", "girasol", "mariposa", "hormiga", "caracol", "pez_payaso"),
            criterio = CriterioClasificacion.POR_REINO,
            xpBase = 50,
            requiere = "m1_05"
        ),

        // ----------------------- NIVEL 2: INVESTIGADOR -----------------------
        Mision(
            id = "m2_01",
            titulo = "Bajo el agua",
            consigna = "Registra forma, color, textura y partes del pez payaso.",
            instruccionGuia = "Ahora añadimos la textura. Imagina cómo se sentiría su piel si pudieras tocarla.",
            tipo = TipoMision.OBSERVACION,
            nivel = 2,
            orden = 1,
            muestrasIds = listOf("pez_payaso"),
            categorias = listOf(
                CategoriaRasgo.FORMA, CategoriaRasgo.COLOR,
                CategoriaRasgo.TEXTURA, CategoriaRasgo.PARTES
            ),
            xpBase = 45
        ),
        Mision(
            id = "m2_02",
            titulo = "El sol de los jardines",
            consigna = "Ficha completa del girasol, incluida su simetría.",
            instruccionGuia = "Traza una línea imaginaria por el centro. ¿Da igual por dónde la traces?",
            tipo = TipoMision.OBSERVACION,
            nivel = 2,
            orden = 2,
            muestrasIds = listOf("girasol"),
            categorias = listOf(
                CategoriaRasgo.FORMA, CategoriaRasgo.COLOR, CategoriaRasgo.TEXTURA,
                CategoriaRasgo.SIMETRIA, CategoriaRasgo.TAMANO, CategoriaRasgo.PARTES
            ),
            xpBase = 50,
            requiere = "m2_01"
        ),
        Mision(
            id = "m2_03",
            titulo = "Un caparazón que brilla",
            consigna = "Observa el escarabajo con todo el detalle que puedas.",
            instruccionGuia = "Su color cambia según cómo le da la luz. Mira dos veces antes de decidir.",
            tipo = TipoMision.OBSERVACION,
            nivel = 2,
            orden = 3,
            muestrasIds = listOf("escarabajo"),
            categorias = listOf(
                CategoriaRasgo.FORMA, CategoriaRasgo.COLOR, CategoriaRasgo.TEXTURA,
                CategoriaRasgo.SIMETRIA, CategoriaRasgo.PARTES
            ),
            xpBase = 50,
            requiere = "m2_02"
        ),
        Mision(
            id = "m2_04",
            titulo = "¿Qué hace insecto a un insecto?",
            consigna = "Marca lo que comparten la mariposa y el escarabajo.",
            instruccionGuia = "Los científicos agrupan animales por lo que comparten, no por lo que parecen.",
            tipo = TipoMision.COMPARACION,
            nivel = 2,
            orden = 4,
            muestrasIds = listOf("mariposa", "escarabajo"),
            categorias = listOf(CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO, CategoriaRasgo.SIMETRIA),
            modoComparacion = ModoComparacion.SEMEJANZAS,
            xpBase = 55,
            requiere = "m2_03"
        ),
        Mision(
            id = "m2_05",
            titulo = "Lo que solo tiene el caracol",
            consigna = "Marca lo que tiene el caracol y NO tiene el escarabajo.",
            instruccionGuia = "Los dos llevan una cubierta encima, pero no están hechas igual ni crecen igual.",
            tipo = TipoMision.COMPARACION,
            nivel = 2,
            orden = 5,
            muestrasIds = listOf("caracol", "escarabajo"),
            categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.TEXTURA, CategoriaRasgo.ESTRUCTURA),
            modoComparacion = ModoComparacion.DIFERENCIAS,
            xpBase = 55,
            requiere = "m2_04"
        ),
        Mision(
            id = "m2_06",
            titulo = "Ordena por simetría",
            consigna = "Reparte las muestras según cómo se colocan sus partes.",
            instruccionGuia = "Bilateral: dos mitades iguales. Radial: todo sale del centro. Y hay una que no encaja en ninguna.",
            tipo = TipoMision.CLASIFICACION,
            nivel = 2,
            orden = 6,
            muestrasIds = listOf("pez_payaso", "escarabajo", "caracol", "seta", "cristal_cuarzo", "estrella_mar"),
            criterio = CriterioClasificacion.POR_SIMETRIA,
            xpBase = 60,
            requiere = "m2_05"
        ),

        // ------------------------ NIVEL 3: DESCUBRIDOR ------------------------
        Mision(
            id = "m3_01",
            titulo = "El mundo del microscopio",
            consigna = "Ficha completa de la célula vegetal.",
            instruccionGuia = "Aquí las reglas cambian: nada tiene patas ni alas. Busca paredes, centros y repeticiones.",
            tipo = TipoMision.OBSERVACION,
            nivel = 3,
            orden = 1,
            muestrasIds = listOf("celula_vegetal"),
            categorias = CategoriaRasgo.entries.toList(),
            xpBase = 60
        ),
        Mision(
            id = "m3_02",
            titulo = "Ojos rojos en la hoja",
            consigna = "Ficha completa de la rana arborícola.",
            instruccionGuia = "Tiene dos estrategias opuestas a la vez: esconderse y llamar la atención. Marca las dos.",
            tipo = TipoMision.OBSERVACION,
            nivel = 3,
            orden = 2,
            muestrasIds = listOf("rana_arboricola"),
            categorias = CategoriaRasgo.entries.toList(),
            xpBase = 60,
            requiere = "m3_01"
        ),
        Mision(
            id = "m3_03",
            titulo = "El patrón de la estrella",
            consigna = "Encuentra lo que comparten estas tres muestras tan distintas.",
            instruccionGuia = "Un copo, una estrella y una flor. Nada que ver entre ellos... salvo una cosa.",
            tipo = TipoMision.PATRON,
            nivel = 3,
            orden = 3,
            muestrasIds = listOf("copo_nieve", "estrella_mar", "girasol"),
            categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.SIMETRIA, CategoriaRasgo.NUMERO),
            xpBase = 65,
            requiere = "m3_02"
        ),
        Mision(
            id = "m3_04",
            titulo = "Hecho de hilos",
            consigna = "Descubre qué tienen en común la telaraña, la semilla y la pluma.",
            instruccionGuia = "Tres cosas ligerísimas. Pregúntate de qué están hechas por dentro.",
            tipo = TipoMision.PATRON,
            nivel = 3,
            orden = 4,
            muestrasIds = listOf("telarana", "semilla_viento", "pluma"),
            categorias = listOf(
                CategoriaRasgo.ESTRUCTURA, CategoriaRasgo.TEXTURA, CategoriaRasgo.COLOR
            ),
            xpBase = 65,
            requiere = "m3_03"
        ),
        Mision(
            id = "m3_05",
            titulo = "Vivo o no vivo",
            consigna = "Separa lo que está vivo de lo que no lo está.",
            instruccionGuia = "Cuidado: algo puede moverse, crecer o parecer vivo sin serlo. Piensa si nace y se reproduce.",
            tipo = TipoMision.CLASIFICACION,
            nivel = 3,
            orden = 5,
            muestrasIds = listOf(
                "cristal_cuarzo", "copo_nieve", "telarana",
                "rana_arboricola", "celula_vegetal", "hoja_helecho"
            ),
            criterio = CriterioClasificacion.SER_VIVO,
            xpBase = 70,
            requiere = "m3_04"
        ),
        Mision(
            id = "m3_06",
            titulo = "Rana contra pez",
            consigna = "Marca lo que tiene la rana y NO tiene el pez payaso.",
            instruccionGuia = "Los dos viven cerca del agua, pero se mueven de forma muy distinta.",
            tipo = TipoMision.COMPARACION,
            nivel = 3,
            orden = 6,
            muestrasIds = listOf("rana_arboricola", "pez_payaso"),
            categorias = listOf(CategoriaRasgo.TEXTURA, CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO),
            modoComparacion = ModoComparacion.DIFERENCIAS,
            xpBase = 70,
            requiere = "m3_05"
        ),
        Mision(
            id = "m3_07",
            titulo = "La regla de los insectos",
            consigna = "Encuentra TODO lo que comparten la hormiga, la mariposa y el escarabajo.",
            instruccionGuia = "Si encuentras el patrón completo, habrás descubierto tú solo la definición de insecto.",
            tipo = TipoMision.PATRON,
            nivel = 3,
            orden = 7,
            muestrasIds = listOf("hormiga", "mariposa", "escarabajo"),
            categorias = listOf(
                CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO,
                CategoriaRasgo.SIMETRIA, CategoriaRasgo.ESTRUCTURA
            ),
            xpBase = 80,
            requiere = "m3_06"
        )
    )

    private val indice = todas.associateBy { it.id }

    fun porId(id: String): Mision? = indice[id]

    fun deNivel(nivel: Int): List<Mision> = todas.filter { it.nivel == nivel }
}
