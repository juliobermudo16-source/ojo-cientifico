package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.ChipCaracteristica
import com.ojocientifico.app.ui.componentes.contar
import com.ojocientifico.app.ui.componentes.EstadoChip
import com.ojocientifico.app.ui.componentes.EstadoVacio
import com.ojocientifico.app.ui.componentes.GloboDeIris
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/**
 * "Vuelve a observar": propone repasar las muestras cuyas características se
 * escaparon, a partir de lo que realmente quedó registrado.
 * No juzga al niño ni etiqueta su capacidad: solo señala dónde volver a mirar.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaRepaso(
    panel: PanelViewModel,
    onVolver: () -> Unit,
    onRepasar: (String) -> Unit
) {
    val sugerencias by panel.repaso.collectAsStateWithLifecycle()
    val muestras by panel.muestras.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Vuelve a observar",
            if (sugerencias.isEmpty()) "Nada pendiente"
            else contar(sugerencias.size, "muestra que merece otra mirada", "muestras que merecen otra mirada"),
            onVolver
        )

        if (sugerencias.isEmpty()) {
            EstadoVacio(
                titulo = "No se te ha escapado nada",
                mensaje = "Cuando alguna característica pase desapercibida en una observación, aparecerá aquí para que vuelvas a mirarla con calma."
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                GloboDeIris(
                    "Ningún científico lo ve todo a la primera. Volver a mirar es parte del método.",
                    gesto = GestoIris.ANIMANDO
                )
            }

            items(sugerencias, key = { it.muestraId }) { sugerencia ->
                val muestra = muestras.firstOrNull { it.id == sugerencia.muestraId }
                TarjetaCampo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "${muestra?.nombre ?: sugerencia.muestraId}. ${sugerencia.motivo}"
                        },
                    colorBorde = AmarilloDescubrimiento
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(66.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(AmarilloDescubrimiento.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (muestra != null) {
                                IlustracionMuestra(
                                    muestra.ilustracion, muestra.nombre,
                                    Modifier.fillMaxSize().padding(6.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                muestra?.nombre ?: sugerencia.muestraId,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                sugerencia.motivo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Fíjate esta vez en:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Se nombra la categoría, no la respuesta: sigue siendo un reto.
                        sugerencia.categorias.forEach { categoria ->
                            ChipCaracteristica(
                                etiqueta = categoria.etiqueta,
                                estado = EstadoChip.OLVIDADO
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BotonExpedicion(
                        texto = "Observar otra vez",
                        onClick = { onRepasar(sugerencia.muestraId) },
                        icono = Icons.Filled.Search,
                        modifier = Modifier.fillMaxWidth(),
                        color = AzulMedio
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
