package com.ojocientifico.app.domain.model

/** Rango del explorador. Determina qué contenido está desbloqueado. */
enum class RangoExplorador(
    val nivel: Int,
    val titulo: String,
    val lema: String,
    val xpNecesario: Int
) {
    OBSERVADOR(1, "Observador", "Aprendes a mirar con atención.", 0),
    INVESTIGADOR(2, "Investigador", "Comparas, clasificas y registras.", 200),
    DESCUBRIDOR(3, "Descubridor", "Analizas rasgos y encuentras patrones.", 520);

    companion object {
        fun paraXp(xp: Int): RangoExplorador =
            entries.last { xp >= it.xpNecesario }

        fun porNivel(nivel: Int): RangoExplorador =
            entries.firstOrNull { it.nivel == nivel } ?: OBSERVADOR
    }
}

/** Tipo de actividad científica de una misión. */
enum class TipoMision(val etiqueta: String, val verbo: String) {
    OBSERVACION("Observación", "Observa y registra"),
    COMPARACION("Comparación", "Compara las muestras"),
    CLASIFICACION("Clasificación", "Ordena en grupos"),
    PATRON("Patrón", "Encuentra lo que se repite")
}

/** Qué se pide comparar entre dos muestras. */
enum class ModoComparacion(val etiqueta: String, val consigna: String) {
    SEMEJANZAS("¿En qué se parecen?", "Marca las características que tienen las DOS muestras."),
    DIFERENCIAS("¿Qué cambia?", "Marca las características que solo tiene la primera muestra.")
}

/**
 * Criterio con el que se reparten las muestras en grupos.
 * El grupo correcto de cada muestra se CALCULA a partir de sus datos reales,
 * nunca se guarda escrito a mano.
 */
enum class CriterioClasificacion(
    val etiqueta: String,
    val consigna: String,
    val grupos: List<GrupoClasificacion>
) {
    POR_SIMETRIA(
        "Según su simetría",
        "Coloca cada muestra según cómo se reparten sus partes.",
        listOf(
            GrupoClasificacion("bilateral", "Simetría bilateral", "Dos mitades iguales."),
            GrupoClasificacion("radial", "Simetría radial", "Partes que salen del centro."),
            GrupoClasificacion("otra", "Espiral o sin simetría", "Ni bilateral ni radial.")
        )
    ),
    SER_VIVO(
        "¿Ser vivo o no?",
        "Separa lo que está vivo de lo que no lo está.",
        listOf(
            GrupoClasificacion("vivo", "Es un ser vivo", "Nace, crece y se reproduce."),
            GrupoClasificacion("no_vivo", "No es un ser vivo", "No nace ni crece por sí mismo.")
        )
    ),
    POR_REINO(
        "Según su grupo",
        "Agrupa las muestras por el gran grupo al que pertenecen.",
        listOf(
            GrupoClasificacion("planta", "Plantas", "Fabrican su alimento con la luz."),
            GrupoClasificacion("animal", "Animales", "Se mueven y se alimentan de otros."),
            GrupoClasificacion("otro", "Otros", "Hongos, minerales y construcciones.")
        )
    ),
    POR_TAMANO(
        "Según su tamaño",
        "Ordena las muestras por el tamaño que tienen en la realidad.",
        listOf(
            GrupoClasificacion("diminuto", "Diminuto", "Se ve mal a simple vista."),
            GrupoClasificacion("mano", "Cabe en la mano", "Pequeño o mediano."),
            GrupoClasificacion("grande", "Grande", "Más que tus dos manos.")
        )
    ),
    POR_ESTRUCTURA(
        "Según su estructura",
        "Fíjate en cómo está construida cada muestra.",
        listOf(
            GrupoClasificacion("repetida", "Partes repetidas", "Segmentos, capas o celdas."),
            GrupoClasificacion("hilos", "Hecha de hilos", "Filamentos o redes."),
            GrupoClasificacion("compacta", "Compacta", "Maciza, cristalina o de una pieza.")
        )
    );

    /** Grupo verdadero de una muestra según este criterio. */
    fun grupoDe(muestra: Muestra): String = when (this) {
        POR_SIMETRIA -> when {
            "simetria_bilateral" in muestra.rasgos -> "bilateral"
            "simetria_radial" in muestra.rasgos -> "radial"
            else -> "otra"
        }

        SER_VIVO -> if ("rasgo_no_es_ser_vivo" in muestra.rasgos ||
            "rasgo_construido_por_animal" in muestra.rasgos
        ) "no_vivo" else "vivo"

        POR_REINO -> when (muestra.reino) {
            ReinoMuestra.PLANTA -> "planta"
            ReinoMuestra.ANIMAL -> "animal"
            else -> "otro"
        }

        POR_TAMANO -> when {
            "tamano_microscopico" in muestra.rasgos || "tamano_muy_pequeno" in muestra.rasgos -> "diminuto"
            "tamano_grande" in muestra.rasgos -> "grande"
            else -> "mano"
        }

        POR_ESTRUCTURA -> when {
            muestra.rasgos.any {
                it in setOf("estructura_segmentada", "estructura_capas", "estructura_celular")
            } -> "repetida"

            muestra.rasgos.any {
                it in setOf("estructura_filamentos", "estructura_red")
            } -> "hilos"

            else -> "compacta"
        }
    }
}

/** Un contenedor donde el niño deposita muestras al clasificar. */
data class GrupoClasificacion(
    val id: String,
    val etiqueta: String,
    val descripcion: String
)

/** Estado de una misión para el explorador actual. */
enum class EstadoMision { BLOQUEADA, DISPONIBLE, COMPLETADA }

/**
 * Una misión concreta del cuaderno de expedición.
 *
 * [muestrasIds] identifica las muestras implicadas; [categorias] limita qué se
 * observa (las misiones iniciales piden menos categorías).
 */
data class Mision(
    val id: String,
    val titulo: String,
    val consigna: String,
    val instruccionGuia: String,
    val tipo: TipoMision,
    val nivel: Int,
    val orden: Int,
    val muestrasIds: List<String>,
    val categorias: List<CategoriaRasgo> = emptyList(),
    val modoComparacion: ModoComparacion? = null,
    val criterio: CriterioClasificacion? = null,
    val xpBase: Int = 40,
    val requiere: String? = null
) {
    val rango: RangoExplorador get() = RangoExplorador.porNivel(nivel)
}
