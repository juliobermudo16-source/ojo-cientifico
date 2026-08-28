package com.ojocientifico.app.domain.logica

import com.ojocientifico.app.domain.model.EstadoMision
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.RangoExplorador
import kotlin.math.roundToInt

/**
 * Reglas de experiencia. El XP nunca se regala: depende de la precisión real de
 * la actividad y se reduce al repetir una misión ya superada.
 */
object SistemaXp {

    const val BONUS_TRES_ESTRELLAS = 15
    const val BONUS_DOS_ESTRELLAS = 5
    private const val FACTOR_REPETICION = 0.35f

    /**
     * @param xpBase valor de la misión.
     * @param precision resultado real de la actividad, entre 0 y 1.
     * @param estrellas estrellas obtenidas (0..3).
     * @param yaCompletada si la misión ya estaba superada, el XP se reduce para
     *        que repetir sirva para practicar, no para inflar el nivel.
     */
    fun calcular(
        xpBase: Int,
        precision: Float,
        estrellas: Int,
        yaCompletada: Boolean = false
    ): Int {
        if (xpBase <= 0) return 0
        val limpia = precision.coerceIn(0f, 1f)
        if (limpia <= 0f) return 0
        val bonus = when (estrellas) {
            3 -> BONUS_TRES_ESTRELLAS
            2 -> BONUS_DOS_ESTRELLAS
            else -> 0
        }
        val bruto = xpBase * limpia + bonus
        val ajustado = if (yaCompletada) bruto * FACTOR_REPETICION else bruto
        return ajustado.roundToInt().coerceAtLeast(1)
    }
}

/** Traduce el XP acumulado en rango, avance dentro del rango y desbloqueos. */
object CalculadoraProgreso {

    fun rango(xp: Int): RangoExplorador = RangoExplorador.paraXp(xp.coerceAtLeast(0))

    fun siguienteRango(xp: Int): RangoExplorador? {
        val actual = rango(xp)
        return RangoExplorador.entries.firstOrNull { it.nivel == actual.nivel + 1 }
    }

    /** XP que falta para el siguiente rango, o 0 si ya es el máximo. */
    fun xpRestante(xp: Int): Int {
        val siguiente = siguienteRango(xp) ?: return 0
        return (siguiente.xpNecesario - xp).coerceAtLeast(0)
    }

    /** Avance dentro del rango actual, entre 0 y 1. */
    fun fraccionRango(xp: Int): Float {
        val actual = rango(xp)
        val siguiente = siguienteRango(xp) ?: return 1f
        val tramo = (siguiente.xpNecesario - actual.xpNecesario).toFloat()
        if (tramo <= 0f) return 1f
        return ((xp - actual.xpNecesario) / tramo).coerceIn(0f, 1f)
    }

    /** Detecta si un incremento de XP hace subir de rango. */
    fun rangoGanado(xpAntes: Int, xpDespues: Int): RangoExplorador? {
        val antes = rango(xpAntes)
        val despues = rango(xpDespues)
        return if (despues.nivel > antes.nivel) despues else null
    }
}

/**
 * Decide qué misiones están bloqueadas, disponibles o completadas.
 * Una misión se abre cuando el rango alcanza su nivel y su misión previa
 * (si la tiene) está superada.
 */
object DesbloqueoMisiones {

    fun estado(
        mision: Mision,
        xp: Int,
        completadas: Set<String>
    ): EstadoMision = when {
        mision.id in completadas -> EstadoMision.COMPLETADA
        CalculadoraProgreso.rango(xp).nivel < mision.nivel -> EstadoMision.BLOQUEADA
        mision.requiere != null && mision.requiere !in completadas -> EstadoMision.BLOQUEADA
        else -> EstadoMision.DISPONIBLE
    }

    fun estados(
        misiones: List<Mision>,
        xp: Int,
        completadas: Set<String>
    ): Map<String, EstadoMision> =
        misiones.associate { it.id to estado(it, xp, completadas) }

    /** Primera misión no completada que el explorador ya puede hacer. */
    fun siguienteDisponible(
        misiones: List<Mision>,
        xp: Int,
        completadas: Set<String>
    ): Mision? = misiones
        .sortedWith(compareBy({ it.nivel }, { it.orden }))
        .firstOrNull { estado(it, xp, completadas) == EstadoMision.DISPONIBLE }

    /** Porcentaje de la expedición completado, entre 0 y 1. */
    fun avanceTotal(misiones: List<Mision>, completadas: Set<String>): Float =
        if (misiones.isEmpty()) 0f
        else misiones.count { it.id in completadas }.toFloat() / misiones.size
}
