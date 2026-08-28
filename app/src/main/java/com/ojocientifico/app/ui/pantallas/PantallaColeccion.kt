package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.ui.componentes.BarraXp
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/**
 * Colección de descubrimientos: una tarjeta por muestra, que solo se revela
 * cuando el niño la ha observado con precisión suficiente.
 */
@Composable
fun PantallaColeccion(
    panel: PanelViewModel,
    onVolver: () -> Unit,
    onExplorar: (String) -> Unit
) {
    val muestras by panel.muestras.collectAsStateWithLifecycle()
    val descubiertos by panel.descubiertos.collectAsStateWithLifecycle()
    var abierta by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Colección de descubrimientos",
            "${descubiertos.size} de ${muestras.size} tarjetas reveladas",
            onVolver
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = AmarilloDescubrimiento) {
                    Text(
                        "Cómo se consigue una tarjeta",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Observa una muestra y acierta al menos tres de cada cuatro características. Entonces se revela su ficha completa.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    BarraXp(
                        fraccion = if (muestras.isEmpty()) 0f
                        else descubiertos.size.toFloat() / muestras.size,
                        color = VerdeNatural
                    )
                }
            }

            items(muestras, key = { it.id }) { muestra ->
                TarjetaDescubrimiento(
                    muestra = muestra,
                    revelada = muestra.id in descubiertos,
                    abierta = abierta == muestra.id,
                    onClick = {
                        if (muestra.id in descubiertos) {
                            abierta = if (abierta == muestra.id) null else muestra.id
                        } else {
                            onExplorar(muestra.id)
                        }
                    }
                )
            }
            item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun TarjetaDescubrimiento(
    muestra: Muestra,
    revelada: Boolean,
    abierta: Boolean,
    onClick: () -> Unit
) {
    val acento = if (revelada) VerdeNatural else MaterialTheme.colorScheme.outline
    TarjetaCampo(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (revelada) {
                    "Descubrimiento: ${muestra.nombre}. ${muestra.datoCurioso}"
                } else {
                    "Tarjeta sin revelar. Observa ${muestra.nombre} para conseguirla."
                }
            },
        colorBorde = acento,
        relleno = 10.dp,
        onClick = onClick
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small)
                .background(
                    if (revelada) acento.copy(alpha = 0.10f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (revelada) {
                IlustracionMuestra(
                    muestra.ilustracion,
                    muestra.nombre,
                    Modifier.fillMaxSize().padding(8.dp)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "?",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Sin revelar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (revelada) muestra.nombre else "Muestra ${muestra.nivelRequerido}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2
        )
        if (revelada) {
            Text(
                muestra.nombreCientifico,
                style = MaterialTheme.typography.labelMedium,
                color = TurquesaAgua,
                maxLines = 1
            )
            if (abierta) {
                Spacer(Modifier.height(10.dp))
                Text(
                    muestra.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(AmarilloDescubrimiento.copy(alpha = 0.18f))
                        .padding(10.dp)
                ) {
                    Text(
                        "¿Sabías que…? ${muestra.datoCurioso}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row {
                    Text(
                        "${muestra.reino.etiqueta} · ${muestra.habitat.etiqueta}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Toca para leer la ficha",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(Modifier.height(6.dp))
            Text(
                "Toca para ir a observarla",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
        }
    }
}
