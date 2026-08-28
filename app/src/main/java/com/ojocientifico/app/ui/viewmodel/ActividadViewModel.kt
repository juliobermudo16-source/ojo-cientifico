package com.ojocientifico.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ojocientifico.app.data.repository.OjoRepository
import com.ojocientifico.app.domain.logica.DetectorPatrones
import com.ojocientifico.app.domain.logica.EvaluadorClasificacion
import com.ojocientifico.app.domain.logica.EvaluadorComparacion
import com.ojocientifico.app.domain.logica.EvaluadorObservacion
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.GrupoClasificacion
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.ModoComparacion
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.ResultadoClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import com.ojocientifico.app.domain.model.ResumenMision
import com.ojocientifico.app.domain.model.TipoMision
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la actividad que el niño tiene delante en este momento. */
sealed interface EstadoActividad {

    data object Cargando : EstadoActividad

    data class NoDisponible(val motivo: String) : EstadoActividad

    /** Registro morfológico de una muestra, categoría a categoría. */
    data class Observacion(
        val mision: Mision?,
        val muestra: Muestra,
        val categorias: List<CategoriaRasgo>,
        val opciones: Map<CategoriaRasgo, List<String>>,
        val categoriaActiva: CategoriaRasgo,
        val seleccion: Set<String> = emptySet(),
        val nota: String = "",
        val lupaActiva: Boolean = false
    ) : EstadoActividad {
        val categoriasVisitadas: Int get() = categorias.indexOf(categoriaActiva) + 1
        val puedeComprobar: Boolean get() = seleccion.isNotEmpty()
    }

    /** Comparación de dos muestras: semejanzas o diferencias. */
    data class Comparacion(
        val mision: Mision,
        val a: Muestra,
        val b: Muestra,
        val modo: ModoComparacion,
        val opciones: List<String>,
        val seleccion: Set<String> = emptySet()
    ) : EstadoActividad {
        val puedeComprobar: Boolean get() = seleccion.isNotEmpty()
    }

    /** Búsqueda del rasgo que comparten todas las muestras. */
    data class Patron(
        val mision: Mision,
        val muestras: List<Muestra>,
        val opciones: List<String>,
        val seleccion: Set<String> = emptySet()
    ) : EstadoActividad {
        val puedeComprobar: Boolean get() = seleccion.isNotEmpty()
    }

    /** Reparto de muestras en grupos según un criterio. */
    data class Clasificacion(
        val mision: Mision,
        val criterio: CriterioClasificacion,
        val muestras: List<Muestra>,
        val grupos: List<GrupoClasificacion>,
        val asignaciones: Map<String, String> = emptyMap(),
        val seleccionada: String? = null
    ) : EstadoActividad {
        val pendientes: List<Muestra> get() = muestras.filter { it.id !in asignaciones }
        val puedeComprobar: Boolean get() = asignaciones.size == muestras.size
    }

    /** Pantalla de resultado con el feedback educativo. */
    data class Terminada(
        val resumen: ResumenMision,
        val esperados: Set<String>,
        val aciertos: Set<String>,
        val omitidos: Set<String>,
        val sobrantes: Set<String>,
        val tituloActividad: String,
        /** Nombres legibles cuando el desglose no son características. */
        val nombres: Map<String, String> = emptyMap(),
        /** True cuando lo que se desglosa son muestras colocadas en grupos. */
        val desgloseDeMuestras: Boolean = false
    ) : EstadoActividad
}

/**
 * Conduce una actividad de principio a fin: prepara los datos, recoge lo que el
 * niño marca, evalúa contra la información real y persiste el resultado.
 */
class ActividadViewModel(private val repositorio: OjoRepository) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoActividad>(EstadoActividad.Cargando)
    val estado: StateFlow<EstadoActividad> = _estado.asStateFlow()

    private var estadoPrevio: EstadoActividad? = null

    // ============================== Arranque ==============================

    fun iniciarMision(misionId: String) {
        viewModelScope.launch {
            val mision = repositorio.mision(misionId)
            if (mision == null) {
                _estado.value = EstadoActividad.NoDisponible("Esta misión ya no está disponible.")
                return@launch
            }
            val muestras = mision.muestrasIds.mapNotNull { repositorio.muestra(it) }
            if (muestras.isEmpty()) {
                _estado.value = EstadoActividad.NoDisponible("No se encontraron las muestras.")
                return@launch
            }
            _estado.value = when (mision.tipo) {
                TipoMision.OBSERVACION -> observacionDe(mision, muestras.first())
                TipoMision.COMPARACION -> comparacionDe(mision, muestras)
                TipoMision.PATRON -> patronDe(mision, muestras)
                TipoMision.CLASIFICACION -> clasificacionDe(mision, muestras)
            }
        }
    }

    /** Exploración libre desde el laboratorio, sin misión asociada. */
    fun iniciarExploracion(muestraId: String) {
        viewModelScope.launch {
            val nivel = repositorio.nivelActual()
            val muestra = repositorio.muestra(muestraId)
            if (muestra == null) {
                _estado.value = EstadoActividad.NoDisponible("Muestra no encontrada.")
                return@launch
            }
            _estado.value = observacionDe(
                mision = null,
                muestra = muestra,
                categorias = CategoriaRasgo.paraNivel(nivel)
            )
        }
    }

    private fun observacionDe(
        mision: Mision?,
        muestra: Muestra,
        categorias: List<CategoriaRasgo> = mision?.categorias.orEmpty()
    ): EstadoActividad {
        val efectivas = categorias
            .ifEmpty { CategoriaRasgo.entries.toList() }
            .filter { muestra.rasgosDe(it).isNotEmpty() }
        if (efectivas.isEmpty()) {
            return EstadoActividad.NoDisponible("Esta muestra aún no tiene datos que observar.")
        }
        return EstadoActividad.Observacion(
            mision = mision,
            muestra = muestra,
            categorias = efectivas,
            opciones = EvaluadorObservacion.universo(muestra, efectivas),
            categoriaActiva = efectivas.first()
        )
    }

    private fun comparacionDe(mision: Mision, muestras: List<Muestra>): EstadoActividad {
        if (muestras.size < 2) return EstadoActividad.NoDisponible("Faltan muestras que comparar.")
        val modo = mision.modoComparacion ?: ModoComparacion.SEMEJANZAS
        return EstadoActividad.Comparacion(
            mision = mision,
            a = muestras[0],
            b = muestras[1],
            modo = modo,
            opciones = EvaluadorComparacion.universo(muestras[0], muestras[1], mision.categorias)
        )
    }

    private fun patronDe(mision: Mision, muestras: List<Muestra>): EstadoActividad =
        EstadoActividad.Patron(
            mision = mision,
            muestras = muestras,
            opciones = DetectorPatrones.universo(muestras, mision.categorias)
        )

    private fun clasificacionDe(mision: Mision, muestras: List<Muestra>): EstadoActividad {
        val criterio = mision.criterio
            ?: return EstadoActividad.NoDisponible("Esta misión no tiene criterio de clasificación.")
        return EstadoActividad.Clasificacion(
            mision = mision,
            criterio = criterio,
            muestras = muestras,
            grupos = EvaluadorClasificacion.gruposUtiles(muestras, criterio)
        )
    }

    // ============================ Interacción ============================

    fun alternarOpcion(opcionId: String) {
        _estado.value = when (val actual = _estado.value) {
            is EstadoActividad.Observacion -> actual.copy(seleccion = actual.seleccion.alternar(opcionId))
            is EstadoActividad.Comparacion -> actual.copy(seleccion = actual.seleccion.alternar(opcionId))
            is EstadoActividad.Patron -> actual.copy(seleccion = actual.seleccion.alternar(opcionId))
            else -> actual
        }
    }

    fun cambiarCategoria(categoria: CategoriaRasgo) {
        val actual = _estado.value as? EstadoActividad.Observacion ?: return
        if (categoria !in actual.categorias) return
        _estado.value = actual.copy(categoriaActiva = categoria)
    }

    fun avanzarCategoria() {
        val actual = _estado.value as? EstadoActividad.Observacion ?: return
        val indice = actual.categorias.indexOf(actual.categoriaActiva)
        if (indice < actual.categorias.lastIndex) {
            _estado.value = actual.copy(categoriaActiva = actual.categorias[indice + 1])
        }
    }

    fun retrocederCategoria() {
        val actual = _estado.value as? EstadoActividad.Observacion ?: return
        val indice = actual.categorias.indexOf(actual.categoriaActiva)
        if (indice > 0) {
            _estado.value = actual.copy(categoriaActiva = actual.categorias[indice - 1])
        }
    }

    fun alternarLupa() {
        val actual = _estado.value as? EstadoActividad.Observacion ?: return
        _estado.value = actual.copy(lupaActiva = !actual.lupaActiva)
    }

    fun escribirNota(texto: String) {
        val actual = _estado.value as? EstadoActividad.Observacion ?: return
        _estado.value = actual.copy(nota = texto.take(280))
    }

    fun seleccionarMuestra(muestraId: String?) {
        val actual = _estado.value as? EstadoActividad.Clasificacion ?: return
        _estado.value = actual.copy(seleccionada = if (actual.seleccionada == muestraId) null else muestraId)
    }

    /** Coloca la muestra seleccionada en un grupo, o la saca si ya estaba ahí. */
    fun asignarAGrupo(grupoId: String) {
        val actual = _estado.value as? EstadoActividad.Clasificacion ?: return
        val muestraId = actual.seleccionada ?: return
        val nuevas = actual.asignaciones.toMutableMap()
        if (nuevas[muestraId] == grupoId) nuevas.remove(muestraId) else nuevas[muestraId] = grupoId
        _estado.value = actual.copy(asignaciones = nuevas, seleccionada = null)
    }

    fun retirarDeGrupo(muestraId: String) {
        val actual = _estado.value as? EstadoActividad.Clasificacion ?: return
        _estado.value = actual.copy(asignaciones = actual.asignaciones - muestraId)
    }

    // ============================ Comprobación ============================

    /**
     * Evalúa la actividad contra los datos reales, guarda todo y pasa a la
     * pantalla de resultado. Ignora pulsaciones repetidas.
     */
    fun comprobar() {
        val actual = _estado.value
        if (actual is EstadoActividad.Terminada || actual is EstadoActividad.Cargando) return
        estadoPrevio = actual
        viewModelScope.launch {
            when (actual) {
                is EstadoActividad.Observacion -> comprobarObservacion(actual)
                is EstadoActividad.Comparacion -> comprobarComparacion(actual)
                is EstadoActividad.Patron -> comprobarPatron(actual)
                is EstadoActividad.Clasificacion -> comprobarClasificacion(actual)
                else -> Unit
            }
        }
    }

    private suspend fun comprobarObservacion(estado: EstadoActividad.Observacion) {
        val resultado: ResultadoSeleccion = EvaluadorObservacion.evaluar(
            muestra = estado.muestra,
            categorias = estado.categorias,
            seleccion = estado.seleccion
        )
        val resumen = repositorio.registrarObservacion(
            muestra = estado.muestra,
            misionId = estado.mision?.id,
            resultado = resultado,
            nota = estado.nota
        )
        _estado.value = EstadoActividad.Terminada(
            resumen = resumen,
            esperados = resultado.esperados,
            aciertos = resultado.aciertos,
            omitidos = resultado.omitidos,
            sobrantes = resultado.falsosPositivos,
            tituloActividad = estado.mision?.titulo ?: "Exploración de ${estado.muestra.nombre}"
        )
    }

    private suspend fun comprobarComparacion(estado: EstadoActividad.Comparacion) {
        val semejanzas = estado.modo == ModoComparacion.SEMEJANZAS
        val resultado = if (semejanzas) {
            EvaluadorComparacion.evaluarSemejanzas(
                estado.a, estado.b, estado.mision.categorias, estado.seleccion
            )
        } else {
            EvaluadorComparacion.evaluarDiferencias(
                estado.a, estado.b, estado.mision.categorias, estado.seleccion
            )
        }
        val resumen = repositorio.registrarComparacion(
            mision = estado.mision,
            a = estado.a,
            b = estado.b,
            resultado = resultado,
            buscabaSemejanzas = semejanzas
        )
        _estado.value = EstadoActividad.Terminada(
            resumen = resumen,
            esperados = resultado.esperados,
            aciertos = resultado.aciertos,
            omitidos = resultado.omitidos,
            sobrantes = resultado.falsosPositivos,
            tituloActividad = estado.mision.titulo
        )
    }

    private suspend fun comprobarPatron(estado: EstadoActividad.Patron) {
        val resultado = DetectorPatrones.evaluar(
            muestras = estado.muestras,
            categorias = estado.mision.categorias,
            seleccion = estado.seleccion
        )
        val resumen = repositorio.registrarPatron(estado.mision, estado.muestras, resultado)
        _estado.value = EstadoActividad.Terminada(
            resumen = resumen,
            esperados = resultado.esperados,
            aciertos = resultado.aciertos,
            omitidos = resultado.omitidos,
            sobrantes = resultado.falsosPositivos,
            tituloActividad = estado.mision.titulo
        )
    }

    private suspend fun comprobarClasificacion(estado: EstadoActividad.Clasificacion) {
        val resultado: ResultadoClasificacion = EvaluadorClasificacion.evaluar(
            muestras = estado.muestras,
            criterio = estado.criterio,
            asignaciones = estado.asignaciones
        )
        val nombres = estado.muestras.associate { it.id to it.nombre }
        val resumen = repositorio.registrarClasificacion(
            mision = estado.mision,
            criterio = estado.criterio,
            resultado = resultado,
            nombrePorId = { nombres[it] ?: it }
        )
        _estado.value = EstadoActividad.Terminada(
            resumen = resumen,
            esperados = resultado.esperado.keys,
            aciertos = resultado.correctas,
            omitidos = resultado.sinAsignar,
            sobrantes = resultado.incorrectas,
            tituloActividad = estado.mision.titulo,
            nombres = nombres,
            desgloseDeMuestras = true
        )
    }

    /** Vuelve a la actividad con la selección vacía para intentarlo de nuevo. */
    fun reintentar() {
        _estado.value = when (val previo = estadoPrevio) {
            is EstadoActividad.Observacion -> previo.copy(seleccion = emptySet())
            is EstadoActividad.Comparacion -> previo.copy(seleccion = emptySet())
            is EstadoActividad.Patron -> previo.copy(seleccion = emptySet())
            is EstadoActividad.Clasificacion -> previo.copy(asignaciones = emptyMap(), seleccionada = null)
            else -> _estado.value
        }
    }

    private fun Set<String>.alternar(id: String): Set<String> =
        if (id in this) this - id else this + id

    class Factory(private val repositorio: OjoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ActividadViewModel(repositorio) as T
    }
}
