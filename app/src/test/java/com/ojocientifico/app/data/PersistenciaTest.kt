package com.ojocientifico.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ojocientifico.app.data.local.OjoDatabase
import com.ojocientifico.app.data.repository.OjoRepository
import com.ojocientifico.app.data.seed.MisionesSemilla
import com.ojocientifico.app.data.seed.MuestrasSemilla
import com.ojocientifico.app.domain.logica.EvaluadorObservacion
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Persistencia real con Room: el progreso tiene que sobrevivir a cerrar la
 * aplicación, y ninguna recompensa puede concederse sin quedar registrada.
 */
@RunWith(RobolectricTestRunner::class)
// Se usa una Application neutra: el test comprueba el repositorio contra una
// base en memoria, no el arranque de la aplicacion real.
@Config(application = android.app.Application::class)
class PersistenciaTest {

    private lateinit var db: OjoDatabase
    private lateinit var repo: OjoRepository
    private var reloj = 1_700_000_000_000L

    private val UN_DIA = 24 * 60 * 60 * 1000L

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            OjoDatabase::class.java
        ).allowMainThreadQueries().build()
        repo = OjoRepository(db) { reloj }
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    // ----------------------------- Siembra -----------------------------

    @Test
    fun `la siembra inicial deja la aplicacion lista para usarse`() = runTest {
        repo.asegurarSemilla()

        assertEquals(MuestrasSemilla.todas.size, repo.muestras().size)
        assertEquals(MisionesSemilla.todas.size, repo.misiones().size)
        assertNotNull(repo.muestra("mariposa"))
        assertNotNull(repo.mision("m1_01"))
    }

    @Test
    fun `sembrar dos veces no duplica el contenido`() = runTest {
        repo.asegurarSemilla()
        repo.asegurarSemilla()
        repo.asegurarSemilla()

        assertEquals(MuestrasSemilla.todas.size, db.catalogoDao().contarMuestras())
        assertEquals(MisionesSemilla.todas.size, db.catalogoDao().contarMisiones())
    }

    @Test
    fun `las caracteristicas viajan a la base sin perder la distincion de distractores`() = runTest {
        repo.asegurarSemilla()

        val original = MuestrasSemilla.requerir("caracol")
        val guardada = repo.muestra("caracol")!!

        assertEquals(original.rasgos, guardada.rasgos)
        assertEquals(original.distractores, guardada.distractores)
        assertEquals(original.nombreCientifico, guardada.nombreCientifico)
    }

    // ------------------------- Registro de fichas -------------------------

    @Test
    fun `una observacion queda guardada como ficha con sus marcas`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        repo.registrarObservacion(
            muestra = muestra,
            misionId = "m1_02",
            resultado = ResultadoSeleccion(esperados, esperados),
            nota = "Las alas parecen de papel"
        )

        val fichas = repo.fichas.first()
        assertEquals(1, fichas.size)
        assertEquals("mariposa", fichas.first().muestraId)
        assertEquals(esperados.size, fichas.first().aciertos)
        assertEquals("Las alas parecen de papel", fichas.first().notaDelExplorador)
        assertEquals(esperados, fichas.first().rasgosRegistrados.toSet())
    }

    @Test
    fun `una nota vacia o larguisima no rompe el registro`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("hormiga")!!
        val categorias = listOf(CategoriaRasgo.FORMA)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "x".repeat(1000))

        val fichas = repo.fichas.first()
        assertEquals(2, fichas.size)
        assertTrue(fichas.any { it.notaDelExplorador.isEmpty() })
        assertTrue(fichas.all { it.notaDelExplorador.length <= 280 })
    }

    // ------------------------------- XP -------------------------------

    @Test
    fun `el xp se acumula y sobrevive a releer el perfil`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        val primero = repo.registrarObservacion(
            muestra, "m1_02", ResultadoSeleccion(esperados, esperados), ""
        )
        val segundo = repo.registrarObservacion(
            muestra, "m1_02", ResultadoSeleccion(esperados, esperados), ""
        )

        assertTrue(primero.xpGanado > 0)
        // La segunda vez la misión ya estaba superada: da menos XP.
        assertTrue(segundo.xpGanado < primero.xpGanado)
        assertEquals(primero.xpGanado + segundo.xpGanado, repo.estadisticas().xp)
    }

    @Test
    fun `una observacion sin aciertos no da xp pero si queda registrada`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val categorias = listOf(CategoriaRasgo.FORMA)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        val resumen = repo.registrarObservacion(
            muestra, null, ResultadoSeleccion(esperados, emptySet()), ""
        )

        assertEquals(0, resumen.xpGanado)
        assertEquals(0, repo.estadisticas().xp)
        assertEquals(1, repo.estadisticas().fichasRegistradas)
    }

    // -------------------------- Progreso de misión --------------------------

    @Test
    fun `repetir una mision conserva siempre el mejor resultado`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        repo.registrarObservacion(muestra, "m1_02", ResultadoSeleccion(esperados, esperados), "")
        repo.registrarObservacion(muestra, "m1_02", ResultadoSeleccion(esperados, emptySet()), "")

        val progreso = db.progresoDao().progresoMision("m1_02")!!
        assertEquals(3, progreso.mejorEstrellas)
        assertTrue(progreso.completada)
        assertEquals(2, progreso.intentos)
    }

    @Test
    fun `una mision fallada no se marca como completada`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        repo.registrarObservacion(muestra, "m1_02", ResultadoSeleccion(esperados, emptySet()), "")

        assertFalse(db.progresoDao().progresoMision("m1_02")!!.completada)
        assertTrue(repo.misionesCompletadas.first().isEmpty())
    }

    // ---------------------------- Colección ----------------------------

    @Test
    fun `el descubrimiento solo se desbloquea observando con precision`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("girasol")!!
        val categorias = listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR, CategoriaRasgo.PARTES)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        // Observación floja: no desbloquea.
        val floja = repo.registrarObservacion(
            muestra, null, ResultadoSeleccion(esperados, esperados.take(1).toSet()), ""
        )
        assertTrue(floja.descubrimientosNuevos.isEmpty())
        assertFalse("girasol" in repo.descubrimientosDesbloqueados.first())

        // Observación completa: desbloquea una sola vez.
        val buena = repo.registrarObservacion(
            muestra, null, ResultadoSeleccion(esperados, esperados), ""
        )
        assertEquals(listOf("girasol"), buena.descubrimientosNuevos)

        val repetida = repo.registrarObservacion(
            muestra, null, ResultadoSeleccion(esperados, esperados), ""
        )
        assertTrue(repetida.descubrimientosNuevos.isEmpty())
        assertEquals(1, repo.estadisticas().descubrimientosDesbloqueados)
    }

    // ------------------------------ Repaso ------------------------------

    @Test
    fun `los rasgos que se escapan se acumulan y desaparecen al acertarlos`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("caracol")!!
        val categorias = listOf(CategoriaRasgo.FORMA)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, emptySet()), "")
        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, emptySet()), "")

        val fallosAcumulados = repo.fallos.first()
        assertEquals(esperados.size, fallosAcumulados.size)
        assertTrue(fallosAcumulados.all { it.veces == 2 })

        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        assertTrue(repo.fallos.first().isEmpty())
    }

    // ---------------------------- Insignias ----------------------------

    @Test
    fun `la insignia se concede una sola vez y queda persistida`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("hormiga")!!
        val categorias = listOf(CategoriaRasgo.FORMA)
        val esperados = EvaluadorObservacion.esperados(muestra, categorias)

        val resumenes = (1..4).map {
            repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        }

        val anuncios = resumenes.flatMap { it.insigniasNuevas }.count { it.id == "explorador_atento" }
        assertEquals(1, anuncios)
        assertTrue("explorador_atento" in repo.insigniasDesbloqueadas.first())
    }

    // ------------------------- Días de expedición -------------------------

    @Test
    fun `investigar varias veces el mismo dia cuenta como un solo dia`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("hormiga")!!
        val esperados = EvaluadorObservacion.esperados(muestra, listOf(CategoriaRasgo.FORMA))

        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        assertEquals(1, repo.estadisticas().diasDeExpedicion)

        reloj += UN_DIA
        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        assertEquals(2, repo.estadisticas().diasDeExpedicion)
    }

    // ----------------------------- Historial -----------------------------

    @Test
    fun `cada actividad deja una entrada en el historial`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val esperados = EvaluadorObservacion.esperados(muestra, listOf(CategoriaRasgo.FORMA))

        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, esperados), "")
        repo.registrarObservacion(muestra, null, ResultadoSeleccion(esperados, emptySet()), "")

        val historial = repo.historial.first()
        assertEquals(2, historial.size)
        assertTrue(historial.all { it.referencia == "mariposa" })
    }

    // ------------------------------ Reinicio ------------------------------

    @Test
    fun `reiniciar borra el progreso pero conserva el catalogo`() = runTest {
        repo.asegurarSemilla()
        val muestra = repo.muestra("mariposa")!!
        val esperados = EvaluadorObservacion.esperados(
            muestra, listOf(CategoriaRasgo.FORMA, CategoriaRasgo.COLOR)
        )
        repeat(4) {
            repo.registrarObservacion(muestra, "m1_02", ResultadoSeleccion(esperados, esperados), "nota")
        }
        assertTrue(repo.estadisticas().xp > 0)

        repo.reiniciarProgreso()

        val stats = repo.estadisticas()
        assertEquals(0, stats.xp)
        assertEquals(0, stats.fichasRegistradas)
        assertEquals(0, stats.misionesCompletadas)
        assertEquals(0, stats.descubrimientosDesbloqueados)
        assertEquals(0, stats.diasDeExpedicion)
        assertTrue(repo.insigniasDesbloqueadas.first().isEmpty())
        assertTrue(repo.historial.first().isEmpty())

        // El contenido sembrado sigue intacto.
        assertEquals(MuestrasSemilla.todas.size, repo.muestras().size)
        assertEquals(MisionesSemilla.todas.size, repo.misiones().size)
    }

    // --------------------------- Configuración ---------------------------

    @Test
    fun `el alias y las preferencias se guardan y se leen`() = runTest {
        repo.asegurarSemilla()

        repo.completarOnboarding("Ojo de Lince", avatar = 2)
        val guardada = repo.configuracion.first()

        assertEquals("Ojo de Lince", guardada.alias)
        assertEquals(2, guardada.avatar)
        assertTrue(guardada.onboardingHecho)

        repo.guardarConfiguracion(guardada.copy(textoGrande = true, altoContraste = true))
        val actualizada = repo.configuracion.first()
        assertTrue(actualizada.textoGrande)
        assertTrue(actualizada.altoContraste)
        assertEquals("Ojo de Lince", actualizada.alias)
    }

    @Test
    fun `un alias vacio se sustituye por uno por defecto`() = runTest {
        repo.asegurarSemilla()
        repo.completarOnboarding("   ", avatar = 0)

        assertEquals("Explorador", repo.configuracion.first().alias)
    }
}
