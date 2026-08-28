package com.ojocientifico.app.ui.ilustraciones

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AmbarCalido
import com.ojocientifico.app.ui.theme.AzulCientifico
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.Grafito
import com.ojocientifico.app.ui.theme.Tierra
import com.ojocientifico.app.ui.theme.VerdeNatural
import kotlin.math.min

/** Gestos de Iris, la exploradora que acompaña al niño. */
enum class GestoIris { NEUTRO, ANIMANDO, PENSANDO, CELEBRANDO }

/**
 * Iris, guía científica de la expedición.
 *
 * Es una exploradora joven con gafas de aumento en la frente. Trata al niño de
 * igual a igual: no habla como a un bebé y no ocupa media pantalla.
 */
@Composable
fun Iris(
    modifier: Modifier = Modifier,
    gesto: GestoIris = GestoIris.NEUTRO
) {
    Box(
        modifier = modifier.semantics {
            contentDescription = "Iris, la exploradora que te guía"
        }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) { dibujarIris(gesto) }
    }
}

fun DrawScope.dibujarIris(gesto: GestoIris) {
    val lado = min(size.width, size.height)
    val ox = (size.width - lado) / 2f
    val oy = (size.height - lado) / 2f
    fun p(x: Float, y: Float) = Offset(ox + x * lado, oy + y * lado)
    fun s(v: Float) = v * lado

    val piel = Color(0xFFE8B98C)
    val pelo = Color(0xFF4A2E1E)
    val chaleco = VerdeNatural

    // Coleta
    drawCircle(pelo, s(0.10f), p(0.80f, 0.42f))
    drawLine(pelo, p(0.70f, 0.36f), p(0.82f, 0.44f), strokeWidth = s(0.09f), cap = StrokeCap.Round)

    // Cuerpo con chaleco de campo
    val cuerpo = Path().apply {
        moveTo(p(0.28f, 1.02f).x, p(0.28f, 1.02f).y)
        lineTo(p(0.33f, 0.66f).x, p(0.33f, 0.66f).y)
        quadraticBezierTo(p(0.5f, 0.58f).x, p(0.5f, 0.58f).y, p(0.67f, 0.66f).x, p(0.67f, 0.66f).y)
        lineTo(p(0.72f, 1.02f).x, p(0.72f, 1.02f).y)
        close()
    }
    drawPath(cuerpo, chaleco)
    // Bolsillos del chaleco
    drawRect(
        color = Color(0xFF1F5C42),
        topLeft = p(0.36f, 0.80f),
        size = Size(s(0.11f), s(0.11f))
    )
    drawRect(
        color = Color(0xFF1F5C42),
        topLeft = p(0.53f, 0.80f),
        size = Size(s(0.11f), s(0.11f))
    )
    // Pañuelo
    val panuelo = Path().apply {
        moveTo(p(0.36f, 0.66f).x, p(0.36f, 0.66f).y)
        lineTo(p(0.64f, 0.66f).x, p(0.64f, 0.66f).y)
        lineTo(p(0.50f, 0.80f).x, p(0.50f, 0.80f).y)
        close()
    }
    drawPath(panuelo, AmbarCalido)

    // Cabeza
    drawCircle(piel, s(0.26f), p(0.5f, 0.38f))
    // Flequillo
    val flequillo = Path().apply {
        moveTo(p(0.24f, 0.36f).x, p(0.24f, 0.36f).y)
        quadraticBezierTo(p(0.5f, 0.02f).x, p(0.5f, 0.02f).y, p(0.76f, 0.36f).x, p(0.76f, 0.36f).y)
        quadraticBezierTo(p(0.5f, 0.24f).x, p(0.5f, 0.24f).y, p(0.24f, 0.36f).x, p(0.24f, 0.36f).y)
        close()
    }
    drawPath(flequillo, pelo)

    // Gafas de aumento subidas a la frente: el instrumento de la exploradora
    drawLine(Grafito, p(0.24f, 0.28f), p(0.76f, 0.28f), strokeWidth = s(0.035f), cap = StrokeCap.Round)
    listOf(0.36f, 0.64f).forEach { x ->
        drawCircle(AzulMedio.copy(alpha = 0.55f), s(0.095f), p(x, 0.24f))
        drawCircle(Grafito, s(0.095f), p(x, 0.24f), style = Stroke(width = s(0.022f)))
        drawCircle(Color.White.copy(alpha = 0.7f), s(0.028f), p(x - 0.03f, 0.21f))
    }

    // Ojos y boca según el gesto
    when (gesto) {
        GestoIris.CELEBRANDO -> {
            listOf(0.40f, 0.60f).forEach { x ->
                drawArc(
                    color = Grafito, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = p(x - 0.055f, 0.36f), size = Size(s(0.11f), s(0.09f)),
                    style = Stroke(width = s(0.022f), cap = StrokeCap.Round)
                )
            }
            drawArc(
                color = Grafito, startAngle = 15f, sweepAngle = 150f, useCenter = false,
                topLeft = p(0.40f, 0.44f), size = Size(s(0.20f), s(0.14f)),
                style = Stroke(width = s(0.022f), cap = StrokeCap.Round)
            )
        }

        GestoIris.PENSANDO -> {
            drawCircle(Grafito, s(0.028f), p(0.42f, 0.42f))
            drawCircle(Grafito, s(0.028f), p(0.61f, 0.42f))
            drawLine(Grafito, p(0.44f, 0.53f), p(0.58f, 0.51f), strokeWidth = s(0.020f), cap = StrokeCap.Round)
            // Signo de interrogación flotante
            drawArc(
                color = AzulCientifico, startAngle = 160f, sweepAngle = 220f, useCenter = false,
                topLeft = p(0.80f, 0.04f), size = Size(s(0.14f), s(0.14f)),
                style = Stroke(width = s(0.026f), cap = StrokeCap.Round)
            )
            drawCircle(AzulCientifico, s(0.020f), p(0.87f, 0.26f))
        }

        else -> {
            listOf(0.40f, 0.60f).forEach { x ->
                drawCircle(Grafito, s(0.030f), p(x, 0.41f))
                drawCircle(Color.White, s(0.011f), p(x - 0.012f, 0.40f))
            }
            drawArc(
                color = Grafito, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                topLeft = p(0.42f, 0.45f), size = Size(s(0.16f), s(0.11f)),
                style = Stroke(width = s(0.020f), cap = StrokeCap.Round)
            )
        }
    }
    // Mejillas
    drawCircle(Color(0xFFD98F6C).copy(alpha = 0.5f), s(0.045f), p(0.31f, 0.46f))
    drawCircle(Color(0xFFD98F6C).copy(alpha = 0.5f), s(0.045f), p(0.69f, 0.46f))

    // Brazo con lupa cuando anima o celebra
    if (gesto == GestoIris.ANIMANDO || gesto == GestoIris.CELEBRANDO) {
        drawLine(piel, p(0.68f, 0.72f), p(0.90f, 0.56f), strokeWidth = s(0.055f), cap = StrokeCap.Round)
        drawCircle(AmarilloDescubrimiento.copy(alpha = 0.45f), s(0.10f), p(0.95f, 0.44f))
        drawCircle(Tierra, s(0.10f), p(0.95f, 0.44f), style = Stroke(width = s(0.028f)))
        drawLine(Tierra, p(0.90f, 0.52f), p(0.84f, 0.60f), strokeWidth = s(0.030f), cap = StrokeCap.Round)
    } else {
        drawLine(piel, p(0.68f, 0.72f), p(0.80f, 0.92f), strokeWidth = s(0.055f), cap = StrokeCap.Round)
    }
    drawLine(piel, p(0.32f, 0.72f), p(0.20f, 0.92f), strokeWidth = s(0.055f), cap = StrokeCap.Round)

    // Chapa de expedición
    drawCircle(AzulProfundo, s(0.045f), p(0.40f, 0.72f))
    drawCircle(AmarilloDescubrimiento, s(0.020f), p(0.40f, 0.72f))
}
