package com.ojocientifico.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ojocientifico.app.data.repository.OjoRepository
import com.ojocientifico.app.ui.pantallas.PantallaActividad
import com.ojocientifico.app.ui.pantallas.PantallaAjustes
import com.ojocientifico.app.ui.pantallas.PantallaBienvenida
import com.ojocientifico.app.ui.pantallas.PantallaColeccion
import com.ojocientifico.app.ui.pantallas.PantallaCuaderno
import com.ojocientifico.app.ui.pantallas.PantallaInsignias
import com.ojocientifico.app.ui.pantallas.PantallaLaboratorio
import com.ojocientifico.app.ui.pantallas.PantallaMisiones
import com.ojocientifico.app.ui.pantallas.PantallaMuestrario
import com.ojocientifico.app.ui.pantallas.PantallaProgreso
import com.ojocientifico.app.ui.pantallas.PantallaRepaso
import com.ojocientifico.app.ui.viewmodel.ActividadViewModel
import com.ojocientifico.app.ui.viewmodel.AppViewModel
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/** Rutas de navegación de Ojo Científico. */
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LABORATORIO = "laboratorio"
    const val MISIONES = "misiones"
    const val MUESTRARIO = "muestrario"
    const val CUADERNO = "cuaderno"
    const val COLECCION = "coleccion"
    const val INSIGNIAS = "insignias"
    const val REPASO = "repaso"
    const val PROGRESO = "progreso"
    const val AJUSTES = "ajustes"

    const val ARG_MISION = "misionId"
    const val ARG_MUESTRA = "muestraId"

    const val MISION = "mision/{$ARG_MISION}"
    const val EXPLORAR = "explorar/{$ARG_MUESTRA}"

    fun mision(id: String) = "mision/$id"
    fun explorar(id: String) = "explorar/$id"
}

@Composable
fun NavegacionOjo(
    repositorio: OjoRepository,
    appViewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    val configuracion by appViewModel.configuracion.collectAsStateWithLifecycle()
    val panel: PanelViewModel = viewModel(factory = PanelViewModel.Factory(repositorio))

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = if (configuracion.onboardingHecho) Rutas.LABORATORIO else Rutas.BIENVENIDA
            ) {
                composable(Rutas.BIENVENIDA) {
                    PantallaBienvenida(
                        onEmpezar = { alias, avatar ->
                            appViewModel.completarOnboarding(alias, avatar)
                            navController.navigate(Rutas.LABORATORIO) {
                                popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Rutas.LABORATORIO) {
                    PantallaLaboratorio(
                        panel = panel,
                        onMision = { navController.navigate(Rutas.mision(it)) },
                        onIrA = { navController.navigate(it) }
                    )
                }

                composable(Rutas.MISIONES) {
                    PantallaMisiones(
                        panel = panel,
                        onVolver = { navController.popBackStack() },
                        onMision = { navController.navigate(Rutas.mision(it)) }
                    )
                }

                composable(Rutas.MUESTRARIO) {
                    PantallaMuestrario(
                        panel = panel,
                        onVolver = { navController.popBackStack() },
                        onMuestra = { navController.navigate(Rutas.explorar(it)) }
                    )
                }

                composable(
                    route = Rutas.MISION,
                    arguments = listOf(navArgument(Rutas.ARG_MISION) { type = NavType.StringType })
                ) { entrada ->
                    val misionId = entrada.arguments?.getString(Rutas.ARG_MISION).orEmpty()
                    val actividad: ActividadViewModel =
                        viewModel(factory = ActividadViewModel.Factory(repositorio))
                    PantallaActividad(
                        viewModel = actividad,
                        arranque = { actividad.iniciarMision(misionId) },
                        onSalir = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Rutas.EXPLORAR,
                    arguments = listOf(navArgument(Rutas.ARG_MUESTRA) { type = NavType.StringType })
                ) { entrada ->
                    val muestraId = entrada.arguments?.getString(Rutas.ARG_MUESTRA).orEmpty()
                    val actividad: ActividadViewModel =
                        viewModel(factory = ActividadViewModel.Factory(repositorio))
                    PantallaActividad(
                        viewModel = actividad,
                        arranque = { actividad.iniciarExploracion(muestraId) },
                        onSalir = { navController.popBackStack() }
                    )
                }

                composable(Rutas.CUADERNO) {
                    PantallaCuaderno(panel = panel, onVolver = { navController.popBackStack() })
                }

                composable(Rutas.COLECCION) {
                    PantallaColeccion(
                        panel = panel,
                        onVolver = { navController.popBackStack() },
                        onExplorar = { navController.navigate(Rutas.explorar(it)) }
                    )
                }

                composable(Rutas.INSIGNIAS) {
                    PantallaInsignias(panel = panel, onVolver = { navController.popBackStack() })
                }

                composable(Rutas.REPASO) {
                    PantallaRepaso(
                        panel = panel,
                        onVolver = { navController.popBackStack() },
                        onRepasar = { navController.navigate(Rutas.explorar(it)) }
                    )
                }

                composable(Rutas.PROGRESO) {
                    PantallaProgreso(panel = panel, onVolver = { navController.popBackStack() })
                }

                composable(Rutas.AJUSTES) {
                    PantallaAjustes(
                        appViewModel = appViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
