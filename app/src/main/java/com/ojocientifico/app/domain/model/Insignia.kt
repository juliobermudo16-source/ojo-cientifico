package com.ojocientifico.app.domain.model

/** Dibujo asociado a cada insignia (se pinta con Compose Canvas). */
enum class ClaveInsignia {
    LUPA,
    OJO,
    FORMAS,
    ESTRUCTURA,
    BALANZA,
    HUELLA,
    CUADERNO,
    MEDALLA,
    BRUJULA,
    CRISTAL
}

/**
 * Fotografía del avance real del explorador. Se construye SIEMPRE a partir de
 * los datos persistidos; ninguna insignia se concede sin respaldo en la base.
 */
data class EstadisticasExplorador(
    val xp: Int = 0,
    val misionesCompletadas: Int = 0,
    val misionesPerfectas: Int = 0,
    val fichasRegistradas: Int = 0,
    val observacionesCorrectas: Int = 0,
    val comparacionesCompletadas: Int = 0,
    val clasificacionesCompletadas: Int = 0,
    val patronesEncontrados: Int = 0,
    val descubrimientosDesbloqueados: Int = 0,
    val rasgosDistintosRegistrados: Int = 0,
    val categoriasExploradas: Int = 0,
    val diasDeExpedicion: Int = 0
)

/**
 * Una insignia científica. [meta] es el objetivo numérico y [medida] extrae del
 * estado real el valor a comparar, de modo que el progreso siempre es auténtico.
 */
data class Insignia(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val comoSeGana: String,
    val clave: ClaveInsignia,
    val meta: Int,
    val medida: (EstadisticasExplorador) -> Int
) {
    fun progreso(estado: EstadisticasExplorador): Int = medida(estado).coerceAtMost(meta)

    fun fraccion(estado: EstadisticasExplorador): Float =
        if (meta <= 0) 0f else progreso(estado).toFloat() / meta

    fun conseguida(estado: EstadisticasExplorador): Boolean = medida(estado) >= meta
}

/** Catálogo de insignias de Ojo Científico. */
object CatalogoInsignias {

    val todas: List<Insignia> = listOf(
        Insignia(
            id = "explorador_atento",
            nombre = "Explorador atento",
            descripcion = "Has empezado a mirar de verdad, no solo a ver.",
            comoSeGana = "Completa 3 misiones de observación.",
            clave = ClaveInsignia.LUPA,
            meta = 3,
            medida = { it.observacionesCorrectas }
        ),
        Insignia(
            id = "detective_detalles",
            nombre = "Detective de detalles",
            descripcion = "Ningún detalle pequeño se te escapa.",
            comoSeGana = "Consigue 3 estrellas en 5 misiones.",
            clave = ClaveInsignia.OJO,
            meta = 5,
            medida = { it.misionesPerfectas }
        ),
        Insignia(
            id = "maestro_formas",
            nombre = "Maestro de las formas",
            descripcion = "Reconoces la forma de una muestra de un vistazo.",
            comoSeGana = "Registra 25 características distintas.",
            clave = ClaveInsignia.FORMAS,
            meta = 25,
            medida = { it.rasgosDistintosRegistrados }
        ),
        Insignia(
            id = "observador_estructuras",
            nombre = "Observador de estructuras",
            descripcion = "Ves cómo están construidas las cosas por dentro.",
            comoSeGana = "Explora las 9 categorías de características.",
            clave = ClaveInsignia.ESTRUCTURA,
            meta = 9,
            medida = { it.categoriasExploradas }
        ),
        Insignia(
            id = "comparador_experto",
            nombre = "Comparador experto",
            descripcion = "Encuentras parecidos y diferencias con precisión.",
            comoSeGana = "Termina 4 misiones de comparación.",
            clave = ClaveInsignia.BALANZA,
            meta = 4,
            medida = { it.comparacionesCompletadas }
        ),
        Insignia(
            id = "clasificador_certero",
            nombre = "Clasificador certero",
            descripcion = "Sabes poner cada muestra en su grupo.",
            comoSeGana = "Termina 3 misiones de clasificación.",
            clave = ClaveInsignia.BRUJULA,
            meta = 3,
            medida = { it.clasificacionesCompletadas }
        ),
        Insignia(
            id = "cazador_patrones",
            nombre = "Cazador de patrones",
            descripcion = "Descubres lo que se repite entre muestras distintas.",
            comoSeGana = "Encuentra 3 patrones comunes.",
            clave = ClaveInsignia.CRISTAL,
            meta = 3,
            medida = { it.patronesEncontrados }
        ),
        Insignia(
            id = "cuaderno_completo",
            nombre = "Cuaderno de campo",
            descripcion = "Tu cuaderno ya cuenta una historia.",
            comoSeGana = "Guarda 8 fichas científicas.",
            clave = ClaveInsignia.CUADERNO,
            meta = 8,
            medida = { it.fichasRegistradas }
        ),
        Insignia(
            id = "gran_descubridor",
            nombre = "Gran descubridor",
            descripcion = "Tu colección de descubrimientos impresiona.",
            comoSeGana = "Desbloquea 10 descubrimientos.",
            clave = ClaveInsignia.MEDALLA,
            meta = 10,
            medida = { it.descubrimientosDesbloqueados }
        ),
        Insignia(
            id = "expedicion_constante",
            nombre = "Expedición constante",
            descripcion = "Un buen científico vuelve al laboratorio.",
            comoSeGana = "Investiga en 4 días distintos.",
            clave = ClaveInsignia.HUELLA,
            meta = 4,
            medida = { it.diasDeExpedicion }
        )
    )

    fun porId(id: String): Insignia? = todas.firstOrNull { it.id == id }
}
