package com.ojocientifico.app.domain.model

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Resultado de una actividad en la que el niño MARCA características.
 * Sirve para observación, comparación y búsqueda de patrones.
 *
 * Todo se calcula comparando la selección con el conjunto verdadero: no hay
 * textos ni puntuaciones escritas a mano.
 */
data class ResultadoSeleccion(
    val esperados: Set<String>,
    val seleccionados: Set<String>
) {
    /** Características correctas que el niño sí marcó. */
    val aciertos: Set<String> = esperados intersect seleccionados

    /** Características correctas que se le pasaron por alto. */
    val omitidos: Set<String> = esperados - seleccionados

    /** Características que marcó pero que la muestra no tiene. */
    val falsosPositivos: Set<String> = seleccionados - esperados

    /**
     * Precisión entre 0 y 1. Cada característica marcada de más resta media
     * característica: observar de más también es un error, pero pesa menos que
     * no observar.
     */
    val precision: Float = run {
        val total = esperados.size
        if (total == 0) {
            if (seleccionados.isEmpty()) 1f else 0f
        } else {
            val base = aciertos.size.toFloat() / total
            val castigo = falsosPositivos.size * 0.5f / max(1, total)
            (base - castigo).coerceIn(0f, 1f)
        }
    }

    val estrellas: Int = estrellasPara(precision)

    val perfecto: Boolean = omitidos.isEmpty() && falsosPositivos.isEmpty()

    val porcentaje: Int = (precision * 100).roundToInt()

    companion object {
        fun estrellasPara(precision: Float): Int = when {
            precision >= 0.95f -> 3
            precision >= 0.75f -> 2
            precision >= 0.50f -> 1
            else -> 0
        }
    }
}

/**
 * Resultado de repartir muestras en grupos.
 * [esperado] se calcula con [CriterioClasificacion.grupoDe], nunca se escribe a mano.
 */
data class ResultadoClasificacion(
    val esperado: Map<String, String>,
    val asignado: Map<String, String>
) {
    val correctas: Set<String> =
        esperado.filter { (muestra, grupo) -> asignado[muestra] == grupo }.keys

    val incorrectas: Set<String> =
        asignado.filter { (muestra, grupo) -> esperado[muestra] != null && esperado[muestra] != grupo }.keys

    val sinAsignar: Set<String> = esperado.keys - asignado.keys

    val precision: Float =
        if (esperado.isEmpty()) 0f else correctas.size.toFloat() / esperado.size

    val estrellas: Int = ResultadoSeleccion.estrellasPara(precision)

    val perfecto: Boolean = esperado.isNotEmpty() && correctas.size == esperado.size

    val porcentaje: Int = (precision * 100).roundToInt()

    /** Para cada muestra mal colocada: en qué grupo la puso y cuál era el correcto. */
    fun correcciones(): Map<String, Pair<String, String>> =
        incorrectas.associateWith { muestra ->
            (asignado[muestra] ?: "") to (esperado[muestra] ?: "")
        }
}

/** Tono del mensaje que devuelve el motor de feedback. */
enum class TonoFeedback { EXCELENTE, BIEN, CASI, REINTENTAR }

/**
 * Mensaje educativo devuelto tras una actividad. Nunca es un simple
 * "correcto" / "incorrecto": siempre nombra lo observado y sugiere dónde mirar.
 */
data class Feedback(
    val tono: TonoFeedback,
    val titulo: String,
    val mensaje: String,
    val pista: String? = null,
    val puedeReintentar: Boolean = false
)

/** Resumen de lo ocurrido al terminar una misión. */
data class ResumenMision(
    val misionId: String,
    val estrellas: Int,
    val precision: Float,
    val xpGanado: Int,
    val feedback: Feedback,
    val insigniasNuevas: List<Insignia> = emptyList(),
    val descubrimientosNuevos: List<String> = emptyList(),
    val subioDeRango: RangoExplorador? = null
)
