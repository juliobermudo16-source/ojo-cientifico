package com.ojocientifico.app.domain

import com.ojocientifico.app.data.seed.MuestrasSemilla
import com.ojocientifico.app.domain.logica.EvaluadorClasificacion
import com.ojocientifico.app.domain.logica.EvaluadorObservacion
import com.ojocientifico.app.domain.logica.MotorFeedback
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import com.ojocientifico.app.domain.model.TonoFeedback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El feedback nunca puede reducirse a "correcto" o "incorrecto", ni humillar al
 * niño. Estos tests fijan ese contrato.
 */
class MotorFeedbackTest {

    private val mariposa = MuestrasSemilla.requerir("mariposa")
    private val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)

    @Test
    fun `una observacion perfecta celebra y no ofrece reintento`() {
        val esperados = EvaluadorObservacion.esperados(mariposa, categorias)
        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, esperados)

        val feedback = MotorFeedback.paraObservacion(resultado, mariposa.nombre)

        assertEquals(TonoFeedback.EXCELENTE, feedback.tono)
        assertFalse(feedback.puedeReintentar)
        assertNull(feedback.pista)
    }

    @Test
    fun `si algo se escapa el feedback trae una pista de esa categoria`() {
        val esperados = EvaluadorObservacion.esperados(mariposa, categorias)
        val incompleta = esperados.drop(1).toSet()
        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, incompleta)

        val feedback = MotorFeedback.paraObservacion(resultado, mariposa.nombre)

        assertNotNull(feedback.pista)
        assertTrue(feedback.puedeReintentar)
        assertTrue(feedback.pista!!.startsWith("Pista sobre "))
    }

    @Test
    fun `el mensaje nombra la muestra y nunca queda vacio`() {
        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, emptySet())
        val feedback = MotorFeedback.paraObservacion(resultado, mariposa.nombre)

        assertTrue(feedback.mensaje.contains(mariposa.nombre))
        assertTrue(feedback.mensaje.length > 20)
        assertTrue(feedback.titulo.isNotBlank())
    }

    @Test
    fun `el feedback nunca usa palabras que descalifiquen al nino`() {
        val prohibidas = listOf("mal", "error tuyo", "fallaste", "tonto", "no sabes", "incapaz")
        val casos = listOf(0f, 0.4f, 0.8f, 1f).map { fraccion ->
            val esperados = EvaluadorObservacion.esperados(mariposa, categorias)
            val cuantos = (esperados.size * fraccion).toInt()
            EvaluadorObservacion.evaluar(mariposa, categorias, esperados.take(cuantos).toSet())
        }

        casos.forEach { resultado ->
            val texto = MotorFeedback.paraObservacion(resultado, mariposa.nombre)
                .let { "${it.titulo} ${it.mensaje} ${it.pista.orEmpty()}" }
                .lowercase()
            prohibidas.forEach { palabra ->
                assertFalse("Feedback inadecuado: contiene «$palabra»", texto.contains(palabra))
            }
        }
    }

    @Test
    fun `el tono baja de forma coherente con la precision`() {
        fun tonoDe(precision: Float): TonoFeedback {
            val esperados = setOf("a", "b", "c", "d")
            val aciertos = esperados.take((esperados.size * precision).toInt()).toSet()
            return MotorFeedback.paraObservacion(
                ResultadoSeleccion(esperados, aciertos), "Muestra"
            ).tono
        }

        assertEquals(TonoFeedback.EXCELENTE, tonoDe(1f))
        assertEquals(TonoFeedback.BIEN, tonoDe(0.75f))
        assertEquals(TonoFeedback.CASI, tonoDe(0.5f))
        assertEquals(TonoFeedback.REINTENTAR, tonoDe(0.25f))
    }

    @Test
    fun `la comparacion menciona las dos muestras`() {
        val hormiga = MuestrasSemilla.requerir("hormiga")
        val resultado = ResultadoSeleccion(setOf("numero_6"), setOf("numero_6"))

        val feedback = MotorFeedback.paraComparacion(
            resultado, mariposa.nombre, hormiga.nombre, buscabaSemejanzas = true
        )

        assertTrue(feedback.mensaje.contains(mariposa.nombre))
        assertTrue(feedback.mensaje.contains(hormiga.nombre))
    }

    @Test
    fun `el patron explica que debe aparecer en todas las muestras`() {
        val resultado = ResultadoSeleccion(setOf("numero_6"), setOf("parte_alas"))
        val feedback = MotorFeedback.paraPatron(resultado, cuantasMuestras = 3)

        assertTrue(feedback.mensaje.contains("3"))
        assertTrue(feedback.puedeReintentar)
    }

    @Test
    fun `la clasificacion senala una muestra concreta cuando hay error`() {
        val muestras = listOf(
            MuestrasSemilla.requerir("mariposa"),
            MuestrasSemilla.requerir("estrella_mar")
        )
        val criterio = CriterioClasificacion.POR_SIMETRIA
        val resultado = EvaluadorClasificacion.evaluar(
            muestras, criterio,
            mapOf("mariposa" to "radial", "estrella_mar" to "radial")
        )

        val feedback = MotorFeedback.paraClasificacion(resultado, criterio) { id ->
            muestras.first { it.id == id }.nombre
        }

        assertTrue(feedback.mensaje.contains("Mariposa monarca"))
        assertNotNull(feedback.pista)
    }

    @Test
    fun `la clasificacion perfecta no propone reintento`() {
        val muestras = listOf(
            MuestrasSemilla.requerir("mariposa"),
            MuestrasSemilla.requerir("estrella_mar")
        )
        val criterio = CriterioClasificacion.POR_SIMETRIA
        val resultado = EvaluadorClasificacion.evaluar(
            muestras, criterio, EvaluadorClasificacion.esperado(muestras, criterio)
        )

        val feedback = MotorFeedback.paraClasificacion(resultado, criterio) { it }

        assertEquals(TonoFeedback.EXCELENTE, feedback.tono)
        assertFalse(feedback.puedeReintentar)
    }
}
