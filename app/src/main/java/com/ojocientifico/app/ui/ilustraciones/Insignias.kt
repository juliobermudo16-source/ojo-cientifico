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
import com.ojocientifico.app.domain.model.ClaveInsignia
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AmbarCalido
import com.ojocientifico.app.ui.theme.AzulClaro
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.Tierra
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Insignias científicas dibujadas a mano con Canvas.
 * Cuando están bloqueadas se muestran en gris: se ve QUÉ falta por conseguir,
 * que es parte del incentivo.
 */
@Composable
fun InsigniaIlustrada(
    clave: ClaveInsignia,
    desbloqueada: Boolean,
    nombre: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.semantics {
            contentDescription =
                if (desbloqueada) "Insignia conseguida: $nombre" else "Insignia por conseguir: $nombre"
        }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            dibujarInsignia(clave, desbloqueada)
        }
    }
}

fun DrawScope.dibujarInsignia(clave: ClaveInsignia, desbloqueada: Boolean) {
    val lado = min(size.width, size.height)
    val ox = (size.width - lado) / 2f
    val oy = (size.height - lado) / 2f
    fun p(x: Float, y: Float) = Offset(ox + x * lado, oy + y * lado)
    fun s(v: Float) = v * lado
    val c = p(0.5f, 0.5f)

    fun tono(color: Color): Color =
        if (desbloqueada) color else Color(0xFF9AA3A9).copy(alpha = 0.55f)

    // Medallón base: doce muescas, como una rueda de instrumento.
    val fondo = if (desbloqueada) Color(0xFFFBF3E2) else Color(0xFFE7E7E7)
    drawCircle(fondo, s(0.46f), c)
    drawCircle(tono(AmbarCalido), s(0.46f), c, style = Stroke(width = s(0.045f)))
    for (i in 0 until 12) {
        val ang = Math.toRadians((i * 30.0))
        val r1 = s(0.46f)
        val r2 = s(0.40f)
        drawLine(
            tono(AmbarCalido).copy(alpha = 0.5f),
            Offset(c.x + r1 * cos(ang).toFloat(), c.y + r1 * sin(ang).toFloat()),
            Offset(c.x + r2 * cos(ang).toFloat(), c.y + r2 * sin(ang).toFloat()),
            strokeWidth = s(0.020f)
        )
    }

    when (clave) {
        // Lupa: explorador atento
        ClaveInsignia.LUPA -> {
            drawCircle(tono(AzulClaro).copy(alpha = 0.35f), s(0.19f), p(0.44f, 0.42f))
            drawCircle(tono(AzulProfundo), s(0.19f), p(0.44f, 0.42f), style = Stroke(width = s(0.05f)))
            drawLine(
                tono(Tierra), p(0.57f, 0.56f), p(0.73f, 0.73f),
                strokeWidth = s(0.075f), cap = StrokeCap.Round
            )
        }

        // Ojo: detective de detalles
        ClaveInsignia.OJO -> {
            val ojo = Path().apply {
                moveTo(p(0.22f, 0.5f).x, p(0.22f, 0.5f).y)
                quadraticBezierTo(p(0.5f, 0.24f).x, p(0.5f, 0.24f).y, p(0.78f, 0.5f).x, p(0.78f, 0.5f).y)
                quadraticBezierTo(p(0.5f, 0.76f).x, p(0.5f, 0.76f).y, p(0.22f, 0.5f).x, p(0.22f, 0.5f).y)
                close()
            }
            drawPath(ojo, tono(Color.White))
            drawPath(ojo, tono(AzulProfundo), style = Stroke(width = s(0.045f)))
            drawCircle(tono(VerdeNatural), s(0.115f), c)
            drawCircle(tono(AzulProfundo), s(0.055f), c)
            drawCircle(tono(Color.White), s(0.022f), p(0.46f, 0.46f))
        }

        // Tres figuras: maestro de las formas
        ClaveInsignia.FORMAS -> {
            drawCircle(tono(TurquesaAgua), s(0.115f), p(0.34f, 0.40f))
            val triangulo = Path().apply {
                moveTo(p(0.66f, 0.28f).x, p(0.66f, 0.28f).y)
                lineTo(p(0.79f, 0.52f).x, p(0.79f, 0.52f).y)
                lineTo(p(0.53f, 0.52f).x, p(0.53f, 0.52f).y)
                close()
            }
            drawPath(triangulo, tono(AmarilloDescubrimiento))
            drawRect(tono(CoralAviso), topLeft = p(0.38f, 0.58f), size = Size(s(0.24f), s(0.20f)))
        }

        // Capas: observador de estructuras
        ClaveInsignia.ESTRUCTURA -> {
            listOf(0.30f to TurquesaAgua, 0.45f to AzulMedio, 0.60f to VerdeNatural).forEach { (y, color) ->
                drawRect(tono(color), topLeft = p(0.24f, y), size = Size(s(0.52f), s(0.10f)))
            }
            for (i in 0..4) {
                drawLine(
                    tono(AzulProfundo).copy(alpha = 0.55f),
                    p(0.28f + i * 0.11f, 0.28f), p(0.28f + i * 0.11f, 0.72f),
                    strokeWidth = s(0.014f)
                )
            }
        }

        // Balanza: comparador experto
        ClaveInsignia.BALANZA -> {
            drawLine(tono(Tierra), p(0.5f, 0.26f), p(0.5f, 0.74f), strokeWidth = s(0.04f), cap = StrokeCap.Round)
            drawLine(tono(Tierra), p(0.22f, 0.36f), p(0.78f, 0.36f), strokeWidth = s(0.04f), cap = StrokeCap.Round)
            listOf(0.24f, 0.76f).forEach { x ->
                drawLine(tono(Tierra), p(x, 0.36f), p(x, 0.52f), strokeWidth = s(0.02f))
                drawArc(
                    color = tono(AzulMedio),
                    startAngle = 0f, sweepAngle = 180f, useCenter = true,
                    topLeft = p(x - 0.13f, 0.46f), size = Size(s(0.26f), s(0.18f))
                )
            }
            drawLine(tono(Tierra), p(0.36f, 0.76f), p(0.64f, 0.76f), strokeWidth = s(0.04f), cap = StrokeCap.Round)
        }

        // Huella: expedición constante
        ClaveInsignia.HUELLA -> {
            drawOval(
                tono(Tierra),
                topLeft = p(0.36f, 0.42f), size = Size(s(0.28f), s(0.36f))
            )
            listOf(0.30f to 0.32f, 0.44f to 0.26f, 0.58f to 0.26f, 0.70f to 0.34f).forEach { (x, y) ->
                drawCircle(tono(Tierra), s(0.055f), p(x, y))
            }
        }

        // Cuaderno: cuaderno de campo
        ClaveInsignia.CUADERNO -> {
            drawRect(tono(Color.White), topLeft = p(0.26f, 0.24f), size = Size(s(0.48f), s(0.54f)))
            drawRect(
                tono(AzulProfundo), topLeft = p(0.26f, 0.24f), size = Size(s(0.48f), s(0.54f)),
                style = Stroke(width = s(0.032f))
            )
            drawRect(tono(CoralAviso), topLeft = p(0.26f, 0.24f), size = Size(s(0.08f), s(0.54f)))
            for (i in 0..3) {
                drawLine(
                    tono(AzulMedio).copy(alpha = 0.7f),
                    p(0.40f, 0.36f + i * 0.10f), p(0.68f, 0.36f + i * 0.10f),
                    strokeWidth = s(0.022f), cap = StrokeCap.Round
                )
            }
        }

        // Medalla: gran descubridor
        ClaveInsignia.MEDALLA -> {
            drawLine(tono(CoralAviso), p(0.36f, 0.14f), p(0.5f, 0.42f), strokeWidth = s(0.08f))
            drawLine(tono(AzulMedio), p(0.64f, 0.14f), p(0.5f, 0.42f), strokeWidth = s(0.08f))
            drawCircle(tono(AmarilloDescubrimiento), s(0.22f), p(0.5f, 0.58f))
            drawCircle(tono(AmbarCalido), s(0.22f), p(0.5f, 0.58f), style = Stroke(width = s(0.035f)))
            // Estrella central
            val estrella = Path()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) s(0.13f) else s(0.055f)
                val ang = Math.toRadians((i * 36 - 90).toDouble())
                val punto = Offset(
                    p(0.5f, 0.58f).x + r * cos(ang).toFloat(),
                    p(0.5f, 0.58f).y + r * sin(ang).toFloat()
                )
                if (i == 0) estrella.moveTo(punto.x, punto.y) else estrella.lineTo(punto.x, punto.y)
            }
            estrella.close()
            drawPath(estrella, tono(AzulProfundo))
        }

        // Brújula: clasificador certero
        ClaveInsignia.BRUJULA -> {
            drawCircle(tono(Color.White), s(0.28f), c)
            drawCircle(tono(AzulProfundo), s(0.28f), c, style = Stroke(width = s(0.035f)))
            val aguja = Path().apply {
                moveTo(p(0.5f, 0.26f).x, p(0.5f, 0.26f).y)
                lineTo(p(0.60f, 0.54f).x, p(0.60f, 0.54f).y)
                lineTo(p(0.5f, 0.74f).x, p(0.5f, 0.74f).y)
                lineTo(p(0.40f, 0.54f).x, p(0.40f, 0.54f).y)
                close()
            }
            drawPath(aguja, tono(CoralAviso))
            val mitad = Path().apply {
                moveTo(p(0.5f, 0.74f).x, p(0.5f, 0.74f).y)
                lineTo(p(0.60f, 0.54f).x, p(0.60f, 0.54f).y)
                lineTo(p(0.40f, 0.54f).x, p(0.40f, 0.54f).y)
                close()
            }
            drawPath(mitad, tono(AzulMedio))
            drawCircle(tono(AzulProfundo), s(0.035f), c)
        }

        // Cristal: cazador de patrones
        ClaveInsignia.CRISTAL -> {
            val gema = Path().apply {
                moveTo(p(0.5f, 0.20f).x, p(0.5f, 0.20f).y)
                lineTo(p(0.76f, 0.42f).x, p(0.76f, 0.42f).y)
                lineTo(p(0.5f, 0.80f).x, p(0.5f, 0.80f).y)
                lineTo(p(0.24f, 0.42f).x, p(0.24f, 0.42f).y)
                close()
            }
            drawPath(gema, tono(TurquesaAgua).copy(alpha = 0.75f))
            drawPath(gema, tono(AzulProfundo), style = Stroke(width = s(0.035f)))
            drawLine(tono(AzulProfundo), p(0.24f, 0.42f), p(0.76f, 0.42f), strokeWidth = s(0.024f))
            drawLine(tono(AzulProfundo), p(0.5f, 0.20f), p(0.5f, 0.80f), strokeWidth = s(0.024f))
        }
    }
}
