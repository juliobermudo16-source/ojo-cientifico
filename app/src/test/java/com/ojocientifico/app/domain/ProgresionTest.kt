package com.ojocientifico.app.domain

import com.ojocientifico.app.data.seed.MisionesSemilla
import com.ojocientifico.app.domain.logica.CalculadoraProgreso
import com.ojocientifico.app.domain.logica.DesbloqueoMisiones
import com.ojocientifico.app.domain.logica.MotorInsignias
import com.ojocientifico.app.domain.logica.PlanificadorRepaso
import com.ojocientifico.app.domain.logica.SistemaXp
import com.ojocientifico.app.domain.model.CatalogoInsignias
import com.ojocientifico.app.domain.model.EstadisticasExplorador
import com.ojocientifico.app.domain.model.EstadoMision
import com.ojocientifico.app.domain.model.RangoExplorador
import com.ojocientifico.app.domain.model.RasgoFallado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas de XP, rangos, desbloqueo, insignias y repaso. */
class ProgresionTest {

    // ---------------------------- XP ----------------------------

    @Test
    fun `el xp es proporcional a la precision`() {
        val mitad = SistemaXp.calcular(xpBase = 40, precision = 0.5f, estrellas = 1)
        val entero = SistemaXp.calcular(xpBase = 40, precision = 1f, estrellas = 3)

        assertEquals(20, mitad)
        assertEquals(40 + SistemaXp.BONUS_TRES_ESTRELLAS, entero)
    }

    @Test
    fun `sin aciertos no se gana xp`() {
        assertEquals(0, SistemaXp.calcular(xpBase = 60, precision = 0f, estrellas = 0))
    }

    @Test
    fun `repetir una mision ya superada da menos xp`() {
        val primera = SistemaXp.calcular(50, 1f, 3, yaCompletada = false)
        val repetida = SistemaXp.calcular(50, 1f, 3, yaCompletada = true)

        assertTrue(repetida < primera)
        assertTrue(repetida > 0)
    }

    @Test
    fun `una precision fuera de rango se recorta`() {
        val exagerada = SistemaXp.calcular(40, 5f, 3)
        val negativa = SistemaXp.calcular(40, -3f, 0)

        assertEquals(40 + SistemaXp.BONUS_TRES_ESTRELLAS, exagerada)
        assertEquals(0, negativa)
    }

    @Test
    fun `una mision sin recompensa no genera xp`() {
        assertEquals(0, SistemaXp.calcular(0, 1f, 3))
    }

    // --------------------------- Rangos ---------------------------

    @Test
    fun `el rango depende del xp acumulado`() {
        assertEquals(RangoExplorador.OBSERVADOR, CalculadoraProgreso.rango(0))
        assertEquals(RangoExplorador.OBSERVADOR, CalculadoraProgreso.rango(199))
        assertEquals(RangoExplorador.INVESTIGADOR, CalculadoraProgreso.rango(200))
        assertEquals(RangoExplorador.DESCUBRIDOR, CalculadoraProgreso.rango(9999))
    }

    @Test
    fun `un xp negativo no rompe el calculo de rango`() {
        assertEquals(RangoExplorador.OBSERVADOR, CalculadoraProgreso.rango(-50))
        assertEquals(0f, CalculadoraProgreso.fraccionRango(-50), 0.0001f)
    }

    @Test
    fun `la fraccion de rango va de cero a uno`() {
        assertEquals(0f, CalculadoraProgreso.fraccionRango(0), 0.0001f)
        assertEquals(1f, CalculadoraProgreso.fraccionRango(10_000), 0.0001f)

        val medio = CalculadoraProgreso.fraccionRango(100)
        assertTrue(medio in 0f..1f)
    }

    @Test
    fun `en el rango maximo no queda xp por delante`() {
        assertEquals(0, CalculadoraProgreso.xpRestante(10_000))
        assertNull(CalculadoraProgreso.siguienteRango(10_000))
    }

    @Test
    fun `subir de rango se detecta al cruzar el umbral`() {
        assertNotNull(CalculadoraProgreso.rangoGanado(199, 200))
        assertNull(CalculadoraProgreso.rangoGanado(200, 260))
        assertEquals(
            RangoExplorador.DESCUBRIDOR,
            CalculadoraProgreso.rangoGanado(519, 520)
        )
    }

    // -------------------------- Desbloqueo --------------------------

    @Test
    fun `la primera mision esta disponible desde el principio`() {
        val estados = DesbloqueoMisiones.estados(MisionesSemilla.todas, xp = 0, completadas = emptySet())
        assertEquals(EstadoMision.DISPONIBLE, estados["m1_01"])
    }

    @Test
    fun `una mision con requisito sin cumplir esta bloqueada`() {
        val estados = DesbloqueoMisiones.estados(MisionesSemilla.todas, xp = 0, completadas = emptySet())
        assertEquals(EstadoMision.BLOQUEADA, estados["m1_02"])
    }

    @Test
    fun `las misiones de nivel superior estan bloqueadas sin xp suficiente`() {
        val todasNivel1 = MisionesSemilla.deNivel(1).map { it.id }.toSet()
        val estados = DesbloqueoMisiones.estados(MisionesSemilla.todas, xp = 10, completadas = todasNivel1)

        assertEquals(EstadoMision.BLOQUEADA, estados["m2_01"])
    }

    @Test
    fun `con xp y requisitos cumplidos la mision se abre`() {
        val completadas = MisionesSemilla.deNivel(1).map { it.id }.toSet()
        val estados = DesbloqueoMisiones.estados(MisionesSemilla.todas, xp = 250, completadas = completadas)

        assertEquals(EstadoMision.DISPONIBLE, estados["m2_01"])
        assertEquals(EstadoMision.COMPLETADA, estados["m1_01"])
    }

    @Test
    fun `la siguiente mision disponible respeta nivel y orden`() {
        val siguiente = DesbloqueoMisiones.siguienteDisponible(
            MisionesSemilla.todas, xp = 0, completadas = emptySet()
        )
        assertEquals("m1_01", siguiente?.id)

        val despues = DesbloqueoMisiones.siguienteDisponible(
            MisionesSemilla.todas, xp = 40, completadas = setOf("m1_01")
        )
        assertEquals("m1_02", despues?.id)
    }

    @Test
    fun `cuando todo esta completado no hay siguiente mision`() {
        val todas = MisionesSemilla.todas.map { it.id }.toSet()
        assertNull(DesbloqueoMisiones.siguienteDisponible(MisionesSemilla.todas, 10_000, todas))
        assertEquals(1f, DesbloqueoMisiones.avanceTotal(MisionesSemilla.todas, todas), 0.0001f)
    }

    @Test
    fun `el avance de una expedicion vacia es cero`() {
        assertEquals(0f, DesbloqueoMisiones.avanceTotal(emptyList(), emptySet()), 0.0001f)
    }

    // -------------------------- Insignias --------------------------

    @Test
    fun `sin actividad no se concede ninguna insignia`() {
        val nuevas = MotorInsignias.nuevas(EstadisticasExplorador(), emptySet())
        assertTrue(nuevas.isEmpty())
    }

    @Test
    fun `la insignia se concede justo al alcanzar la meta`() {
        val casi = EstadisticasExplorador(observacionesCorrectas = 2)
        val justo = EstadisticasExplorador(observacionesCorrectas = 3)

        assertTrue(MotorInsignias.nuevas(casi, emptySet()).none { it.id == "explorador_atento" })
        assertTrue(MotorInsignias.nuevas(justo, emptySet()).any { it.id == "explorador_atento" })
    }

    @Test
    fun `una insignia ya conseguida no se vuelve a anunciar`() {
        val estado = EstadisticasExplorador(observacionesCorrectas = 9)
        val nuevas = MotorInsignias.nuevas(estado, setOf("explorador_atento"))

        assertTrue(nuevas.none { it.id == "explorador_atento" })
    }

    @Test
    fun `el panel muestra el avance parcial de las insignias pendientes`() {
        val estado = EstadisticasExplorador(fichasRegistradas = 4)
        val panel = MotorInsignias.panel(estado, emptySet())
        val cuaderno = panel.first { it.insignia.id == "cuaderno_completo" }

        assertFalse(cuaderno.desbloqueada)
        assertEquals(4, cuaderno.progreso)
        assertEquals(0.5f, cuaderno.fraccion, 0.0001f)
    }

    @Test
    fun `una insignia desbloqueada aparece siempre al maximo`() {
        val panel = MotorInsignias.panel(EstadisticasExplorador(), setOf("gran_descubridor"))
        val entrada = panel.first { it.insignia.id == "gran_descubridor" }

        assertTrue(entrada.desbloqueada)
        assertEquals(entrada.meta, entrada.progreso)
        assertEquals(1f, entrada.fraccion, 0.0001f)
    }

    @Test
    fun `todas las insignias tienen meta positiva y descripcion`() {
        CatalogoInsignias.todas.forEach { insignia ->
            assertTrue("Meta inválida en ${insignia.id}", insignia.meta > 0)
            assertTrue(insignia.nombre.isNotBlank())
            assertTrue(insignia.comoSeGana.isNotBlank())
        }
    }

    // ---------------------------- Repaso ----------------------------

    @Test
    fun `sin fallos no hay nada que repasar`() {
        assertTrue(PlanificadorRepaso.sugerencias(emptyList()).isEmpty())
        assertNull(PlanificadorRepaso.categoriaMasFloja(emptyList()))
    }

    @Test
    fun `las sugerencias se ordenan por numero de fallos`() {
        val fallos = listOf(
            RasgoFallado("mariposa", "color_negro", veces = 1, ultimaFechaMillis = 10),
            RasgoFallado("caracol", "forma_espiral", veces = 4, ultimaFechaMillis = 20),
            RasgoFallado("caracol", "textura_humeda", veces = 2, ultimaFechaMillis = 30)
        )

        val sugerencias = PlanificadorRepaso.sugerencias(fallos)

        assertEquals("caracol", sugerencias.first().muestraId)
        assertEquals(6, sugerencias.first().vecesFalladas)
        assertEquals(2, sugerencias.size)
    }

    @Test
    fun `el limite de sugerencias se respeta`() {
        val fallos = (1..12).map {
            RasgoFallado("muestra_$it", "color_verde", veces = it, ultimaFechaMillis = 0)
        }
        assertEquals(3, PlanificadorRepaso.sugerencias(fallos, limite = 3).size)
        assertTrue(PlanificadorRepaso.sugerencias(fallos, limite = 0).isEmpty())
    }

    @Test
    fun `la categoria mas floja es la que acumula mas fallos`() {
        val fallos = listOf(
            RasgoFallado("a", "color_negro", veces = 1, ultimaFechaMillis = 0),
            RasgoFallado("b", "forma_espiral", veces = 5, ultimaFechaMillis = 0),
            RasgoFallado("c", "forma_ovalada", veces = 2, ultimaFechaMillis = 0)
        )
        assertEquals(
            com.ojocientifico.app.domain.model.CategoriaRasgo.FORMA,
            PlanificadorRepaso.categoriaMasFloja(fallos)
        )
    }

    @Test
    fun `las categorias exploradas no cuentan repeticiones`() {
        val registrados = listOf("color_verde", "color_rojo", "forma_espiral", "color_verde")
        assertEquals(2, PlanificadorRepaso.categoriasExploradas(registrados))
        assertEquals(0, PlanificadorRepaso.categoriasExploradas(emptyList()))
    }
}
