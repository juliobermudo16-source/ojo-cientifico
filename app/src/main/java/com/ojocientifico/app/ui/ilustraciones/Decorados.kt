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
import com.ojocientifico.app.ui.theme.AzulClaro
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.Tierra
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import kotlin.math.min

/** Instrumentos y objetos del laboratorio, dibujados con Canvas. */
enum class ObjetoLaboratorio { LUPA, CUADERNO, MICROSCOPIO, MUESTRARIO, MAPA, PROBETA }

@Composable
fun IconoLaboratorio(
    objeto: ObjetoLaboratorio,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.semantics { contentDescription = descripcion }) {
        Canvas(modifier = Modifier.matchParentSize()) { dibujarObjeto(objeto) }
    }
}

fun DrawScope.dibujarObjeto(objeto: ObjetoLaboratorio) {
    val lado = min(size.width, size.height)
    val ox = (size.width - lado) / 2f
    val oy = (size.height - lado) / 2f
    fun p(x: Float, y: Float) = Offset(ox + x * lado, oy + y * lado)
    fun s(v: Float) = v * lado

    when (objeto) {
        ObjetoLaboratorio.LUPA -> {
            // Cristal claro: un amarillo translucido se ensuciaba sobre el fondo azul.
            drawCircle(Color(0xFFE6F2F8), s(0.30f), p(0.42f, 0.40f))
            drawCircle(AmarilloDescubrimiento.copy(alpha = 0.22f), s(0.30f), p(0.42f, 0.40f))
            drawCircle(AzulProfundo, s(0.30f), p(0.42f, 0.40f), style = Stroke(width = s(0.075f)))
            drawLine(Tierra, p(0.63f, 0.61f), p(0.86f, 0.86f), strokeWidth = s(0.11f), cap = StrokeCap.Round)
            drawLine(AmbarCalido, p(0.63f, 0.61f), p(0.86f, 0.86f), strokeWidth = s(0.045f), cap = StrokeCap.Round)
            drawArc(
                color = Color.White.copy(alpha = 0.8f),
                startAngle = 170f, sweepAngle = 70f, useCenter = false,
                topLeft = p(0.20f, 0.18f), size = Size(s(0.44f), s(0.44f)),
                style = Stroke(width = s(0.045f), cap = StrokeCap.Round)
            )
        }

        ObjetoLaboratorio.CUADERNO -> {
            drawRoundRect(
                color = CoralAviso,
                topLeft = p(0.12f, 0.14f), size = Size(s(0.18f), s(0.72f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.05f))
            )
            drawRoundRect(
                color = Color(0xFFFDF8EC),
                topLeft = p(0.24f, 0.14f), size = Size(s(0.64f), s(0.72f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.05f))
            )
            drawRoundRect(
                color = AzulProfundo,
                topLeft = p(0.24f, 0.14f), size = Size(s(0.64f), s(0.72f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.05f)),
                style = Stroke(width = s(0.040f))
            )
            for (i in 0..4) {
                drawLine(
                    AzulMedio.copy(alpha = 0.55f),
                    p(0.32f, 0.28f + i * 0.12f), p(0.80f, 0.28f + i * 0.12f),
                    strokeWidth = s(0.028f), cap = StrokeCap.Round
                )
            }
            drawCircle(VerdeNatural, s(0.055f), p(0.36f, 0.28f))
        }

        ObjetoLaboratorio.MICROSCOPIO -> {
            // Base y brazo
            drawRoundRect(
                color = AzulProfundo,
                topLeft = p(0.16f, 0.80f), size = Size(s(0.68f), s(0.14f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.06f))
            )
            drawLine(AzulMedio, p(0.62f, 0.80f), p(0.62f, 0.30f), strokeWidth = s(0.10f), cap = StrokeCap.Round)
            // Platina
            drawRect(AzulProfundo, topLeft = p(0.24f, 0.58f), size = Size(s(0.44f), s(0.06f)))
            drawCircle(TurquesaAgua.copy(alpha = 0.6f), s(0.055f), p(0.40f, 0.61f))
            // Tubo óptico inclinado
            val tubo = Path().apply {
                moveTo(p(0.34f, 0.24f).x, p(0.34f, 0.24f).y)
                lineTo(p(0.62f, 0.14f).x, p(0.62f, 0.14f).y)
                lineTo(p(0.70f, 0.30f).x, p(0.70f, 0.30f).y)
                lineTo(p(0.42f, 0.40f).x, p(0.42f, 0.40f).y)
                close()
            }
            drawPath(tubo, AzulMedio)
            drawPath(tubo, AzulProfundo, style = Stroke(width = s(0.035f)))
            drawLine(AzulProfundo, p(0.38f, 0.42f), p(0.38f, 0.54f), strokeWidth = s(0.07f), cap = StrokeCap.Round)
            drawCircle(AmarilloDescubrimiento, s(0.05f), p(0.66f, 0.20f))
        }

        ObjetoLaboratorio.MUESTRARIO -> {
            // Bandeja de muestras: cuatro celdillas con contenido distinto
            drawRoundRect(
                color = Color(0xFFEDE3CE),
                topLeft = p(0.08f, 0.16f), size = Size(s(0.84f), s(0.68f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.06f))
            )
            drawRoundRect(
                color = Tierra,
                topLeft = p(0.08f, 0.16f), size = Size(s(0.84f), s(0.68f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.06f)),
                style = Stroke(width = s(0.040f))
            )
            val celdas = listOf(
                Triple(0.16f, 0.24f, VerdeNatural),
                Triple(0.54f, 0.24f, AmbarCalido),
                Triple(0.16f, 0.54f, TurquesaAgua),
                Triple(0.54f, 0.54f, CoralAviso)
            )
            celdas.forEach { (x, y, color) ->
                drawRoundRect(
                    color = Color.White,
                    topLeft = p(x, y), size = Size(s(0.30f), s(0.22f)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(s(0.03f))
                )
                drawCircle(color, s(0.070f), p(x + 0.15f, y + 0.11f))
            }
        }

        ObjetoLaboratorio.MAPA -> {
            val mapa = Path().apply {
                moveTo(p(0.08f, 0.24f).x, p(0.08f, 0.24f).y)
                lineTo(p(0.36f, 0.14f).x, p(0.36f, 0.14f).y)
                lineTo(p(0.64f, 0.26f).x, p(0.64f, 0.26f).y)
                lineTo(p(0.92f, 0.16f).x, p(0.92f, 0.16f).y)
                lineTo(p(0.92f, 0.78f).x, p(0.92f, 0.78f).y)
                lineTo(p(0.64f, 0.88f).x, p(0.64f, 0.88f).y)
                lineTo(p(0.36f, 0.76f).x, p(0.36f, 0.76f).y)
                lineTo(p(0.08f, 0.86f).x, p(0.08f, 0.86f).y)
                close()
            }
            drawPath(mapa, Color(0xFFF3E9D2))
            drawPath(mapa, Tierra, style = Stroke(width = s(0.035f)))
            drawLine(Tierra.copy(alpha = 0.5f), p(0.36f, 0.14f), p(0.36f, 0.76f), strokeWidth = s(0.020f))
            drawLine(Tierra.copy(alpha = 0.5f), p(0.64f, 0.26f), p(0.64f, 0.88f), strokeWidth = s(0.020f))
            // Ruta y marca
            val ruta = Path().apply {
                moveTo(p(0.20f, 0.66f).x, p(0.20f, 0.66f).y)
                quadraticBezierTo(p(0.48f, 0.34f).x, p(0.48f, 0.34f).y, p(0.76f, 0.56f).x, p(0.76f, 0.56f).y)
            }
            drawPath(
                ruta, CoralAviso,
                style = Stroke(
                    width = s(0.030f),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(s(0.06f), s(0.05f))
                    )
                )
            )
            drawCircle(AzulProfundo, s(0.045f), p(0.76f, 0.56f))
            drawCircle(AmarilloDescubrimiento, s(0.020f), p(0.76f, 0.56f))
        }

        ObjetoLaboratorio.PROBETA -> {
            val vidrio = Path().apply {
                moveTo(p(0.38f, 0.10f).x, p(0.38f, 0.10f).y)
                lineTo(p(0.38f, 0.48f).x, p(0.38f, 0.48f).y)
                lineTo(p(0.16f, 0.84f).x, p(0.16f, 0.84f).y)
                quadraticBezierTo(p(0.50f, 0.98f).x, p(0.50f, 0.98f).y, p(0.84f, 0.84f).x, p(0.84f, 0.84f).y)
                lineTo(p(0.62f, 0.48f).x, p(0.62f, 0.48f).y)
                lineTo(p(0.62f, 0.10f).x, p(0.62f, 0.10f).y)
                close()
            }
            drawPath(vidrio, AzulClaro.copy(alpha = 0.18f))
            drawPath(vidrio, AzulProfundo, style = Stroke(width = s(0.040f)))
            val liquido = Path().apply {
                moveTo(p(0.26f, 0.66f).x, p(0.26f, 0.66f).y)
                lineTo(p(0.74f, 0.66f).x, p(0.74f, 0.66f).y)
                lineTo(p(0.84f, 0.84f).x, p(0.84f, 0.84f).y)
                quadraticBezierTo(p(0.50f, 0.98f).x, p(0.50f, 0.98f).y, p(0.16f, 0.84f).x, p(0.16f, 0.84f).y)
                close()
            }
            drawPath(liquido, VerdeNatural.copy(alpha = 0.75f))
            drawCircle(Color.White.copy(alpha = 0.5f), s(0.030f), p(0.40f, 0.76f))
            drawCircle(Color.White.copy(alpha = 0.4f), s(0.020f), p(0.58f, 0.72f))
            drawLine(AzulProfundo, p(0.34f, 0.10f), p(0.66f, 0.10f), strokeWidth = s(0.045f), cap = StrokeCap.Round)
        }
    }
}

/**
 * Fondo de cuaderno de campo: retícula tenue con manchas de tinta.
 * Da textura sin robar protagonismo al contenido.
 */
@Composable
fun FondoCuaderno(
    modifier: Modifier = Modifier,
    colorLinea: Color,
    intensidad: Float = 0.16f
) {
    Canvas(modifier = modifier) {
        val paso = size.minDimension / 11f
        var x = paso
        while (x < size.width) {
            drawLine(
                colorLinea.copy(alpha = intensidad),
                Offset(x, 0f), Offset(x, size.height),
                strokeWidth = 1.1f
            )
            x += paso
        }
        var y = paso
        while (y < size.height) {
            drawLine(
                colorLinea.copy(alpha = intensidad),
                Offset(0f, y), Offset(size.width, y),
                strokeWidth = 1.1f
            )
            y += paso
        }
    }
}
