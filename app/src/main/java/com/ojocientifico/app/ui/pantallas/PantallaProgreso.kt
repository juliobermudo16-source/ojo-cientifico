package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.RangoExplorador
import com.ojocientifico.app.domain.model.TipoActividad
import com.ojocientifico.app.ui.componentes.BarraXp
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.Estrellas
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.componentes.TituloSeccion
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Progreso del explorador: todo se calcula a partir de lo persistido.
 * Ningún número está escrito a mano.
 */
@Composable
fun PantallaProgreso(panel: PanelViewModel, onVolver: () -> Unit) {
    val estado by panel.laboratorio.collectAsStateWithLifecycle()
    val stats by panel.estadisticas.collectAsStateWithLifecycle()
    val historial by panel.historial.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion("Tu progreso", "Rango ${estado.rango.titulo}", onVolver)

        Column(Modifier.padding(horizontal = 16.dp)) {

            // ---------------- Rangos ----------------
            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = AmarilloDescubrimiento) {
                Text("${estado.xp} XP acumulados", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                BarraXp(estado.fraccionRango, alto = 16.dp)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (estado.xpRestante > 0)
                        "Faltan ${estado.xpRestante} XP para el siguiente rango"
                    else "Rango máximo alcanzado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                RangoExplorador.entries.forEach { rango ->
                    val alcanzado = estado.rango.nivel >= rango.nivel
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    if (alcanzado) VerdeNatural
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (alcanzado) "✓" else "${rango.nivel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (alcanzado) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(rango.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(
                                rango.lema,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${rango.xpNecesario} XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            TituloSeccion("Tus números")
            Spacer(Modifier.height(10.dp))

            val tarjetas = listOf(
                Dato("Misiones superadas", "${stats.misionesCompletadas}", VerdeNatural),
                Dato("Con 3 estrellas", "${stats.misionesPerfectas}", AmarilloDescubrimiento),
                Dato("Fichas en el cuaderno", "${stats.fichasRegistradas}", CoralAviso),
                Dato("Descubrimientos", "${stats.descubrimientosDesbloqueados}", TurquesaAgua),
                Dato("Características distintas", "${stats.rasgosDistintosRegistrados}", AzulMedio),
                Dato("Categorías exploradas", "${stats.categoriasExploradas} de 9", VerdeNatural),
                Dato("Comparaciones", "${stats.comparacionesCompletadas}", AzulMedio),
                Dato("Clasificaciones", "${stats.clasificacionesCompletadas}", TurquesaAgua),
                Dato("Patrones hallados", "${stats.patronesEncontrados}", CoralAviso),
                Dato("Días de expedición", "${stats.diasDeExpedicion}", AmarilloDescubrimiento)
            )
            tarjetas.chunked(2).forEach { fila ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fila.forEach { dato ->
                        TarjetaCampo(
                            modifier = Modifier
                                .weight(1f)
                                .semantics { contentDescription = "${dato.etiqueta}: ${dato.valor}" },
                            colorBorde = dato.color,
                            relleno = 12.dp
                        ) {
                            Text(
                                dato.valor,
                                style = MaterialTheme.typography.headlineMedium,
                                color = dato.color
                            )
                            Text(
                                dato.etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(10.dp))
            TituloSeccion("Historial de expedición")
            Spacer(Modifier.height(10.dp))

            if (historial.isEmpty()) {
                TarjetaCampo(Modifier.fillMaxWidth()) {
                    Text(
                        "Todavía no hay actividades registradas. En cuanto termines una misión aparecerá aquí.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                historial.take(20).forEach { entrada ->
                    TarjetaCampo(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colorBorde = colorDeActividad(entrada.tipo),
                        relleno = 12.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    etiquetaActividad(entrada.tipo),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colorDeActividad(entrada.tipo)
                                )
                                Text(
                                    "${entrada.aciertos} aciertos · ${entrada.fallos} fallos · ${fecha(entrada.fechaMillis)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Estrellas(entrada.estrellas, tamano = 16.dp)
                                Text(
                                    "+${entrada.xpGanado} XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmarilloDescubrimiento
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

private data class Dato(val etiqueta: String, val valor: String, val color: Color)

private fun etiquetaActividad(tipo: TipoActividad): String = when (tipo) {
    TipoActividad.OBSERVACION -> "Observación"
    TipoActividad.COMPARACION -> "Comparación"
    TipoActividad.CLASIFICACION -> "Clasificación"
    TipoActividad.PATRON -> "Búsqueda de patrón"
    TipoActividad.REPASO -> "Repaso"
}

private fun colorDeActividad(tipo: TipoActividad): Color = when (tipo) {
    TipoActividad.OBSERVACION -> TurquesaAgua
    TipoActividad.COMPARACION -> AzulMedio
    TipoActividad.CLASIFICACION -> VerdeNatural
    TipoActividad.PATRON -> CoralAviso
    TipoActividad.REPASO -> AmarilloDescubrimiento
}

private fun fecha(millis: Long): String =
    SimpleDateFormat("d MMM HH:mm", Locale("es", "ES")).format(Date(millis))
