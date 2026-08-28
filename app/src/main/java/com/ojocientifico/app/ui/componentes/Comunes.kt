package com.ojocientifico.app.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.Iris
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.LocalAjustesVisuales
import com.ojocientifico.app.ui.theme.VerdeNatural

/** Altura mínima cómoda para dedos de 8 años. */
val AlturaTactilMinima = 56.dp

/**
 * Tarjeta con aire de ficha de cartulina: borde marcado y esquinas generosas.
 * Es la unidad visual básica de toda la aplicación.
 */
@Composable
fun TarjetaCampo(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    colorBorde: Color = MaterialTheme.colorScheme.outline,
    grosorBorde: androidx.compose.ui.unit.Dp = 2.dp,
    forma: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    relleno: androidx.compose.ui.unit.Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val base = modifier
        .clip(forma)
        .background(color)
        .border(grosorBorde, colorBorde, forma)
        .let { if (onClick != null) it.clickable { onClick() } else it }

    Column(modifier = base.padding(relleno), content = contenido)
}

/** Cabecera de pantalla con botón de volver y subtítulo opcional. */
@Composable
fun CabeceraExpedicion(
    titulo: String,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    accion: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onVolver != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onVolver() }
                    .semantics { contentDescription = "Volver" },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        accion?.invoke()
    }
}

/** Botón principal, grande y con mucho contraste. */
@Composable
fun BotonExpedicion(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    colorTexto: Color = MaterialTheme.colorScheme.onPrimary
) {
    val fondo = if (habilitado) color else MaterialTheme.colorScheme.surfaceVariant
    val tinta = if (habilitado) colorTexto else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier
            .heightIn(min = AlturaTactilMinima)
            .clip(MaterialTheme.shapes.large)
            .background(fondo)
            .clickable(enabled = habilitado) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = tinta, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = tinta,
            textAlign = TextAlign.Center
        )
    }
}

/** Botón secundario, con contorno en lugar de relleno. */
@Composable
fun BotonContorno(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    Row(
        modifier = modifier
            .heightIn(min = AlturaTactilMinima)
            .clip(MaterialTheme.shapes.large)
            .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(
                icono, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

/** Estrellas obtenidas en una actividad, de 0 a 3. */
@Composable
fun Estrellas(
    cantidad: Int,
    modifier: Modifier = Modifier,
    total: Int = 3,
    tamano: androidx.compose.ui.unit.Dp = 22.dp
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "$cantidad de $total estrellas"
        },
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(total) { indice ->
            val ganada = indice < cantidad
            Icon(
                imageVector = if (ganada) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (ganada) AmarilloDescubrimiento else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(tamano)
            )
        }
    }
}

/** Barra de experiencia con animación breve al cambiar. */
@Composable
fun BarraXp(
    fraccion: Float,
    modifier: Modifier = Modifier,
    color: Color = AmarilloDescubrimiento,
    alto: androidx.compose.ui.unit.Dp = 14.dp
) {
    val animaciones = LocalAjustesVisuales.current.animaciones
    val objetivo = fraccion.coerceIn(0f, 1f)
    val valor by animateFloatAsState(
        targetValue = objetivo,
        animationSpec = tween(if (animaciones) 700 else 0),
        label = "barraXp"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clearAndSetSemantics {
                contentDescription = "Progreso: ${(objetivo * 100).toInt()} por ciento"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(valor)
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

/** Estado visual de una característica marcable. */
enum class EstadoChip { NEUTRO, SELECCIONADO, ACIERTO, FALLO, OLVIDADO }

/**
 * Chip de característica morfológica.
 * El estado se comunica con forma, icono y texto, nunca solo con el color.
 */
@Composable
fun ChipCaracteristica(
    etiqueta: String,
    estado: EstadoChip,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val esquema = MaterialTheme.colorScheme
    val (fondo, borde, tinta) = when (estado) {
        EstadoChip.NEUTRO -> Triple(esquema.surface, esquema.outline, esquema.onSurface)
        EstadoChip.SELECCIONADO -> Triple(esquema.primaryContainer, esquema.primary, esquema.onPrimaryContainer)
        EstadoChip.ACIERTO -> Triple(esquema.secondaryContainer, VerdeNatural, esquema.onSecondaryContainer)
        EstadoChip.FALLO -> Triple(esquema.errorContainer, CoralAviso, esquema.onErrorContainer)
        EstadoChip.OLVIDADO -> Triple(esquema.tertiaryContainer, esquema.tertiary, esquema.onTertiaryContainer)
    }
    val sufijo = when (estado) {
        EstadoChip.ACIERTO -> ". Acertada"
        EstadoChip.FALLO -> ". No corresponde a esta muestra"
        EstadoChip.OLVIDADO -> ". Se te escapó"
        EstadoChip.SELECCIONADO -> ". Marcada"
        EstadoChip.NEUTRO -> ""
    }

    Row(
        modifier = modifier
            .heightIn(min = 52.dp)
            .clip(MaterialTheme.shapes.small)
            .background(fondo)
            .border(
                width = if (estado == EstadoChip.NEUTRO) 2.dp else 3.dp,
                color = borde,
                shape = MaterialTheme.shapes.small
            )
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .semantics { contentDescription = etiqueta + sufijo },
        verticalAlignment = Alignment.CenterVertically
    ) {
        MarcaEstado(estado, tinta)
        Spacer(Modifier.width(10.dp))
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (estado == EstadoChip.NEUTRO) FontWeight.Normal else FontWeight.Bold,
            color = tinta
        )
    }
}

@Composable
private fun MarcaEstado(estado: EstadoChip, tinta: Color) {
    val forma = if (estado == EstadoChip.OLVIDADO) RoundedCornerShape(4.dp) else CircleShape
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(forma)
            .background(
                if (estado == EstadoChip.NEUTRO) Color.Transparent else tinta.copy(alpha = 0.18f)
            )
            .border(2.dp, tinta.copy(alpha = 0.65f), forma),
        contentAlignment = Alignment.Center
    ) {
        when (estado) {
            EstadoChip.SELECCIONADO, EstadoChip.ACIERTO ->
                Icon(Icons.Filled.Check, null, tint = tinta, modifier = Modifier.size(15.dp))

            EstadoChip.FALLO ->
                Text("×", style = MaterialTheme.typography.titleMedium, color = tinta)

            EstadoChip.OLVIDADO ->
                Text("!", style = MaterialTheme.typography.labelMedium, color = tinta)

            EstadoChip.NEUTRO -> Unit
        }
    }
}

/** Bocadillo de Iris. Frases cortas: nunca párrafos. */
@Composable
fun GloboDeIris(
    texto: String,
    modifier: Modifier = Modifier,
    gesto: GestoIris = GestoIris.NEUTRO,
    tamanoIris: androidx.compose.ui.unit.Dp = 72.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Iris(gesto = gesto, modifier = Modifier.size(tamanoIris))
        Spacer(Modifier.width(8.dp))
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = texto,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/** Sello de contenido bloqueado. */
@Composable
fun SelloBloqueado(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Lock, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Título de sección con una línea de acento. */
@Composable
fun TituloSeccion(
    texto: String,
    modifier: Modifier = Modifier,
    acento: Color = MaterialTheme.colorScheme.primary
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 6.dp, height = 22.dp)
                .clip(RoundedCornerShape(50))
                .background(acento)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            texto,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/** Mensaje para listas vacías: siempre propone algo que hacer. */
@Composable
fun EstadoVacio(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Iris(gesto = GestoIris.PENSANDO, modifier = Modifier.sizeIn(minWidth = 96.dp, minHeight = 96.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (accion != null) {
            Spacer(Modifier.height(16.dp))
            accion()
        }
    }
}

/** Aparición suave, respetando la preferencia de animaciones. */
@Composable
fun AparicionSuave(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    val animaciones = LocalAjustesVisuales.current.animaciones
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animaciones) fadeIn(tween(300)) + expandVertically(tween(300)) else fadeIn(tween(0)),
        exit = if (animaciones) fadeOut(tween(200)) + shrinkVertically(tween(200)) else fadeOut(tween(0))
    ) {
        contenido()
    }
}
