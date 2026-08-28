package com.ojocientifico.app.domain.logica

import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.Feedback
import com.ojocientifico.app.domain.model.ResultadoClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import com.ojocientifico.app.domain.model.TonoFeedback

/**
 * Convierte un resultado en un mensaje educativo.
 *
 * Regla del producto: nunca decir solo "correcto" o "incorrecto". Cada mensaje
 * nombra algo concreto que el niño observó y, si falta algo, señala dónde
 * mirar sin dar la respuesta hecha.
 */
object MotorFeedback {

    private fun tono(precision: Float, perfecto: Boolean): TonoFeedback = when {
        perfecto -> TonoFeedback.EXCELENTE
        precision >= 0.75f -> TonoFeedback.BIEN
        precision >= 0.5f -> TonoFeedback.CASI
        else -> TonoFeedback.REINTENTAR
    }

    private fun titulo(tono: TonoFeedback): String = when (tono) {
        TonoFeedback.EXCELENTE -> "¡Observación completa!"
        TonoFeedback.BIEN -> "¡Buen ojo!"
        TonoFeedback.CASI -> "Vas por buen camino"
        TonoFeedback.REINTENTAR -> "Vuelve a mirar con calma"
    }

    /** Feedback de un registro morfológico sobre una muestra concreta. */
    fun paraObservacion(resultado: ResultadoSeleccion, nombreMuestra: String): Feedback {
        val t = tono(resultado.precision, resultado.perfecto)
        val cuerpo = buildString {
            if (resultado.aciertos.isNotEmpty()) {
                append("Registraste ${resultado.aciertos.size} de ${resultado.esperados.size} características de $nombreMuestra")
                val ejemplo = resultado.aciertos.minByOrNull { CatalogoRasgos.etiqueta(it) }
                if (ejemplo != null) {
                    append(", entre ellas «${CatalogoRasgos.etiqueta(ejemplo).lowercase()}»")
                }
                append(". ")
            } else {
                append("Todavía no coincide ninguna característica con las de $nombreMuestra. ")
            }
            when {
                resultado.perfecto ->
                    append("No se te escapó ningún detalle: así trabaja un científico.")

                resultado.omitidos.isNotEmpty() && resultado.falsosPositivos.isNotEmpty() ->
                    append("Quedan ${resultado.omitidos.size} por descubrir y marcaste ${resultado.falsosPositivos.size} que esta muestra no tiene.")

                resultado.omitidos.isNotEmpty() ->
                    append("Aún quedan ${resultado.omitidos.size} características por descubrir.")

                else ->
                    append("Marcaste ${resultado.falsosPositivos.size} características que esta muestra no tiene. Observar de menos también es observar mejor.")
            }
        }
        return Feedback(
            tono = t,
            titulo = titulo(t),
            mensaje = cuerpo,
            pista = pistaDe(resultado),
            puedeReintentar = !resultado.perfecto
        )
    }

    /** Feedback de una comparación entre dos muestras. */
    fun paraComparacion(
        resultado: ResultadoSeleccion,
        nombreA: String,
        nombreB: String,
        buscabaSemejanzas: Boolean
    ): Feedback {
        val t = tono(resultado.precision, resultado.perfecto)
        val que = if (buscabaSemejanzas) "en común" else "que las distinguen"
        val cuerpo = buildString {
            append("Encontraste ${resultado.aciertos.size} de ${resultado.esperados.size} características $que entre $nombreA y $nombreB. ")
            if (resultado.perfecto) {
                append("Comparar es mirar dos cosas a la vez, y lo hiciste sin perder detalle.")
            } else if (resultado.omitidos.isNotEmpty()) {
                val cat = resultado.omitidos.firstNotNullOfOrNull { CatalogoRasgos.categoriaDe(it) }
                append("Prueba a fijarte otra vez en ${cat?.etiqueta?.lowercase() ?: "las partes visibles"}: ahí queda algo por ver.")
            } else {
                append("Alguna de tus marcas no aparece en las dos muestras. Compáralas parte por parte.")
            }
        }
        return Feedback(t, titulo(t), cuerpo, pistaDe(resultado), !resultado.perfecto)
    }

    /** Feedback de una búsqueda de patrón común. */
    fun paraPatron(resultado: ResultadoSeleccion, cuantasMuestras: Int): Feedback {
        val t = tono(resultado.precision, resultado.perfecto)
        val cuerpo = buildString {
            if (resultado.perfecto) {
                append("¡Patrón descubierto! Las $cuantasMuestras muestras comparten exactamente esas características, aunque a simple vista parezcan muy distintas.")
            } else {
                append("Un patrón solo cuenta si aparece en las $cuantasMuestras muestras. ")
                if (resultado.falsosPositivos.isNotEmpty()) {
                    val ej = resultado.falsosPositivos.first()
                    append("«${CatalogoRasgos.etiqueta(ej)}» no está en todas. ")
                }
                if (resultado.omitidos.isNotEmpty()) {
                    append("Aún queda algo que sí se repite en todas ellas.")
                }
            }
        }
        return Feedback(t, titulo(t), cuerpo, pistaDe(resultado), !resultado.perfecto)
    }

    /** Feedback de una clasificación en grupos. */
    fun paraClasificacion(
        resultado: ResultadoClasificacion,
        criterio: CriterioClasificacion,
        nombrePorId: (String) -> String
    ): Feedback {
        val t = tono(resultado.precision, resultado.perfecto)
        val cuerpo = buildString {
            append("Colocaste bien ${resultado.correctas.size} de ${resultado.esperado.size} muestras usando el criterio «${criterio.etiqueta.lowercase()}». ")
            when {
                resultado.perfecto ->
                    append("Clasificar bien significa que encontraste la regla escondida detrás del grupo.")

                resultado.sinAsignar.isNotEmpty() ->
                    append("Te quedan ${resultado.sinAsignar.size} muestras sin colocar.")

                else -> {
                    val fallo = resultado.incorrectas.firstOrNull()
                    if (fallo != null) {
                        val grupo = criterio.grupos.firstOrNull { it.id == resultado.esperado[fallo] }
                        append("Vuelve a mirar ${nombrePorId(fallo)}: fíjate en ${grupo?.descripcion?.lowercase() ?: "sus características"}")
                    }
                }
            }
        }
        val pista = resultado.incorrectas.firstOrNull()?.let { fallo ->
            criterio.grupos.firstOrNull { it.id == resultado.esperado[fallo] }
                ?.let { "Pista: ${nombrePorId(fallo)} encaja mejor donde ${it.descripcion.lowercase()}" }
        }
        return Feedback(t, titulo(t), cuerpo, pista, !resultado.perfecto)
    }

    /** Pista construida a partir de la primera característica que falta. */
    private fun pistaDe(resultado: ResultadoSeleccion): String? {
        val olvidado = resultado.omitidos.minByOrNull { CatalogoRasgos.etiqueta(it) } ?: return null
        val opcion = CatalogoRasgos.porId(olvidado) ?: return null
        return "Pista sobre ${opcion.categoria.etiqueta.lowercase()}: ${opcion.pista}"
    }
}
