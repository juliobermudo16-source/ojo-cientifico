package com.ojocientifico.app.domain.model

/**
 * Una ficha del cuaderno científico: lo que el niño registró sobre una muestra
 * en un momento concreto. Se guarda siempre en la base de datos.
 */
data class FichaCientifica(
    val id: Long = 0L,
    val muestraId: String,
    val misionId: String?,
    val fechaMillis: Long,
    val aciertos: Int,
    val totalEsperado: Int,
    val marcasDeMas: Int,
    val estrellas: Int,
    val notaDelExplorador: String,
    val rasgosRegistrados: List<String>
) {
    val precision: Float =
        if (totalEsperado <= 0) 0f else (aciertos.toFloat() / totalEsperado).coerceIn(0f, 1f)

    val completa: Boolean = totalEsperado > 0 && aciertos == totalEsperado && marcasDeMas == 0
}

/** Característica que el niño no logró observar, para el módulo de repaso. */
data class RasgoFallado(
    val muestraId: String,
    val opcionId: String,
    val veces: Int,
    val ultimaFechaMillis: Long
)

/** Tarjeta de la colección: se desbloquea observando bien una muestra. */
data class Descubrimiento(
    val muestraId: String,
    val desbloqueado: Boolean,
    val fechaMillis: Long?
)

/** Tipo de actividad guardada en el historial. */
enum class TipoActividad { OBSERVACION, COMPARACION, CLASIFICACION, PATRON, REPASO }

/** Una entrada del historial de expedición. */
data class EntradaHistorial(
    val id: Long = 0L,
    val tipo: TipoActividad,
    val referencia: String,
    val aciertos: Int,
    val fallos: Int,
    val estrellas: Int,
    val xpGanado: Int,
    val fechaMillis: Long
)

/** Preferencias del explorador. No contiene ningún dato personal. */
data class Configuracion(
    val alias: String = "Explorador",
    val avatar: Int = 0,
    val sonidoActivo: Boolean = true,
    val vibracionActiva: Boolean = true,
    val animacionesActivas: Boolean = true,
    val textoGrande: Boolean = false,
    val altoContraste: Boolean = false,
    val onboardingHecho: Boolean = false
)
