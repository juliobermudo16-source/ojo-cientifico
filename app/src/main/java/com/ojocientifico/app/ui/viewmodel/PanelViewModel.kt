package com.ojocientifico.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ojocientifico.app.data.repository.OjoRepository
import com.ojocientifico.app.domain.logica.CalculadoraProgreso
import com.ojocientifico.app.domain.logica.DesbloqueoMisiones
import com.ojocientifico.app.domain.logica.InsigniaConProgreso
import com.ojocientifico.app.domain.logica.MotorInsignias
import com.ojocientifico.app.domain.logica.PlanificadorRepaso
import com.ojocientifico.app.domain.logica.SugerenciaRepaso
import com.ojocientifico.app.domain.model.CatalogoInsignias
import com.ojocientifico.app.domain.model.EstadisticasExplorador
import com.ojocientifico.app.domain.model.EstadoMision
import com.ojocientifico.app.domain.model.EntradaHistorial
import com.ojocientifico.app.domain.model.FichaCientifica
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.RangoExplorador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Una misión con su estado de desbloqueo y las estrellas ya conseguidas. */
data class MisionEnPanel(
    val mision: Mision,
    val estado: EstadoMision,
    val estrellas: Int
)

/** Todo lo que necesita el laboratorio (pantalla principal). */
data class EstadoLaboratorio(
    val alias: String = "Explorador",
    val avatar: Int = 0,
    val xp: Int = 0,
    val rango: RangoExplorador = RangoExplorador.OBSERVADOR,
    val fraccionRango: Float = 0f,
    val xpRestante: Int = 0,
    val misionActual: Mision? = null,
    val misiones: List<MisionEnPanel> = emptyList(),
    val avanceExpedicion: Float = 0f,
    val descubrimientos: Int = 0,
    val totalMuestras: Int = 0,
    val insignias: Int = 0,
    val totalInsignias: Int = 0,
    val fichas: Int = 0,
    val pendientesDeRepaso: Int = 0,
    val cargando: Boolean = true
)

/**
 * Estado de lectura compartido por el laboratorio, las misiones, el cuaderno,
 * la colección, las insignias, el repaso y el progreso.
 */
class PanelViewModel(private val repositorio: OjoRepository) : ViewModel() {

    private val _muestras = MutableStateFlow<List<Muestra>>(emptyList())
    val muestras: StateFlow<List<Muestra>> = _muestras.asStateFlow()

    private val _misiones = MutableStateFlow<List<Mision>>(emptyList())

    private val _estadisticas = MutableStateFlow(EstadisticasExplorador())
    val estadisticas: StateFlow<EstadisticasExplorador> = _estadisticas.asStateFlow()

    val fichas: StateFlow<List<FichaCientifica>> = repositorio.fichas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val historial: StateFlow<List<EntradaHistorial>> = repositorio.historial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val descubiertos: StateFlow<Set<String>> = repositorio.descubrimientosDesbloqueados
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val repaso: StateFlow<List<SugerenciaRepaso>> = repositorio.fallos
        .map { PlanificadorRepaso.sugerencias(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val insignias: StateFlow<List<InsigniaConProgreso>> =
        combine(repositorio.insigniasDesbloqueadas, estadisticas) { ganadas, estado ->
            MotorInsignias.panel(estado, ganadas)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _laboratorio = MutableStateFlow(EstadoLaboratorio())
    val laboratorio: StateFlow<EstadoLaboratorio> = _laboratorio.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.asegurarSemilla()
            _muestras.value = repositorio.muestras()
            _misiones.value = repositorio.misiones()
            observarPanel()
        }
    }

    private suspend fun observarPanel() {
        combine(
            repositorio.perfil,
            repositorio.misionesCompletadas,
            repositorio.estrellasPorMision,
            repositorio.descubrimientosDesbloqueados,
            repositorio.insigniasDesbloqueadas
        ) { perfil, completadas, estrellas, descubiertos, insignias ->
            Quintupla(perfil, completadas, estrellas, descubiertos, insignias)
        }.combine(repositorio.fallos) { base, fallos ->
            base to fallos
        }.collect { (base, fallos) ->
            val (perfil, completadas, estrellas, descubiertos, insigniasGanadas) = base
            val xp = perfil?.xp ?: 0
            val lista = _misiones.value
            val estados = DesbloqueoMisiones.estados(lista, xp, completadas)

            val stats = repositorio.estadisticas()
            _estadisticas.value = stats

            _laboratorio.value = EstadoLaboratorio(
                alias = perfil?.alias ?: "Explorador",
                avatar = perfil?.avatar ?: 0,
                xp = xp,
                rango = CalculadoraProgreso.rango(xp),
                fraccionRango = CalculadoraProgreso.fraccionRango(xp),
                xpRestante = CalculadoraProgreso.xpRestante(xp),
                misionActual = DesbloqueoMisiones.siguienteDisponible(lista, xp, completadas),
                misiones = lista.map {
                    MisionEnPanel(
                        mision = it,
                        estado = estados[it.id] ?: EstadoMision.BLOQUEADA,
                        estrellas = estrellas[it.id] ?: 0
                    )
                },
                avanceExpedicion = DesbloqueoMisiones.avanceTotal(lista, completadas),
                descubrimientos = descubiertos.size,
                totalMuestras = _muestras.value.size,
                insignias = insigniasGanadas.size,
                totalInsignias = CatalogoInsignias.todas.size,
                fichas = stats.fichasRegistradas,
                pendientesDeRepaso = PlanificadorRepaso.sugerencias(fallos).size,
                cargando = false
            )
        }
    }

    /** Muestras que el explorador ya puede abrir en el laboratorio libre. */
    fun muestrasDisponibles(nivel: Int): List<Muestra> =
        _muestras.value.filter { it.nivelRequerido <= nivel }

    fun muestra(id: String): Muestra? = _muestras.value.firstOrNull { it.id == id }

    fun mision(id: String): Mision? = _misiones.value.firstOrNull { it.id == id }

    fun fichasDe(muestraId: String) = repositorio.fichasDe(muestraId)

    private data class Quintupla(
        val perfil: com.ojocientifico.app.data.local.entity.PerfilEntity?,
        val completadas: Set<String>,
        val estrellas: Map<String, Int>,
        val descubiertos: Set<String>,
        val insignias: Set<String>
    )

    class Factory(private val repositorio: OjoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PanelViewModel(repositorio) as T
    }
}
