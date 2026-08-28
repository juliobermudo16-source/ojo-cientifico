package com.ojocientifico.app.domain.logica

import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.ResultadoClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion

/**
 * Registro morfológico: compara lo que el niño marcó con las características
 * reales almacenadas para la muestra.
 */
object EvaluadorObservacion {

    /** Características verdaderas de la muestra dentro de las categorías pedidas. */
    fun esperados(muestra: Muestra, categorias: List<CategoriaRasgo>): Set<String> =
        categorias.flatMap { muestra.rasgosDe(it) }.toSet()

    /**
     * Opciones que se ofrecen al niño: las verdaderas mezcladas con distractores
     * de la misma categoría, para que marcar sea una decisión real.
     */
    fun universo(muestra: Muestra, categorias: List<CategoriaRasgo>): Map<CategoriaRasgo, List<String>> =
        categorias.associateWith { muestra.universoDe(it) }
            .filterValues { it.isNotEmpty() }

    fun evaluar(
        muestra: Muestra,
        categorias: List<CategoriaRasgo>,
        seleccion: Set<String>
    ): ResultadoSeleccion {
        val ofrecidas = universo(muestra, categorias).values.flatten().toSet()
        // Solo cuentan las marcas que realmente estaban en pantalla.
        val limpia = seleccion intersect ofrecidas
        return ResultadoSeleccion(
            esperados = esperados(muestra, categorias),
            seleccionados = limpia
        )
    }
}

/**
 * Comparación entre dos muestras. Semejanzas y diferencias se CALCULAN a partir
 * de los conjuntos de características reales.
 */
object EvaluadorComparacion {

    fun semejanzas(a: Muestra, b: Muestra, categorias: List<CategoriaRasgo>): Set<String> =
        (a.rasgos intersect b.rasgos).filtrarPor(categorias)

    fun diferencias(a: Muestra, b: Muestra, categorias: List<CategoriaRasgo>): Set<String> =
        (a.rasgos - b.rasgos).filtrarPor(categorias)

    /** Todas las características que aparecen en alguna de las dos muestras. */
    fun universo(a: Muestra, b: Muestra, categorias: List<CategoriaRasgo>): List<String> =
        (a.rasgos + b.rasgos).filtrarPor(categorias)
            .sortedWith(compareBy({ CatalogoRasgos.categoriaDe(it)?.ordinal ?: 99 }, { CatalogoRasgos.etiqueta(it) }))

    fun evaluarSemejanzas(
        a: Muestra,
        b: Muestra,
        categorias: List<CategoriaRasgo>,
        seleccion: Set<String>
    ): ResultadoSeleccion = ResultadoSeleccion(
        esperados = semejanzas(a, b, categorias),
        seleccionados = seleccion intersect universo(a, b, categorias).toSet()
    )

    fun evaluarDiferencias(
        a: Muestra,
        b: Muestra,
        categorias: List<CategoriaRasgo>,
        seleccion: Set<String>
    ): ResultadoSeleccion = ResultadoSeleccion(
        esperados = diferencias(a, b, categorias),
        seleccionados = seleccion intersect universo(a, b, categorias).toSet()
    )
}

/**
 * Búsqueda de patrones: qué característica comparten TODAS las muestras del
 * conjunto. Es la actividad central del rango Descubridor.
 */
object DetectorPatrones {

    fun patronComun(muestras: List<Muestra>, categorias: List<CategoriaRasgo>): Set<String> {
        if (muestras.isEmpty()) return emptySet()
        val comun = muestras
            .map { it.rasgos }
            .reduce { acumulado, siguiente -> acumulado intersect siguiente }
        return comun.filtrarPor(categorias)
    }

    fun universo(muestras: List<Muestra>, categorias: List<CategoriaRasgo>): List<String> =
        muestras.flatMap { it.rasgos }.toSet().filtrarPor(categorias)
            .sortedWith(compareBy({ CatalogoRasgos.categoriaDe(it)?.ordinal ?: 99 }, { CatalogoRasgos.etiqueta(it) }))

    fun evaluar(
        muestras: List<Muestra>,
        categorias: List<CategoriaRasgo>,
        seleccion: Set<String>
    ): ResultadoSeleccion = ResultadoSeleccion(
        esperados = patronComun(muestras, categorias),
        seleccionados = seleccion intersect universo(muestras, categorias).toSet()
    )
}

/**
 * Clasificación: el grupo correcto de cada muestra se deduce de sus datos con
 * [CriterioClasificacion.grupoDe]; nunca está escrito en la actividad.
 */
object EvaluadorClasificacion {

    fun esperado(muestras: List<Muestra>, criterio: CriterioClasificacion): Map<String, String> =
        muestras.associate { it.id to criterio.grupoDe(it) }

    fun evaluar(
        muestras: List<Muestra>,
        criterio: CriterioClasificacion,
        asignaciones: Map<String, String>
    ): ResultadoClasificacion {
        val idsValidos = muestras.map { it.id }.toSet()
        val gruposValidos = criterio.grupos.map { it.id }.toSet()
        val limpias = asignaciones
            .filterKeys { it in idsValidos }
            .filterValues { it in gruposValidos }
        return ResultadoClasificacion(
            esperado = esperado(muestras, criterio),
            asignado = limpias
        )
    }

    /** Grupos que realmente aparecen en este conjunto de muestras. */
    fun gruposUtiles(muestras: List<Muestra>, criterio: CriterioClasificacion) =
        criterio.grupos.filter { grupo -> muestras.any { criterio.grupoDe(it) == grupo.id } }
}

private fun Set<String>.filtrarPor(categorias: List<CategoriaRasgo>): Set<String> =
    if (categorias.isEmpty()) this
    else filterTo(mutableSetOf()) { CatalogoRasgos.categoriaDe(it) in categorias }
