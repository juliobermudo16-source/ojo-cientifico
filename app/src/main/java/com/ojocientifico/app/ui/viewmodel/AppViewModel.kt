package com.ojocientifico.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ojocientifico.app.data.repository.OjoRepository
import com.ojocientifico.app.domain.model.Configuracion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado transversal: preferencias, alias del explorador y reinicio.
 * Vive mientras vive la aplicación, porque el tema depende de él.
 */
class AppViewModel(private val repositorio: OjoRepository) : ViewModel() {

    val configuracion: StateFlow<Configuracion> = repositorio.configuracion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    init {
        viewModelScope.launch { repositorio.asegurarSemilla() }
    }

    fun guardar(nueva: Configuracion) {
        viewModelScope.launch { repositorio.guardarConfiguracion(nueva) }
    }

    fun completarOnboarding(alias: String, avatar: Int) {
        viewModelScope.launch { repositorio.completarOnboarding(alias, avatar) }
    }

    fun reiniciarProgreso() {
        viewModelScope.launch { repositorio.reiniciarProgreso() }
    }

    class Factory(private val repositorio: OjoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AppViewModel(repositorio) as T
    }
}
