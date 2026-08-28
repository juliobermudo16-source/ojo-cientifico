package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ojocientifico.app.domain.model.EstadoMision
import com.ojocientifico.app.domain.model.RangoExplorador
import com.ojocientifico.app.domain.model.TipoMision
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.Estrellas
import com.ojocientifico.app.ui.componentes.SelloBloqueado
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.componentes.TituloSeccion
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.MisionEnPanel
import com.ojocientifico.app.ui.viewmodel.PanelViewModel

/**
 * Plan de la expedición: las misiones agrupadas por rango, con su estado real
 * de desbloqueo y las estrellas conseguidas.
 */
@Composable
fun PantallaMisiones(
    panel: PanelViewModel,
    onVolver: () -> Unit,
    onMision: (String) -> Unit
) {
    val estado by panel.laboratorio.collectAsStateWithLifecycle()
    val muestras by panel.muestras.collectAsStateWithLifecycle()

    val completadas = estado.misiones.count { it.estado == EstadoMision.COMPLETADA }

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Misiones",
            "$completadas de ${estado.misiones.size} superadas",
            onVolver
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            RangoExplorador.entries.forEach { rango ->
                val delRango = estado.misiones.filter { it.mision.nivel == rango.nivel }
                if (delRango.isEmpty()) return@forEach

                item(key = "cab_${rango.nivel}") {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        TituloSeccion(
                            "Nivel ${rango.nivel} · ${rango.titulo}",
                            acento = colorDeRango(rango)
                        )
                        Text(
                            rango.lema,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 14.dp, top = 2.dp)
                        )
                        if (estado.rango.nivel < rango.nivel) {
                            Spacer(Modifier.height(6.dp))
                            SelloBloqueado(
                                "Se abre con ${rango.xpNecesario} XP · llevas ${estado.xp}",
                                Modifier.padding(start = 14.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }

                items(delRango, key = { it.mision.id }) { entrada ->
                    FilaMision(
                        entrada = entrada,
                        ilustracion = entrada.mision.muestrasIds.firstOrNull()?.let { id ->
                            muestras.firstOrNull { it.id == id }
                        },
                        onClick = { onMision(entrada.mision.id) }
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun FilaMision(
    entrada: MisionEnPanel,
    ilustracion: com.ojocientifico.app.domain.model.Muestra?,
    onClick: () -> Unit
) {
    val bloqueada = entrada.estado == EstadoMision.BLOQUEADA
    val completada = entrada.estado == EstadoMision.COMPLETADA
    val acento = when {
        bloqueada -> MaterialTheme.colorScheme.outline
        completada -> VerdeNatural
        else -> colorDeTipo(entrada.mision.tipo)
    }

    TarjetaCampo(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append(entrada.mision.titulo)
                    append(". ").append(entrada.mision.tipo.etiqueta)
                    append(
                        when (entrada.estado) {
                            EstadoMision.BLOQUEADA -> ". Bloqueada"
                            EstadoMision.DISPONIBLE -> ". Disponible"
                            EstadoMision.COMPLETADA -> ". Completada con ${entrada.estrellas} estrellas"
                        }
                    )
                }
            },
        colorBorde = acento,
        grosorBorde = if (entrada.estado == EstadoMision.DISPONIBLE) 3.dp else 2.dp,
        relleno = 12.dp,
        onClick = if (bloqueada) null else onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(68.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(acento.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                if (!bloqueada && ilustracion != null) {
                    IlustracionMuestra(
                        ilustracion.ilustracion, ilustracion.nombre,
                        Modifier.fillMaxSize().padding(6.dp)
                    )
                } else {
                    Text(
                        if (bloqueada) "🔒" else "?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = acento
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(acento.copy(alpha = 0.15f))
                        .border(1.dp, acento, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        entrada.mision.tipo.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = acento
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    entrada.mision.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    entrada.mision.consigna,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Estrellas(entrada.estrellas, tamano = 18.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${entrada.mision.xpBase} XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmarilloDescubrimiento
                    )
                }
            }
            if (completada) {
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(VerdeNatural),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    }
}

private fun colorDeTipo(tipo: TipoMision): Color = when (tipo) {
    TipoMision.OBSERVACION -> TurquesaAgua
    TipoMision.COMPARACION -> AzulMedio
    TipoMision.CLASIFICACION -> VerdeNatural
    TipoMision.PATRON -> CoralAviso
}

private fun colorDeRango(rango: RangoExplorador): Color = when (rango.nivel) {
    1 -> TurquesaAgua
    2 -> AzulMedio
    else -> CoralAviso
}
