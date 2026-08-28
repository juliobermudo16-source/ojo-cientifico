package com.ojocientifico.app.domain

import com.ojocientifico.app.data.seed.MisionesSemilla
import com.ojocientifico.app.data.seed.MuestrasSemilla
import com.ojocientifico.app.domain.logica.DetectorPatrones
import com.ojocientifico.app.domain.logica.EvaluadorClasificacion
import com.ojocientifico.app.domain.logica.EvaluadorComparacion
import com.ojocientifico.app.domain.logica.EvaluadorObservacion
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.ModoComparacion
import com.ojocientifico.app.domain.model.RangoExplorador
import com.ojocientifico.app.domain.model.TipoMision
import com.ojocientifico.app.domain.logica.SistemaXp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El contenido es la mitad del producto: si una misión apunta a una muestra que
 * no existe o pide un patrón imposible, la aplicación deja de ser educativa.
 * Estos tests validan la coherencia de los datos sembrados.
 */
class ContenidoSemillaTest {

    @Test
    fun `la instalacion inicial trae contenido suficiente`() {
        assertTrue("Pocas muestras", MuestrasSemilla.todas.size >= 12)
        assertTrue("Pocas misiones", MisionesSemilla.todas.size >= 15)
        assertTrue("Pocas caracteristicas", CatalogoRasgos.todas.size >= 60)
    }

    @Test
    fun `no hay identificadores repetidos`() {
        assertEquals(
            MuestrasSemilla.todas.size,
            MuestrasSemilla.todas.map { it.id }.toSet().size
        )
        assertEquals(
            MisionesSemilla.todas.size,
            MisionesSemilla.todas.map { it.id }.toSet().size
        )
        assertEquals(
            CatalogoRasgos.todas.size,
            CatalogoRasgos.todas.map { it.id }.toSet().size
        )
    }

    @Test
    fun `todas las caracteristicas de las muestras existen en el catalogo`() {
        MuestrasSemilla.todas.forEach { muestra ->
            (muestra.rasgos + muestra.distractores).forEach { id ->
                assertNotNull(
                    "La muestra ${muestra.id} usa una característica inexistente: $id",
                    CatalogoRasgos.porId(id)
                )
            }
        }
    }

    @Test
    fun `ninguna muestra tiene un distractor que en realidad es verdadero`() {
        MuestrasSemilla.todas.forEach { muestra ->
            val solapados = muestra.rasgos intersect muestra.distractores
            assertTrue(
                "La muestra ${muestra.id} tiene características contradictorias: $solapados",
                solapados.isEmpty()
            )
        }
    }

    @Test
    fun `cada muestra tiene datos suficientes para observarla`() {
        MuestrasSemilla.todas.forEach { muestra ->
            assertTrue("${muestra.id} tiene pocas características", muestra.rasgos.size >= 8)
            assertTrue("${muestra.id} tiene pocos distractores", muestra.distractores.size >= 8)
            assertTrue(muestra.nombre.isNotBlank())
            assertTrue(muestra.nombreCientifico.isNotBlank())
            assertTrue(muestra.descripcion.isNotBlank())
            assertTrue(muestra.datoCurioso.isNotBlank())
            assertTrue(muestra.nivelRequerido in 1..3)
        }
    }

    @Test
    fun `cada muestra declara forma color tamano y simetria`() {
        val obligatorias = listOf(
            CategoriaRasgo.FORMA, CategoriaRasgo.COLOR,
            CategoriaRasgo.TAMANO, CategoriaRasgo.SIMETRIA
        )
        MuestrasSemilla.todas.forEach { muestra ->
            obligatorias.forEach { categoria ->
                assertTrue(
                    "${muestra.id} no declara ${categoria.name}",
                    muestra.rasgosDe(categoria).isNotEmpty()
                )
            }
        }
    }

    @Test
    fun `todas las misiones apuntan a muestras que existen`() {
        MisionesSemilla.todas.forEach { mision ->
            assertTrue("${mision.id} no tiene muestras", mision.muestrasIds.isNotEmpty())
            mision.muestrasIds.forEach { id ->
                assertNotNull("${mision.id} usa una muestra inexistente: $id", MuestrasSemilla.porId(id))
            }
        }
    }

    @Test
    fun `los requisitos entre misiones son validos y no forman ciclos`() {
        val porId = MisionesSemilla.todas.associateBy { it.id }
        MisionesSemilla.todas.forEach { mision ->
            var actual = mision.requiere
            var saltos = 0
            while (actual != null) {
                val previa = porId[actual]
                assertNotNull("${mision.id} requiere una misión inexistente: $actual", previa)
                assertTrue(
                    "La misión previa ${previa!!.id} es de un nivel superior a ${mision.id}",
                    previa.nivel <= mision.nivel
                )
                actual = previa.requiere
                saltos++
                assertTrue("Ciclo de requisitos detectado en ${mision.id}", saltos < 50)
            }
        }
    }

    @Test
    fun `cada tipo de mision trae los datos que necesita`() {
        MisionesSemilla.todas.forEach { mision ->
            when (mision.tipo) {
                TipoMision.COMPARACION -> {
                    assertNotNull("${mision.id} sin modo de comparación", mision.modoComparacion)
                    assertEquals("${mision.id} necesita dos muestras", 2, mision.muestrasIds.size)
                }

                TipoMision.CLASIFICACION -> {
                    assertNotNull("${mision.id} sin criterio", mision.criterio)
                    assertTrue("${mision.id} necesita varias muestras", mision.muestrasIds.size >= 3)
                }

                TipoMision.PATRON ->
                    assertTrue("${mision.id} necesita al menos tres muestras", mision.muestrasIds.size >= 3)

                TipoMision.OBSERVACION -> {
                    assertEquals("${mision.id} observa una sola muestra", 1, mision.muestrasIds.size)
                    assertTrue("${mision.id} sin categorías", mision.categorias.isNotEmpty())
                }
            }
            assertTrue(mision.titulo.isNotBlank())
            assertTrue(mision.consigna.isNotBlank())
            assertTrue(mision.instruccionGuia.isNotBlank())
            assertTrue(mision.xpBase > 0)
        }
    }

    @Test
    fun `ninguna mision es imposible ni trivial`() {
        MisionesSemilla.todas.forEach { mision ->
            val muestras = mision.muestrasIds.map { MuestrasSemilla.requerir(it) }
            when (mision.tipo) {
                TipoMision.OBSERVACION -> {
                    val esperados = EvaluadorObservacion.esperados(muestras.first(), mision.categorias)
                    assertTrue("${mision.id} no tiene nada que observar", esperados.size >= 2)
                }

                TipoMision.COMPARACION -> {
                    val esperados = if (mision.modoComparacion == ModoComparacion.SEMEJANZAS) {
                        EvaluadorComparacion.semejanzas(muestras[0], muestras[1], mision.categorias)
                    } else {
                        EvaluadorComparacion.diferencias(muestras[0], muestras[1], mision.categorias)
                    }
                    val universo = EvaluadorComparacion.universo(
                        muestras[0], muestras[1], mision.categorias
                    )
                    assertTrue("${mision.id} no tiene respuesta", esperados.isNotEmpty())
                    assertTrue(
                        "${mision.id} es trivial: todo el universo es la respuesta",
                        esperados.size < universo.size
                    )
                }

                TipoMision.PATRON -> {
                    val patron = DetectorPatrones.patronComun(muestras, mision.categorias)
                    val universo = DetectorPatrones.universo(muestras, mision.categorias)
                    assertTrue("${mision.id} no tiene patrón común", patron.isNotEmpty())
                    assertTrue("${mision.id} es trivial", patron.size < universo.size)
                }

                TipoMision.CLASIFICACION -> {
                    val criterio = mision.criterio!!
                    val grupos = EvaluadorClasificacion.gruposUtiles(muestras, criterio)
                    assertTrue(
                        "${mision.id} solo usa un grupo: no hay nada que clasificar",
                        grupos.size >= 2
                    )
                }
            }
        }
    }

    @Test
    fun `cada nivel tiene xp suficiente para desbloquear el siguiente`() {
        RangoExplorador.entries.forEach { rango ->
            val siguiente = RangoExplorador.entries.firstOrNull { it.nivel == rango.nivel + 1 }
                ?: return@forEach

            // XP máximo alcanzable jugando perfecto hasta el final de este nivel.
            val acumulado = MisionesSemilla.todas
                .filter { it.nivel <= rango.nivel }
                .sumOf { SistemaXp.calcular(it.xpBase, 1f, 3) }

            assertTrue(
                "Con el nivel ${rango.nivel} completo no se llega a ${siguiente.titulo}",
                acumulado >= siguiente.xpNecesario
            )
        }
    }

    @Test
    fun `las misiones estan repartidas entre los tres niveles`() {
        (1..3).forEach { nivel ->
            assertTrue(
                "El nivel $nivel tiene pocas misiones",
                MisionesSemilla.deNivel(nivel).size >= 4
            )
        }
    }

    @Test
    fun `hay actividades de los cuatro tipos`() {
        TipoMision.entries.forEach { tipo ->
            assertTrue(
                "No hay misiones de tipo $tipo",
                MisionesSemilla.todas.any { it.tipo == tipo }
            )
        }
    }

    @Test
    fun `las misiones solo usan muestras ya desbloqueadas en su nivel`() {
        MisionesSemilla.todas.forEach { mision ->
            mision.muestrasIds.forEach { id ->
                val muestra = MuestrasSemilla.requerir(id)
                assertTrue(
                    "${mision.id} (nivel ${mision.nivel}) usa ${muestra.id}, de nivel ${muestra.nivelRequerido}",
                    muestra.nivelRequerido <= mision.nivel
                )
            }
        }
    }

    @Test
    fun `el primer nivel ofrece muestras suficientes para explorar`() {
        assertTrue(MuestrasSemilla.deNivel(1).size >= 5)
        assertEquals(MuestrasSemilla.todas.size, MuestrasSemilla.deNivel(3).size)
    }

    @Test
    fun `todas las categorias del catalogo se usan en alguna muestra`() {
        val usadas = MuestrasSemilla.todas
            .flatMap { it.rasgos }
            .mapNotNull { CatalogoRasgos.categoriaDe(it) }
            .toSet()

        CategoriaRasgo.entries.forEach { categoria ->
            assertTrue("La categoría $categoria no se usa en ninguna muestra", categoria in usadas)
        }
    }

    @Test
    fun `todos los textos del catalogo estan en espanol con tildes correctas`() {
        // Ningún texto puede quedar vacío ni contener el carácter de reemplazo
        // que aparece cuando la codificación se estropea.
        val textos = CatalogoRasgos.todas.flatMap { listOf(it.etiqueta, it.pista) } +
            MuestrasSemilla.todas.flatMap { listOf(it.nombre, it.descripcion, it.datoCurioso) } +
            MisionesSemilla.todas.flatMap { listOf(it.titulo, it.consigna, it.instruccionGuia) }

        textos.forEach { texto ->
            assertTrue("Texto vacío en el contenido", texto.isNotBlank())
            assertFalse("Codificación corrupta en: $texto", texto.contains('�'))
        }

        // Comprobación explícita de que las tildes y la eñe sobreviven.
        assertEquals("Simetría", CatalogoRasgos.requerir("simetria_bilateral").categoria.etiqueta)
        assertEquals("Tamaño", CatalogoRasgos.requerir("tamano_pequeno").categoria.etiqueta)
        assertTrue(CatalogoRasgos.requerir("color_marron").etiqueta == "Marrón")
        assertTrue(CatalogoRasgos.requerir("parte_nucleo").etiqueta == "Núcleo")
    }
}
