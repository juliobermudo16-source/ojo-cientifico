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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.FichaCientifica
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.ChipCaracteristica
import com.ojocientifico.app.ui.componentes.contar
import com.ojocientifico.app.ui.componentes.EstadoChip
import com.ojocientifico.app.ui.componentes.EstadoVacio
import com.ojocientifico.app.ui.componentes.Estrellas
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.PanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Cuaderno científico: todas las fichas guardadas, con lo que el niño marcó,
 * su nota de campo y el resultado. Se pueden volver a leer siempre.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaCuaderno(panel: PanelViewModel, onVolver: () -> Unit) {
    val fichas by panel.fichas.collectAsStateWithLifecycle()
    val muestras by panel.muestras.collectAsStateWithLifecycle()
    var abierta by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        CabeceraExpedicion(
            "Cuaderno científico",
            if (fichas.isEmpty()) "Todavía sin fichas"
            else contar(fichas.size, "ficha registrada", "fichas registradas"),
            onVolver
        )

        if (fichas.isEmpty()) {
            EstadoVacio(
                titulo = "Tu cuaderno está en blanco",
                mensaje = "Cada vez que observes una muestra y guardes el registro, aparecerá aquí una ficha con lo que anotaste."
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(fichas, key = { it.id }) { ficha ->
                val muestra = muestras.firstOrNull { it.id == ficha.muestraId }
                TarjetaFicha(
                    ficha = ficha,
                    nombre = muestra?.nombre ?: ficha.muestraId,
                    nombreCientifico = muestra?.nombreCientifico.orEmpty(),
                    ilustracion = muestra?.ilustracion,
                    expandida = abierta == ficha.id,
                    onClick = { abierta = if (abierta == ficha.id) null else ficha.id }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaFicha(
    ficha: FichaCientifica,
    nombre: String,
    nombreCientifico: String,
    ilustracion: com.ojocientifico.app.domain.model.ClaveIlustracion?,
    expandida: Boolean,
    onClick: () -> Unit
) {
    val acento = when {
        ficha.completa -> VerdeNatural
        ficha.precision >= 0.5f -> AmarilloDescubrimiento
        else -> CoralAviso
    }
    TarjetaCampo(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription =
                    "Ficha de $nombre. ${ficha.aciertos} de ${ficha.totalEsperado} características. ${ficha.estrellas} estrellas."
            },
        colorBorde = acento,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(acento.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                if (ilustracion != null) {
                    IlustracionMuestra(ilustracion, nombre, Modifier.fillMaxSize().padding(6.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(nombre, style = MaterialTheme.typography.titleMedium)
                if (nombreCientifico.isNotBlank()) {
                    Text(
                        nombreCientifico,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    fechaLegible(ficha.fechaMillis),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Estrellas(ficha.estrellas, tamano = 18.dp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${ficha.aciertos}/${ficha.totalEsperado}",
                    style = MaterialTheme.typography.titleMedium,
                    color = acento
                )
            }
        }

        if (expandida) {
            Spacer(Modifier.height(14.dp))
            Text(
                "Características que registraste",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (ficha.rasgosRegistrados.isEmpty()) {
                Text(
                    "No marcaste ninguna característica en esta ficha.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ficha.rasgosRegistrados.sortedBy { CatalogoRasgos.etiqueta(it) }.forEach { id ->
                        ChipCaracteristica(
                            etiqueta = CatalogoRasgos.etiqueta(id),
                            estado = EstadoChip.NEUTRO
                        )
                    }
                }
            }
            if (ficha.marcasDeMas > 0) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Marcaste ${ficha.marcasDeMas} característica${if (ficha.marcasDeMas == 1) "" else "s"} que esta muestra no tiene.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoralAviso
                )
            }
            if (ficha.notaDelExplorador.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tu nota de campo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        "«${ficha.notaDelExplorador}»",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Spacer(Modifier.height(8.dp))
            Text(
                "Toca para ver el detalle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun fechaLegible(millis: Long): String =
    SimpleDateFormat("d 'de' MMMM, HH:mm", Locale("es", "ES")).format(Date(millis))
