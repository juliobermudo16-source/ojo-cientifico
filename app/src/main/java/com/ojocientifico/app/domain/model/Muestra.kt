package com.ojocientifico.app.domain.model

/**
 * Clave estable de la ilustración científica asociada a cada muestra.
 * La ilustración se dibuja con Compose Canvas: no hay imágenes remotas ni
 * dependencias de red.
 */
enum class ClaveIlustracion {
    HOJA_HELECHO,
    MARIPOSA,
    CARACOL,
    HORMIGA,
    PEZ,
    GIRASOL,
    SEMILLA_VIENTO,
    PLUMA,
    ESTRELLA_MAR,
    ESCARABAJO,
    SETA,
    CRISTAL,
    TELARANA,
    COPO_NIEVE,
    CELULA_VEGETAL,
    RANA
}

/** Gran grupo al que pertenece la muestra. Se usa para filtrar y clasificar. */
enum class ReinoMuestra(val etiqueta: String, val descripcion: String) {
    PLANTA("Plantas", "Seres vivos que fabrican su propio alimento con la luz."),
    ANIMAL("Animales", "Seres vivos que se alimentan de otros seres vivos y se mueven."),
    HONGO("Hongos", "Seres vivos que se alimentan descomponiendo materia."),
    MINERAL("Minerales", "Materia sin vida con una estructura ordenada."),
    MICROSCOPICO("Mundo microscópico", "Solo visible con instrumentos de aumento."),
    CONSTRUCCION("Construcciones naturales", "Objetos fabricados por seres vivos.")
}

/** Ambiente donde se puede encontrar la muestra. */
enum class Habitat(val etiqueta: String) {
    BOSQUE("Bosque"),
    JARDIN("Jardín"),
    OCEANO("Océano"),
    RIO("Río y charcas"),
    CIELO("Aire"),
    SUELO("Suelo"),
    LABORATORIO("Laboratorio")
}

/**
 * Una muestra observable con sus características morfológicas reales.
 *
 * [rasgos] es el registro verdadero contra el que se comparan las observaciones
 * del niño. [distractores] son características plausibles pero incorrectas que
 * se ofrecen para que la observación sea una decisión real y no un simple
 * "marca todo".
 */
data class Muestra(
    val id: String,
    val nombre: String,
    val nombreCientifico: String,
    val reino: ReinoMuestra,
    val habitat: Habitat,
    val ilustracion: ClaveIlustracion,
    val descripcion: String,
    val datoCurioso: String,
    val nivelRequerido: Int,
    val rasgos: Set<String>,
    val distractores: Set<String> = emptySet()
) {
    /** Rasgos verdaderos de una categoría concreta. */
    fun rasgosDe(categoria: CategoriaRasgo): Set<String> =
        rasgos.filter { CatalogoRasgos.categoriaDe(it) == categoria }.toSet()

    /**
     * Universo de opciones que se muestran al observar esta categoría:
     * los rasgos verdaderos más los distractores de la misma categoría.
     */
    fun universoDe(categoria: CategoriaRasgo): List<String> {
        val verdaderos = rasgosDe(categoria)
        val falsos = distractores.filter { CatalogoRasgos.categoriaDe(it) == categoria }
        return (verdaderos + falsos).sortedBy { CatalogoRasgos.etiqueta(it) }
    }

    /** Número total de rasgos verdaderos observables en las categorías indicadas. */
    fun totalObservable(categorias: List<CategoriaRasgo>): Int =
        categorias.sumOf { rasgosDe(it).size }
}
