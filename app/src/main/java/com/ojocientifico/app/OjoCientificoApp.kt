package com.ojocientifico.app

import android.app.Application
import com.ojocientifico.app.data.local.OjoDatabase
import com.ojocientifico.app.data.repository.OjoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Punto de arranque de la aplicación.
 *
 * Crea la base de datos local y siembra el contenido inicial para que la
 * primera apertura ya tenga muestras, misiones, insignias y colección.
 */
class OjoCientificoApp : Application() {

    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val repositorio: OjoRepository by lazy {
        OjoRepository(OjoDatabase.obtener(this))
    }

    override fun onCreate() {
        super.onCreate()
        alcance.launch { repositorio.asegurarSemilla() }
    }
}
