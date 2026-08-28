package com.ojocientifico.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ojocientifico.app.ui.nav.NavegacionOjo
import com.ojocientifico.app.ui.theme.AjustesVisuales
import com.ojocientifico.app.ui.theme.OjoCientificoTheme
import com.ojocientifico.app.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val app = application as OjoCientificoApp
            val vm: AppViewModel = viewModel(
                factory = AppViewModel.Factory(app.repositorio)
            )
            val configuracion by vm.configuracion.collectAsStateWithLifecycle()

            OjoCientificoTheme(
                ajustes = AjustesVisuales(
                    textoGrande = configuracion.textoGrande,
                    altoContraste = configuracion.altoContraste,
                    animaciones = configuracion.animacionesActivas
                )
            ) {
                NavegacionOjo(repositorio = app.repositorio, appViewModel = vm)
            }
        }
    }
}
