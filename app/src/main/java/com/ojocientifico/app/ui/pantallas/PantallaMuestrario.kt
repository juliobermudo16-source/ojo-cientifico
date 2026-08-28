package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.GloboDeIris
import com.ojocientifico.app.ui.componentes.SelloBloqueado
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/**
 * Muestrario del laboratorio: todas las muestras disponibles para observar
 * libremente, fuera de las misiones. Las de rango superior se ven pero
 * indican qué falta para abrirlas.
 */
@Composable
fun PantallaMuestrario(
    panel: PanelViewModel,
    onVolver: () -> Unit,
    onMuestra: (String) -> Unit
) {
    val muestras by panel.muestras.collectAsStateWithLifecycle()
    val estado by panel.laboratorio.collectAsStateWithLifecycle()
    val descubiertos by panel.descubiertos.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Muestrario",
            "${muestras.count { it.nivelRequerido <= estado.rango.nivel }} de ${muestras.size} muestras disponibles",
            onVolver
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column {
                    GloboDeIris(
                        "Aquí puedes observar cualquier muestra las veces que quieras. Cada ficha que guardes cuenta.",
                        gesto = GestoIris.ANIMANDO
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            items(muestras, key = { it.id }) { muestra ->
                TarjetaMuestra(
                    muestra = muestra,
                    disponible = muestra.nivelRequerido <= estado.rango.nivel,
                    descubierta = muestra.id in descubiertos,
                    onClick = { onMuestra(muestra.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaMuestra(
    muestra: Muestra,
    disponible: Boolean,
    descubierta: Boolean,
    onClick: () -> Unit
) {
    val acento = when {
        !disponible -> MaterialTheme.colorScheme.outline
        descubierta -> VerdeNatural
        else -> TurquesaAgua
    }
    TarjetaCampo(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append(muestra.nombre)
                    append(". ").append(muestra.reino.etiqueta)
                    if (!disponible) append(". Bloqueada hasta el rango ${muestra.nivelRequerido}")
                    if (descubierta) append(". Ya está en tu colección")
                }
            },
        colorBorde = acento,
        relleno = 10.dp,
        onClick = if (disponible) onClick else null
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small)
                .background(
                    if (disponible) acento.copy(alpha = 0.10f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (disponible) {
                IlustracionMuestra(
                    muestra.ilustracion,
                    "${muestra.nombre}. ${muestra.descripcion}",
                    Modifier.fillMaxSize().padding(8.dp)
                )
            } else {
                Text("?", style = MaterialTheme.typography.displaySmall, color = acento)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            muestra.nombre,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2
        )
        Text(
            muestra.reino.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = acento
        )
        if (!disponible) {
            Spacer(Modifier.height(6.dp))
            SelloBloqueado("Rango ${muestra.nivelRequerido}")
        } else if (descubierta) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "★ En tu colección",
                    style = MaterialTheme.typography.labelMedium,
                    color = AmarilloDescubrimiento
                )
            }
        }
    }
}
