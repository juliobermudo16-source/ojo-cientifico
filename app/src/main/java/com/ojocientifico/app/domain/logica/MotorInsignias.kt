package com.ojocientifico.app.domain.logica

import com.ojocientifico.app.domain.model.CatalogoInsignias
import com.ojocientifico.app.domain.model.EstadisticasExplorador
import com.ojocientifico.app.domain.model.Insignia

/** Estado de una insignia para mostrarlo en la pantalla de logros. */
data class InsigniaConProgreso(
    val insignia: Insignia,
    val desbloqueada: Boolean,
    val progreso: Int,
    val meta: Int
) {
    val fraccion: Float = if (meta <= 0) 0f else (progreso.toFloat() / meta).coerceIn(0f, 1f)
}

/**
 * Concede insignias únicamente a partir de estadísticas reales.
 * Una insignia ya desbloqueada nunca se retira ni se vuelve a anunciar.
 */
object MotorInsignias {

    /** Insignias que se cumplen ahora y todavía no estaban concedidas. */
    fun nuevas(
        estado: EstadisticasExplorador,
        yaDesbloqueadas: Set<String>
    ): List<Insignia> = CatalogoInsignias.todas
        .filter { it.id !in yaDesbloqueadas && it.conseguida(estado) }

    /** Vista completa del muro de insignias, con avance parcial incluido. */
    fun panel(
        estado: EstadisticasExplorador,
        yaDesbloqueadas: Set<String>
    ): List<InsigniaConProgreso> = CatalogoInsignias.todas.map { insignia ->
        val ganada = insignia.id in yaDesbloqueadas || insignia.conseguida(estado)
        InsigniaConProgreso(
            insignia = insignia,
            desbloqueada = ganada,
            progreso = if (ganada) insignia.meta else insignia.progreso(estado),
            meta = insignia.meta
        )
    }

    fun cuantasDesbloqueadas(yaDesbloqueadas: Set<String>): Int =
        CatalogoInsignias.todas.count { it.id in yaDesbloqueadas }
}
