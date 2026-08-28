package com.ojocientifico.app.domain.logica

import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.RasgoFallado

/** Una muestra propuesta para volver a observar, con el motivo. */
data class SugerenciaRepaso(
    val muestraId: String,
    val vecesFalladas: Int,
    val categorias: List<CategoriaRasgo>,
    val rasgos: List<String>
) {
    val motivo: String
        get() = when {
            categorias.size == 1 -> "Se te escapó algo en ${categorias.first().etiqueta.lowercase()}"
            categorias.size > 1 -> "Quedaron detalles en ${categorias.size} categorías"
            else -> "Vuelve a mirarla con calma"
        }
}

/**
 * Construye el plan de "Vuelve a observar" a partir de los fallos realmente
 * persistidos. No emite juicios sobre el niño: solo señala qué muestra conviene
 * volver a mirar y en qué categoría.
 */
object PlanificadorRepaso {

    const val MAXIMO_SUGERENCIAS = 6

    fun sugerencias(
        fallos: List<RasgoFallado>,
        limite: Int = MAXIMO_SUGERENCIAS
    ): List<SugerenciaRepaso> {
        if (fallos.isEmpty() || limite <= 0) return emptyList()
        return fallos
            .groupBy { it.muestraId }
            .map { (muestraId, lista) ->
                val rasgos = lista.sortedByDescending { it.veces }.map { it.opcionId }.distinct()
                SugerenciaRepaso(
                    muestraId = muestraId,
                    vecesFalladas = lista.sumOf { it.veces },
                    categorias = rasgos.mapNotNull { CatalogoRasgos.categoriaDe(it) }
                        .distinct()
                        .sortedBy { it.ordinal },
                    rasgos = rasgos
                )
            }
            .sortedWith(
                compareByDescending<SugerenciaRepaso> { it.vecesFalladas }
                    .thenBy { it.muestraId }
            )
            .take(limite)
    }

    /** Categoría en la que más se está fallando, para orientar el repaso. */
    fun categoriaMasFloja(fallos: List<RasgoFallado>): CategoriaRasgo? =
        fallos.groupBy { CatalogoRasgos.categoriaDe(it.opcionId) }
            .filterKeys { it != null }
            .maxByOrNull { (_, lista) -> lista.sumOf { it.veces } }
            ?.key

    /** Categorías que el explorador ya ha tocado alguna vez. */
    fun categoriasExploradas(rasgosRegistrados: Collection<String>): Int =
        rasgosRegistrados.mapNotNull { CatalogoRasgos.categoriaDe(it) }.distinct().size
}
