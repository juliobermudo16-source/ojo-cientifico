package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.ojocientifico.app.ui.componentes.BarraXp
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.InsigniaIlustrada
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/**
 * Muro de insignias. Las bloqueadas también se ven, con su barra de avance
 * real: saber qué falta es parte del incentivo, no un secreto.
 */
@Composable
fun PantallaInsignias(panel: PanelViewModel, onVolver: () -> Unit) {
    val insignias by panel.insignias.collectAsStateWithLifecycle()
    val conseguidas = insignias.count { it.desbloqueada }

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Insignias científicas",
            "$conseguidas de ${insignias.size} conseguidas",
            onVolver
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(insignias, key = { it.insignia.id }) { entrada ->
                val acento = if (entrada.desbloqueada) VerdeNatural
                else MaterialTheme.colorScheme.outline

                TarjetaCampo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = buildString {
                                append(entrada.insignia.nombre)
                                append(if (entrada.desbloqueada) ", conseguida. " else ", pendiente. ")
                                append(entrada.insignia.comoSeGana)
                                if (!entrada.desbloqueada) {
                                    append(" Llevas ${entrada.progreso} de ${entrada.meta}.")
                                }
                            }
                        },
                    colorBorde = acento,
                    grosorBorde = if (entrada.desbloqueada) 3.dp else 2.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(78.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    if (entrada.desbloqueada) AmarilloDescubrimiento.copy(alpha = 0.14f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            InsigniaIlustrada(
                                clave = entrada.insignia.clave,
                                desbloqueada = entrada.desbloqueada,
                                nombre = entrada.insignia.nombre,
                                modifier = Modifier.fillMaxSize().padding(6.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                entrada.insignia.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                if (entrada.desbloqueada) entrada.insignia.descripcion
                                else entrada.insignia.comoSeGana,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            if (entrada.desbloqueada) {
                                Text(
                                    "✓ Conseguida",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VerdeNatural
                                )
                            } else {
                                BarraXp(fraccion = entrada.fraccion, alto = 10.dp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${entrada.progreso} de ${entrada.meta}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
