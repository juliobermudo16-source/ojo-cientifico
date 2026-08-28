package com.ojocientifico.app.data.local

import com.ojocientifico.app.data.local.entity.ConfiguracionEntity
import com.ojocientifico.app.data.local.entity.FichaEntity
import com.ojocientifico.app.data.local.entity.HistorialEntity
import com.ojocientifico.app.data.local.entity.MisionEntity
import com.ojocientifico.app.data.local.entity.MuestraEntity
import com.ojocientifico.app.data.local.entity.OpcionRasgoEntity
import com.ojocientifico.app.data.local.entity.RasgoFalladoEntity
import com.ojocientifico.app.data.local.entity.RasgoMuestraEntity
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.ClaveIlustracion
import com.ojocientifico.app.domain.model.Configuracion
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.EntradaHistorial
import com.ojocientifico.app.domain.model.FichaCientifica
import com.ojocientifico.app.domain.model.Habitat
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.ModoComparacion
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.OpcionRasgo
import com.ojocientifico.app.domain.model.RasgoFallado
import com.ojocientifico.app.domain.model.ReinoMuestra
import com.ojocientifico.app.domain.model.TipoActividad
import com.ojocientifico.app.domain.model.TipoMision

/**
 * Conversión entre entidades de Room y modelos de dominio.
 * Las listas se guardan como texto separado por comas: son cortas, estables y
 * no requieren un serializador externo.
 */

private const val SEPARADOR = ","

internal fun List<String>.aTexto(): String = joinToString(SEPARADOR)

internal fun String.aLista(): List<String> =
    if (isBlank()) emptyList() else split(SEPARADOR).map { it.trim() }.filter { it.isNotEmpty() }

// ------------------------------- Catálogo -------------------------------

fun OpcionRasgo.aEntidad() = OpcionRasgoEntity(
    id = id,
    categoria = categoria.name,
    etiqueta = etiqueta,
    pista = pista
)

fun OpcionRasgoEntity.aDominio() = OpcionRasgo(
    id = id,
    categoria = CategoriaRasgo.valueOf(categoria),
    etiqueta = etiqueta,
    pista = pista
)

fun Muestra.aEntidad() = MuestraEntity(
    id = id,
    nombre = nombre,
    nombreCientifico = nombreCientifico,
    reino = reino.name,
    habitat = habitat.name,
    ilustracion = ilustracion.name,
    descripcion = descripcion,
    datoCurioso = datoCurioso,
    nivelRequerido = nivelRequerido
)

fun Muestra.aRasgosEntidad(): List<RasgoMuestraEntity> =
    rasgos.map { RasgoMuestraEntity(id, it, verdadero = true) } +
        distractores.map { RasgoMuestraEntity(id, it, verdadero = false) }

fun MuestraEntity.aDominio(rasgos: List<RasgoMuestraEntity>) = Muestra(
    id = id,
    nombre = nombre,
    nombreCientifico = nombreCientifico,
    reino = ReinoMuestra.valueOf(reino),
    habitat = Habitat.valueOf(habitat),
    ilustracion = ClaveIlustracion.valueOf(ilustracion),
    descripcion = descripcion,
    datoCurioso = datoCurioso,
    nivelRequerido = nivelRequerido,
    rasgos = rasgos.filter { it.verdadero }.map { it.opcionId }.toSet(),
    distractores = rasgos.filterNot { it.verdadero }.map { it.opcionId }.toSet()
)

fun Mision.aEntidad() = MisionEntity(
    id = id,
    titulo = titulo,
    consigna = consigna,
    instruccionGuia = instruccionGuia,
    tipo = tipo.name,
    nivel = nivel,
    orden = orden,
    muestrasIds = muestrasIds.aTexto(),
    categorias = categorias.map { it.name }.aTexto(),
    modoComparacion = modoComparacion?.name,
    criterio = criterio?.name,
    xpBase = xpBase,
    requiere = requiere
)

fun MisionEntity.aDominio() = Mision(
    id = id,
    titulo = titulo,
    consigna = consigna,
    instruccionGuia = instruccionGuia,
    tipo = TipoMision.valueOf(tipo),
    nivel = nivel,
    orden = orden,
    muestrasIds = muestrasIds.aLista(),
    categorias = categorias.aLista().map { CategoriaRasgo.valueOf(it) },
    modoComparacion = modoComparacion?.let { ModoComparacion.valueOf(it) },
    criterio = criterio?.let { CriterioClasificacion.valueOf(it) },
    xpBase = xpBase,
    requiere = requiere
)

// ------------------------------- Registro -------------------------------

fun FichaEntity.aDominio(rasgosRegistrados: List<String>) = FichaCientifica(
    id = id,
    muestraId = muestraId,
    misionId = misionId,
    fechaMillis = fechaMillis,
    aciertos = aciertos,
    totalEsperado = totalEsperado,
    marcasDeMas = marcasDeMas,
    estrellas = estrellas,
    notaDelExplorador = nota,
    rasgosRegistrados = rasgosRegistrados
)

fun RasgoFalladoEntity.aDominio() = RasgoFallado(
    muestraId = muestraId,
    opcionId = opcionId,
    veces = veces,
    ultimaFechaMillis = ultimaFechaMillis
)

fun HistorialEntity.aDominio() = EntradaHistorial(
    id = id,
    tipo = runCatching { TipoActividad.valueOf(tipo) }.getOrDefault(TipoActividad.OBSERVACION),
    referencia = referencia,
    aciertos = aciertos,
    fallos = fallos,
    estrellas = estrellas,
    xpGanado = xpGanado,
    fechaMillis = fechaMillis
)

fun ConfiguracionEntity.aDominio(alias: String, avatar: Int) = Configuracion(
    alias = alias,
    avatar = avatar,
    sonidoActivo = sonidoActivo,
    vibracionActiva = vibracionActiva,
    animacionesActivas = animacionesActivas,
    textoGrande = textoGrande,
    altoContraste = altoContraste,
    onboardingHecho = onboardingHecho
)

/** Comprueba que un identificador de rasgo existe en el catálogo del dominio. */
fun String.esRasgoValido(): Boolean = CatalogoRasgos.porId(this) != null
