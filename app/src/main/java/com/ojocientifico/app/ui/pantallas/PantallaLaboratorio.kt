package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.TipoMision
import com.ojocientifico.app.ui.componentes.BarraXp
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.GloboDeIris
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.componentes.TituloSeccion
import com.ojocientifico.app.ui.ilustraciones.FondoCuaderno
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.IconoLaboratorio
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.ilustraciones.ObjetoLaboratorio
import com.ojocientifico.app.ui.nav.Rutas
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

private val ColoresChapa = listOf(VerdeNatural, TurquesaAgua, AmarilloDescubrimiento, CoralAviso)

/**
 * Centro de investigación: la pantalla principal.
 * Muestra quién eres, qué te toca hacer ahora y a qué zonas del laboratorio
 * puedes ir. No es una lista de botones: cada zona tiene su ilustración y su
 * dato real de progreso.
 */
@Composable
fun PantallaLaboratorio(
    panel: PanelViewModel,
    onMision: (String) -> Unit,
    onIrA: (String) -> Unit
) {
    val estado by panel.laboratorio.collectAsStateWithLifecycle()
    val muestras by panel.muestras.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ---------------- Cabecera: estación de campo ----------------
        Box(
            Modifier
                .fillMaxWidth()
                .background(AzulProfundo, RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp))
        ) {
            FondoCuaderno(
                modifier = Modifier.matchParentSize(),
                colorLinea = Color.White,
                intensidad = 0.06f
            )
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ColoresChapa[estado.avatar.coerceIn(ColoresChapa.indices)])
                            .border(3.dp, AmarilloDescubrimiento, CircleShape)
                            .semantics { contentDescription = "Chapa de ${estado.alias}" }
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            estado.alias,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            "Rango ${estado.rango.nivel} · ${estado.rango.titulo}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AmarilloDescubrimiento
                        )
                    }
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable { onIrA(Rutas.AJUSTES) }
                            .semantics { contentDescription = "Ajustes" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Settings, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${estado.xp} XP",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Spacer(Modifier.width(10.dp))
                    BarraXp(fraccion = estado.fraccionRango, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (estado.xpRestante > 0)
                        "Te faltan ${estado.xpRestante} XP para el siguiente rango"
                    else "Has alcanzado el rango máximo: Descubridor",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        Column(Modifier.padding(16.dp)) {

            // ---------------- Misión actual ----------------
            val mision = estado.misionActual
            if (mision != null) {
                TituloSeccion("Tu misión de hoy")
                Spacer(Modifier.height(10.dp))
                TarjetaCampo(
                    modifier = Modifier.fillMaxWidth(),
                    colorBorde = AmarilloDescubrimiento,
                    grosorBorde = 3.dp,
                    onClick = { onMision(mision.id) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val primera = mision.muestrasIds.firstOrNull()?.let { id ->
                            muestras.firstOrNull { it.id == id }
                        }
                        if (primera != null) {
                            Box(
                                Modifier
                                    .size(96.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                IlustracionMuestra(
                                    primera.ilustracion,
                                    primera.nombre,
                                    Modifier.fillMaxSize().padding(6.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            EtiquetaTipo(mision.tipo)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                mision.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                mision.consigna,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    GloboDeIris(texto = mision.instruccionGuia, gesto = GestoIris.ANIMANDO)
                    Spacer(Modifier.height(14.dp))
                    BotonExpedicion(
                        texto = mision.tipo.verbo,
                        onClick = { onMision(mision.id) },
                        icono = Icons.Filled.PlayArrow,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else if (!estado.cargando) {
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = VerdeNatural) {
                    Text(
                        "¡Expedición completa!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Has terminado todas las misiones. Sigue observando muestras en el muestrario o vuelve a las que se te resistieron.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // ---------------- Zonas del laboratorio ----------------
            TituloSeccion("Zonas del laboratorio")
            Spacer(Modifier.height(10.dp))

            val zonas = listOf(
                ZonaLab(
                    "Muestrario", "Observa cualquier muestra",
                    "${estado.totalMuestras} muestras",
                    ObjetoLaboratorio.MUESTRARIO, TurquesaAgua, Rutas.MUESTRARIO
                ),
                ZonaLab(
                    "Misiones", "El plan de la expedición",
                    "${(estado.avanceExpedicion * 100).toInt()} % completado",
                    ObjetoLaboratorio.MAPA, AzulMedio, Rutas.MISIONES
                ),
                ZonaLab(
                    "Cuaderno", "Tus fichas científicas",
                    "${estado.fichas} fichas",
                    ObjetoLaboratorio.CUADERNO, CoralAviso, Rutas.CUADERNO
                ),
                ZonaLab(
                    "Colección", "Descubrimientos",
                    "${estado.descubrimientos} de ${estado.totalMuestras}",
                    ObjetoLaboratorio.PROBETA, VerdeNatural, Rutas.COLECCION
                ),
                ZonaLab(
                    "Insignias", "Logros científicos",
                    "${estado.insignias} de ${estado.totalInsignias}",
                    ObjetoLaboratorio.MICROSCOPIO, AmarilloDescubrimiento, Rutas.INSIGNIAS
                ),
                ZonaLab(
                    "Vuelve a observar", "Lo que se te escapó",
                    if (estado.pendientesDeRepaso > 0) "${estado.pendientesDeRepaso} pendientes" else "Todo revisado",
                    ObjetoLaboratorio.LUPA, AzulProfundo, Rutas.REPASO
                )
            )

            zonas.chunked(2).forEach { fila ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fila.forEach { zona ->
                        TarjetaZona(zona, Modifier.weight(1f)) { onIrA(zona.ruta) }
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(10.dp))

            // ---------------- Avance de la expedición ----------------
            TarjetaCampo(
                Modifier.fillMaxWidth(),
                onClick = { onIrA(Rutas.PROGRESO) }
            ) {
                Text(
                    "Avance de la expedición",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                BarraXp(
                    fraccion = estado.avanceExpedicion,
                    color = VerdeNatural,
                    alto = 16.dp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${estado.misiones.count { it.estado == com.ojocientifico.app.domain.model.EstadoMision.COMPLETADA }} de ${estado.misiones.size} misiones superadas · toca para ver tu progreso",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

private data class ZonaLab(
    val titulo: String,
    val descripcion: String,
    val dato: String,
    val objeto: ObjetoLaboratorio,
    val acento: Color,
    val ruta: String
)

@Composable
private fun TarjetaZona(zona: ZonaLab, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TarjetaCampo(
        modifier = modifier.semantics {
            contentDescription = "${zona.titulo}. ${zona.descripcion}. ${zona.dato}"
        },
        colorBorde = zona.acento,
        relleno = 12.dp,
        onClick = onClick
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clip(MaterialTheme.shapes.small)
                .background(zona.acento.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconoLaboratorio(zona.objeto, zona.titulo, Modifier.fillMaxSize().padding(10.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            zona.titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            zona.dato,
            style = MaterialTheme.typography.labelMedium,
            color = zona.acento
        )
    }
}

@Composable
private fun EtiquetaTipo(tipo: TipoMision) {
    val color = when (tipo) {
        TipoMision.OBSERVACION -> TurquesaAgua
        TipoMision.COMPARACION -> AzulMedio
        TipoMision.CLASIFICACION -> VerdeNatural
        TipoMision.PATRON -> CoralAviso
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .border(1.5.dp, color, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(tipo.etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
    }
}
