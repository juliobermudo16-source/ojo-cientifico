package com.ojocientifico.app.domain

import com.ojocientifico.app.data.seed.MuestrasSemilla
import com.ojocientifico.app.domain.logica.DetectorPatrones
import com.ojocientifico.app.domain.logica.EvaluadorClasificacion
import com.ojocientifico.app.domain.logica.EvaluadorComparacion
import com.ojocientifico.app.domain.logica.EvaluadorObservacion
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La evaluación es el corazón educativo: comprueba lo que el niño marcó contra
 * las características reales de la muestra.
 */
class EvaluadoresTest {

    private val mariposa = MuestrasSemilla.requerir("mariposa")
    private val hormiga = MuestrasSemilla.requerir("hormiga")
    private val escarabajo = MuestrasSemilla.requerir("escarabajo")
    private val caracol = MuestrasSemilla.requerir("caracol")

    // ------------------------- Observación -------------------------

    @Test
    fun `observacion completa da precision uno y tres estrellas`() {
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(mariposa, categorias)

        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, esperados)

        assertEquals(1f, resultado.precision, 0.0001f)
        assertEquals(3, resultado.estrellas)
        assertTrue(resultado.perfecto)
        assertTrue(resultado.omitidos.isEmpty())
        assertTrue(resultado.falsosPositivos.isEmpty())
    }

    @Test
    fun `no marcar nada da precision cero y ninguna estrella`() {
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)

        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, emptySet())

        assertEquals(0f, resultado.precision, 0.0001f)
        assertEquals(0, resultado.estrellas)
        assertFalse(resultado.perfecto)
        assertEquals(EvaluadorObservacion.esperados(mariposa, categorias), resultado.omitidos)
    }

    @Test
    fun `una caracteristica de mas penaliza media caracteristica`() {
        val categorias = listOf(CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(mariposa, categorias)
        val distractor = mariposa.distractores.first { it.startsWith("color_") }

        val resultado = EvaluadorObservacion.evaluar(mariposa, categorias, esperados + distractor)

        val penalizacionEsperada = 1f - 0.5f / esperados.size
        assertEquals(penalizacionEsperada, resultado.precision, 0.0001f)
        assertEquals(setOf(distractor), resultado.falsosPositivos)
    }

    @Test
    fun `se ignoran las marcas que no estaban en pantalla`() {
        val categorias = listOf(CategoriaRasgo.FORMA)
        val esperados = EvaluadorObservacion.esperados(mariposa, categorias)

        // "parte_patas" es verdadero para la mariposa pero pertenece a otra
        // categoría, así que no se ofrecía en esta pantalla.
        val resultado = EvaluadorObservacion.evaluar(
            mariposa, categorias, esperados + "parte_patas" + "rasgo_inventado"
        )

        assertTrue(resultado.falsosPositivos.isEmpty())
        assertTrue(resultado.perfecto)
    }

    @Test
    fun `el universo mezcla caracteristicas verdaderas y distractores`() {
        val universo = EvaluadorObservacion.universo(caracol, listOf(CategoriaRasgo.FORMA))
        val opciones = universo.getValue(CategoriaRasgo.FORMA)

        assertTrue(opciones.containsAll(caracol.rasgosDe(CategoriaRasgo.FORMA)))
        assertTrue(opciones.size > caracol.rasgosDe(CategoriaRasgo.FORMA).size)
    }

    @Test
    fun `categoria sin datos no genera opciones`() {
        // El caracol no tiene ningún rasgo de la categoría PARTICULAR salvo uno;
        // se comprueba con una categoría realmente vacía para esa muestra.
        val cristal = MuestrasSemilla.requerir("cristal_cuarzo")
        val universo = EvaluadorObservacion.universo(cristal, listOf(CategoriaRasgo.PARTES))

        assertTrue(cristal.rasgosDe(CategoriaRasgo.PARTES).isEmpty())
        // Aunque haya distractores de PARTES, no hay nada verdadero que acertar.
        assertTrue(EvaluadorObservacion.esperados(cristal, listOf(CategoriaRasgo.PARTES)).isEmpty())
    }

    // ------------------------- Comparación -------------------------

    @Test
    fun `las semejanzas son la interseccion real de caracteristicas`() {
        val categorias = listOf(CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO)
        val semejanzas = EvaluadorComparacion.semejanzas(mariposa, hormiga, categorias)

        assertTrue("numero_6" in semejanzas)
        assertTrue("parte_antenas" in semejanzas)
        assertTrue("parte_patas" in semejanzas)
        // Las alas solo las tiene la mariposa.
        assertFalse("parte_alas" in semejanzas)
    }

    @Test
    fun `las diferencias son lo que solo tiene la primera muestra`() {
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.ESTRUCTURA)
        val diferencias = EvaluadorComparacion.diferencias(caracol, escarabajo, categorias)

        assertTrue("forma_espiral" in diferencias)
        assertTrue("estructura_capas" in diferencias)
        assertFalse("estructura_segmentada" in diferencias)
    }

    @Test
    fun `comparar una muestra consigo misma no da diferencias`() {
        val diferencias = EvaluadorComparacion.diferencias(
            mariposa, mariposa, CategoriaRasgo.entries.toList()
        )
        assertTrue(diferencias.isEmpty())
    }

    @Test
    fun `el universo de comparacion contiene las dos muestras sin repetir`() {
        val categorias = listOf(CategoriaRasgo.PARTES)
        val universo = EvaluadorComparacion.universo(mariposa, hormiga, categorias)

        assertEquals(universo.size, universo.toSet().size)
        assertTrue(universo.containsAll(mariposa.rasgosDe(CategoriaRasgo.PARTES)))
        assertTrue(universo.containsAll(hormiga.rasgosDe(CategoriaRasgo.PARTES)))
    }

    @Test
    fun `evaluar semejanzas descarta marcas fuera del universo`() {
        val categorias = listOf(CategoriaRasgo.NUMERO)
        val resultado = EvaluadorComparacion.evaluarSemejanzas(
            mariposa, hormiga, categorias, setOf("numero_6", "color_verde")
        )
        assertTrue(resultado.falsosPositivos.isEmpty())
        assertTrue(resultado.perfecto)
    }

    // -------------------------- Patrones --------------------------

    @Test
    fun `el patron de los tres insectos incluye seis patas y simetria bilateral`() {
        val insectos = listOf(hormiga, mariposa, escarabajo)
        val categorias = listOf(
            CategoriaRasgo.PARTES, CategoriaRasgo.NUMERO,
            CategoriaRasgo.SIMETRIA, CategoriaRasgo.ESTRUCTURA
        )

        val patron = DetectorPatrones.patronComun(insectos, categorias)

        assertTrue("numero_6" in patron)
        assertTrue("simetria_bilateral" in patron)
        assertTrue("parte_patas" in patron)
        assertTrue("parte_antenas" in patron)
        assertTrue("estructura_segmentada" in patron)
    }

    @Test
    fun `sin muestras no hay patron`() {
        assertTrue(DetectorPatrones.patronComun(emptyList(), CategoriaRasgo.entries.toList()).isEmpty())
    }

    @Test
    fun `con una sola muestra el patron son todos sus rasgos de esa categoria`() {
        val patron = DetectorPatrones.patronComun(listOf(caracol), listOf(CategoriaRasgo.FORMA))
        assertEquals(caracol.rasgosDe(CategoriaRasgo.FORMA), patron)
    }

    @Test
    fun `marcar algo que no esta en todas cuenta como error`() {
        val muestras = listOf(hormiga, mariposa, escarabajo)
        val categorias = listOf(CategoriaRasgo.PARTES)

        val resultado = DetectorPatrones.evaluar(muestras, categorias, setOf("parte_alas"))

        assertTrue("parte_alas" in resultado.falsosPositivos)
        assertFalse(resultado.perfecto)
    }

    // ------------------------ Clasificación ------------------------

    @Test
    fun `el grupo correcto se deduce de los datos de la muestra`() {
        val muestras = listOf(mariposa, caracol, MuestrasSemilla.requerir("estrella_mar"))
        val esperado = EvaluadorClasificacion.esperado(muestras, CriterioClasificacion.POR_SIMETRIA)

        assertEquals("bilateral", esperado["mariposa"])
        assertEquals("otra", esperado["caracol"])
        assertEquals("radial", esperado["estrella_mar"])
    }

    @Test
    fun `clasificacion perfecta da precision uno`() {
        val muestras = listOf(
            MuestrasSemilla.requerir("cristal_cuarzo"),
            MuestrasSemilla.requerir("rana_arboricola"),
            MuestrasSemilla.requerir("telarana")
        )
        val criterio = CriterioClasificacion.SER_VIVO
        val correcto = EvaluadorClasificacion.esperado(muestras, criterio)

        val resultado = EvaluadorClasificacion.evaluar(muestras, criterio, correcto)

        assertEquals(1f, resultado.precision, 0.0001f)
        assertTrue(resultado.perfecto)
        assertEquals(3, resultado.estrellas)
    }

    @Test
    fun `las asignaciones a grupos inexistentes se descartan`() {
        val muestras = listOf(mariposa, caracol)
        val criterio = CriterioClasificacion.POR_SIMETRIA

        val resultado = EvaluadorClasificacion.evaluar(
            muestras, criterio,
            mapOf("mariposa" to "grupo_inventado", "muestra_fantasma" to "bilateral")
        )

        assertTrue(resultado.correctas.isEmpty())
        assertEquals(setOf("mariposa", "caracol"), resultado.sinAsignar)
    }

    @Test
    fun `dejar muestras sin colocar baja la precision`() {
        val muestras = listOf(mariposa, caracol, hormiga)
        val criterio = CriterioClasificacion.POR_SIMETRIA

        val resultado = EvaluadorClasificacion.evaluar(
            muestras, criterio, mapOf("mariposa" to "bilateral")
        )

        assertEquals(1f / 3f, resultado.precision, 0.0001f)
        assertEquals(setOf("caracol", "hormiga"), resultado.sinAsignar)
        assertFalse(resultado.perfecto)
    }

    @Test
    fun `solo se ofrecen los grupos que realmente se usan`() {
        val muestras = listOf(mariposa, hormiga)
        val grupos = EvaluadorClasificacion.gruposUtiles(muestras, CriterioClasificacion.POR_SIMETRIA)

        assertEquals(1, grupos.size)
        assertEquals("bilateral", grupos.first().id)
    }

    // ------------------------- Casos límite -------------------------

    @Test
    fun `resultado sin nada esperado ni marcado se considera correcto`() {
        val resultado = ResultadoSeleccion(emptySet(), emptySet())
        assertEquals(1f, resultado.precision, 0.0001f)
        assertTrue(resultado.perfecto)
    }

    @Test
    fun `marcar algo cuando no habia nada que marcar es un error`() {
        val resultado = ResultadoSeleccion(emptySet(), setOf("color_verde"))
        assertEquals(0f, resultado.precision, 0.0001f)
        assertFalse(resultado.perfecto)
    }

    @Test
    fun `evaluar dos veces con la misma seleccion da el mismo resultado`() {
        val categorias = listOf(CategoriaRasgo.COLOR, CategoriaRasgo.FORMA)
        val seleccion = EvaluadorObservacion.esperados(hormiga, categorias)

        val primero = EvaluadorObservacion.evaluar(hormiga, categorias, seleccion)
        val segundo = EvaluadorObservacion.evaluar(hormiga, categorias, seleccion)

        assertEquals(primero.precision, segundo.precision, 0.0f)
        assertEquals(primero.aciertos, segundo.aciertos)
    }
}
