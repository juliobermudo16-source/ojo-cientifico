package com.ojocientifico.app.ui.ilustraciones

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.ojocientifico.app.domain.model.ClaveIlustracion
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AmbarCalido
import com.ojocientifico.app.ui.theme.ArenaClara
import com.ojocientifico.app.ui.theme.AzulClaro
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.AzulNiebla
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.Grafito
import com.ojocientifico.app.ui.theme.MoradoMicroscopio
import com.ojocientifico.app.ui.theme.Tierra
import com.ojocientifico.app.ui.theme.TierraClara
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeClaro
import com.ojocientifico.app.ui.theme.VerdeHoja
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.theme.VerdeOscuro
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Ilustraciones científicas originales de Ojo Científico.
 *
 * Todas se dibujan con Compose Canvas: no hay imágenes remotas, ni descargas,
 * ni dependencia de la resolución del dispositivo. Cada dibujo respeta los
 * rasgos morfológicos que el niño debe poder observar (simetría, número de
 * partes, estructura), porque la imagen es material de estudio, no decoración.
 */
@Composable
fun IlustracionMuestra(
    clave: ClaveIlustracion,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.semantics { contentDescription = descripcion }) {
        Canvas(modifier = Modifier.matchParentSize()) {
            dibujarMuestra(clave)
        }
    }
}

/** Marco de dibujo cuadrado y centrado, común a todas las ilustraciones. */
private class Lienzo(val origen: Offset, val lado: Float) {
    fun p(x: Float, y: Float) = Offset(origen.x + x * lado, origen.y + y * lado)
    fun s(v: Float) = v * lado
    val centro: Offset get() = p(0.5f, 0.5f)
}

private fun DrawScope.lienzo(): Lienzo {
    val lado = min(size.width, size.height)
    return Lienzo(Offset((size.width - lado) / 2f, (size.height - lado) / 2f), lado)
}

fun DrawScope.dibujarMuestra(clave: ClaveIlustracion) {
    val l = lienzo()
    when (clave) {
        ClaveIlustracion.HOJA_HELECHO -> hojaHelecho(l)
        ClaveIlustracion.MARIPOSA -> mariposa(l)
        ClaveIlustracion.CARACOL -> caracol(l)
        ClaveIlustracion.HORMIGA -> hormiga(l)
        ClaveIlustracion.PEZ -> pez(l)
        ClaveIlustracion.GIRASOL -> girasol(l)
        ClaveIlustracion.SEMILLA_VIENTO -> semillaViento(l)
        ClaveIlustracion.PLUMA -> pluma(l)
        ClaveIlustracion.ESTRELLA_MAR -> estrellaMar(l)
        ClaveIlustracion.ESCARABAJO -> escarabajo(l)
        ClaveIlustracion.SETA -> seta(l)
        ClaveIlustracion.CRISTAL -> cristal(l)
        ClaveIlustracion.TELARANA -> telarana(l)
        ClaveIlustracion.COPO_NIEVE -> copoNieve(l)
        ClaveIlustracion.CELULA_VEGETAL -> celulaVegetal(l)
        ClaveIlustracion.RANA -> rana(l)
    }
}

// ============================== Utilidades ==============================

private fun DrawScope.elipse(
    centro: Offset,
    anchoMedio: Float,
    altoMedio: Float,
    color: Color,
    grados: Float = 0f
) {
    rotate(grados, centro) {
        drawOval(
            color = color,
            topLeft = Offset(centro.x - anchoMedio, centro.y - altoMedio),
            size = Size(anchoMedio * 2, altoMedio * 2)
        )
    }
}

private fun DrawScope.contornoElipse(
    centro: Offset,
    anchoMedio: Float,
    altoMedio: Float,
    color: Color,
    grosor: Float,
    grados: Float = 0f
) {
    rotate(grados, centro) {
        drawOval(
            color = color,
            topLeft = Offset(centro.x - anchoMedio, centro.y - altoMedio),
            size = Size(anchoMedio * 2, altoMedio * 2),
            style = Stroke(width = grosor)
        )
    }
}

private fun DrawScope.linea(a: Offset, b: Offset, color: Color, grosor: Float) {
    drawLine(color, a, b, strokeWidth = grosor, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

private fun polar(centro: Offset, radio: Float, gradosAngulo: Float): Offset {
    val rad = Math.toRadians(gradosAngulo.toDouble())
    return Offset(centro.x + radio * cos(rad).toFloat(), centro.y + radio * sin(rad).toFloat())
}

// ============================== 1. Helecho ==============================

private fun DrawScope.hojaHelecho(l: Lienzo) {
    val base = l.p(0.5f, 0.95f)
    val punta = l.p(0.5f, 0.07f)
    // Raquis (tallo central)
    val tallo = Path().apply {
        moveTo(base.x, base.y)
        quadraticBezierTo(l.p(0.56f, 0.5f).x, l.p(0.56f, 0.5f).y, punta.x, punta.y)
    }
    drawPath(tallo, VerdeOscuro, style = Stroke(width = l.s(0.022f)))

    // Pares de foliolos, decrecientes hacia la punta: la hoja es bilateral.
    val pares = 11
    for (i in 0 until pares) {
        val t = i / (pares - 1f)
        val y = 0.9f - t * 0.78f
        val escala = 0.24f * (1f - t * 0.82f) + 0.03f
        val x = 0.5f + 0.055f * (1f - t)
        for (lado in listOf(-1f, 1f)) {
            val anclaje = l.p(x, y)
            val extremo = l.p(x + lado * escala * 1.5f, y - 0.05f - escala * 0.35f)
            val hoja = Path().apply {
                moveTo(anclaje.x, anclaje.y)
                quadraticBezierTo(
                    anclaje.x + lado * l.s(escala * 0.9f), anclaje.y - l.s(escala * 0.75f),
                    extremo.x, extremo.y
                )
                quadraticBezierTo(
                    anclaje.x + lado * l.s(escala * 0.8f), anclaje.y + l.s(escala * 0.16f),
                    anclaje.x, anclaje.y
                )
                close()
            }
            drawPath(hoja, if (i % 2 == 0) VerdeHoja else VerdeNatural)
            // Nervadura del foliolo
            linea(anclaje, extremo, VerdeOscuro.copy(alpha = 0.55f), l.s(0.006f))
        }
    }
}

// ============================== 2. Mariposa ==============================

private fun DrawScope.mariposa(l: Lienzo) {
    val c = l.centro
    val naranja = Color(0xFFE8802B)
    for (lado in listOf(-1f, 1f)) {
        // Ala superior
        val superior = Path().apply {
            moveTo(c.x, c.y - l.s(0.12f))
            cubicTo(
                c.x + lado * l.s(0.42f), c.y - l.s(0.46f),
                c.x + lado * l.s(0.46f), c.y - l.s(0.05f),
                c.x + lado * l.s(0.10f), c.y + l.s(0.02f)
            )
            close()
        }
        drawPath(superior, naranja)
        drawPath(superior, Grafito, style = Stroke(width = l.s(0.014f)))

        // Ala inferior
        val inferior = Path().apply {
            moveTo(c.x, c.y + l.s(0.02f))
            cubicTo(
                c.x + lado * l.s(0.34f), c.y + l.s(0.10f),
                c.x + lado * l.s(0.26f), c.y + l.s(0.40f),
                c.x + lado * l.s(0.05f), c.y + l.s(0.26f)
            )
            close()
        }
        drawPath(inferior, Color(0xFFD96F22))
        drawPath(inferior, Grafito, style = Stroke(width = l.s(0.014f)))

        // Nervaduras
        for (k in 0..2) {
            linea(
                Offset(c.x + lado * l.s(0.05f), c.y - l.s(0.06f)),
                Offset(c.x + lado * l.s(0.30f - k * 0.05f), c.y - l.s(0.30f) + l.s(k * 0.10f)),
                Grafito.copy(alpha = 0.6f), l.s(0.008f)
            )
        }
        // Lunares blancos del borde
        for (k in 0..3) {
            drawCircle(
                Color.White,
                radius = l.s(0.018f),
                center = Offset(c.x + lado * l.s(0.33f - k * 0.05f), c.y - l.s(0.24f) + l.s(k * 0.075f))
            )
        }
    }

    // Cuerpo segmentado
    elipse(Offset(c.x, c.y + l.s(0.02f)), l.s(0.035f), l.s(0.20f), Grafito)
    elipse(Offset(c.x, c.y - l.s(0.16f)), l.s(0.042f), l.s(0.055f), Grafito)
    // Antenas
    for (lado in listOf(-1f, 1f)) {
        val inicio = Offset(c.x + lado * l.s(0.02f), c.y - l.s(0.20f))
        val fin = Offset(c.x + lado * l.s(0.16f), c.y - l.s(0.40f))
        linea(inicio, fin, Grafito, l.s(0.011f))
        drawCircle(Grafito, l.s(0.022f), fin)
    }
    // Ojos
    for (lado in listOf(-1f, 1f)) {
        drawCircle(Color.White, l.s(0.015f), Offset(c.x + lado * l.s(0.025f), c.y - l.s(0.185f)))
    }
}

// ============================== 3. Caracol ==============================

private fun DrawScope.caracol(l: Lienzo) {
    // Cuerpo blando
    val cuerpo = Path().apply {
        moveTo(l.p(0.08f, 0.86f).x, l.p(0.08f, 0.86f).y)
        cubicTo(
            l.p(0.05f, 0.62f).x, l.p(0.05f, 0.62f).y,
            l.p(0.22f, 0.52f).x, l.p(0.22f, 0.52f).y,
            l.p(0.36f, 0.60f).x, l.p(0.36f, 0.60f).y
        )
        lineTo(l.p(0.82f, 0.72f).x, l.p(0.82f, 0.72f).y)
        cubicTo(
            l.p(0.88f, 0.88f).x, l.p(0.88f, 0.88f).y,
            l.p(0.50f, 0.94f).x, l.p(0.50f, 0.94f).y,
            l.p(0.08f, 0.86f).x, l.p(0.08f, 0.86f).y
        )
        close()
    }
    drawPath(cuerpo, Color(0xFFC9A87C))
    drawPath(cuerpo, Tierra, style = Stroke(width = l.s(0.014f)))

    // Concha en espiral: cada vuelta es más ancha que la anterior.
    val centroConcha = l.p(0.60f, 0.45f)
    val vueltas = 3.2f
    val pasos = 200
    val espiral = Path()
    for (i in 0..pasos) {
        val t = i / pasos.toFloat()
        val angulo = t * vueltas * 360f
        val radio = l.s(0.045f + t * 0.28f)
        val punto = polar(centroConcha, radio, angulo - 90f)
        if (i == 0) espiral.moveTo(punto.x, punto.y) else espiral.lineTo(punto.x, punto.y)
    }
    drawCircle(Color(0xFFB4885A), l.s(0.33f), centroConcha)
    drawCircle(Tierra, l.s(0.33f), centroConcha, style = Stroke(width = l.s(0.016f)))
    drawPath(espiral, Color(0xFF7A5A38), style = Stroke(width = l.s(0.020f)))

    // Tentáculos con ojos en la punta
    for ((dx, dy) in listOf(0.06f to 0.30f, 0.13f to 0.36f)) {
        val inicio = l.p(0.14f, 0.70f)
        val fin = l.p(0.14f - dx * 0.4f, 0.70f - dy)
        linea(inicio, fin, Color(0xFFC9A87C), l.s(0.028f))
        drawCircle(Grafito, l.s(0.026f), fin)
        drawCircle(Color.White, l.s(0.010f), Offset(fin.x - l.s(0.008f), fin.y - l.s(0.008f)))
    }
}

// ============================== 4. Hormiga ==============================

private fun DrawScope.hormiga(l: Lienzo) {
    val negro = Color(0xFF3A2A22)
    val cabeza = l.p(0.24f, 0.5f)
    val torax = l.p(0.47f, 0.5f)
    val abdomen = l.p(0.74f, 0.5f)

    // Tres segmentos: cabeza, tórax y abdomen.
    elipse(cabeza, l.s(0.10f), l.s(0.085f), negro)
    elipse(torax, l.s(0.09f), l.s(0.070f), negro)
    elipse(abdomen, l.s(0.145f), l.s(0.115f), negro)
    linea(l.p(0.34f, 0.5f), l.p(0.38f, 0.5f), negro, l.s(0.024f))
    linea(l.p(0.56f, 0.5f), l.p(0.60f, 0.5f), negro, l.s(0.024f))

    // Seis patas: tres a cada lado.
    val anclajes = listOf(0.40f, 0.47f, 0.54f)
    for (lado in listOf(-1f, 1f)) {
        anclajes.forEachIndexed { i, x ->
            val a = l.p(x, 0.5f)
            val codo = l.p(x - 0.05f + i * 0.05f, 0.5f + lado * 0.16f)
            val pie = l.p(x - 0.14f + i * 0.14f, 0.5f + lado * 0.30f)
            linea(a, codo, negro, l.s(0.013f))
            linea(codo, pie, negro, l.s(0.013f))
        }
    }
    // Antenas acodadas
    for (lado in listOf(-1f, 1f)) {
        val a = l.p(0.19f, 0.5f + lado * 0.03f)
        val codo = l.p(0.10f, 0.5f + lado * 0.16f)
        val fin = l.p(0.04f, 0.5f + lado * 0.28f)
        linea(a, codo, negro, l.s(0.012f))
        linea(codo, fin, negro, l.s(0.012f))
    }
    // Mandíbulas
    for (lado in listOf(-1f, 1f)) {
        linea(l.p(0.17f, 0.5f + lado * 0.04f), l.p(0.10f, 0.5f + lado * 0.075f), negro, l.s(0.012f))
    }
    // Ojo y brillo del abdomen
    drawCircle(Color.White, l.s(0.016f), l.p(0.245f, 0.465f))
    drawCircle(Grafito, l.s(0.008f), l.p(0.248f, 0.468f))
    elipse(l.p(0.70f, 0.44f), l.s(0.045f), l.s(0.022f), Color.White.copy(alpha = 0.25f), -20f)
}

// ================================ 5. Pez ================================

private fun DrawScope.pez(l: Lienzo) {
    val naranja = Color(0xFFEE7B2E)
    val c = l.p(0.46f, 0.5f)

    // Cola
    val cola = Path().apply {
        moveTo(l.p(0.78f, 0.5f).x, l.p(0.78f, 0.5f).y)
        lineTo(l.p(0.97f, 0.30f).x, l.p(0.97f, 0.30f).y)
        lineTo(l.p(0.93f, 0.5f).x, l.p(0.93f, 0.5f).y)
        lineTo(l.p(0.97f, 0.70f).x, l.p(0.97f, 0.70f).y)
        close()
    }
    drawPath(cola, naranja)
    drawPath(cola, Grafito, style = Stroke(width = l.s(0.012f)))

    // Aleta dorsal y ventral
    val dorsal = Path().apply {
        moveTo(l.p(0.32f, 0.30f).x, l.p(0.32f, 0.30f).y)
        quadraticBezierTo(l.p(0.55f, 0.05f).x, l.p(0.55f, 0.05f).y, l.p(0.70f, 0.34f).x, l.p(0.70f, 0.34f).y)
        close()
    }
    drawPath(dorsal, Color(0xFFD96C22))
    drawPath(dorsal, Grafito, style = Stroke(width = l.s(0.012f)))
    val ventral = Path().apply {
        moveTo(l.p(0.40f, 0.70f).x, l.p(0.40f, 0.70f).y)
        quadraticBezierTo(l.p(0.52f, 0.92f).x, l.p(0.52f, 0.92f).y, l.p(0.64f, 0.68f).x, l.p(0.64f, 0.68f).y)
        close()
    }
    drawPath(ventral, Color(0xFFD96C22))
    drawPath(ventral, Grafito, style = Stroke(width = l.s(0.012f)))

    // Cuerpo ovalado
    elipse(c, l.s(0.36f), l.s(0.24f), naranja)

    // Tres franjas blancas con borde negro: simetría bilateral evidente.
    val franjas = listOf(0.22f to 0.055f, 0.46f to 0.045f, 0.70f to 0.035f)
    franjas.forEach { (x, ancho) ->
        val alturaMedia = 0.24f * (1f - kotlin.math.abs(x - 0.46f) * 1.1f)
        elipse(l.p(x, 0.5f), l.s(ancho), l.s(alturaMedia), Color.White)
        contornoElipse(l.p(x, 0.5f), l.s(ancho), l.s(alturaMedia), Grafito, l.s(0.010f))
    }
    contornoElipse(c, l.s(0.36f), l.s(0.24f), Grafito, l.s(0.014f))

    // Aleta pectoral, ojo y boca
    elipse(l.p(0.42f, 0.58f), l.s(0.075f), l.s(0.045f), Color(0xFFF0A15C), 25f)
    drawCircle(Color.White, l.s(0.055f), l.p(0.20f, 0.44f))
    drawCircle(Grafito, l.s(0.030f), l.p(0.205f, 0.445f))
    drawCircle(Color.White, l.s(0.011f), l.p(0.195f, 0.430f))
    drawArc(
        color = Grafito,
        startAngle = 20f, sweepAngle = 120f, useCenter = false,
        topLeft = l.p(0.08f, 0.48f), size = Size(l.s(0.10f), l.s(0.10f)),
        style = Stroke(width = l.s(0.012f))
    )
}

// ============================== 6. Girasol ==============================

private fun DrawScope.girasol(l: Lienzo) {
    val c = l.p(0.5f, 0.42f)

    // Tallo y hojas
    linea(l.p(0.5f, 0.42f), l.p(0.5f, 1.0f), VerdeNatural, l.s(0.036f))
    for (lado in listOf(-1f, 1f)) {
        val hoja = Path().apply {
            moveTo(l.p(0.5f, 0.80f).x, l.p(0.5f, 0.80f).y)
            quadraticBezierTo(
                l.p(0.5f + lado * 0.22f, 0.70f).x, l.p(0.5f + lado * 0.22f, 0.70f).y,
                l.p(0.5f + lado * 0.30f, 0.86f).x, l.p(0.5f + lado * 0.30f, 0.86f).y
            )
            quadraticBezierTo(
                l.p(0.5f + lado * 0.16f, 0.88f).x, l.p(0.5f + lado * 0.16f, 0.88f).y,
                l.p(0.5f, 0.80f).x, l.p(0.5f, 0.80f).y
            )
            close()
        }
        drawPath(hoja, VerdeHoja)
    }

    // Dos coronas de pétalos: simetría radial.
    for (anillo in 0..1) {
        val cantidad = if (anillo == 0) 16 else 14
        val radio = if (anillo == 0) 0.32f else 0.26f
        val largo = if (anillo == 0) 0.115f else 0.10f
        val color = if (anillo == 0) AmarilloDescubrimiento else AmbarCalido
        for (i in 0 until cantidad) {
            val angulo = i * (360f / cantidad) + anillo * 11f
            val punta = polar(c, l.s(radio + largo), angulo)
            elipse(
                Offset((c.x + punta.x) / 2f, (c.y + punta.y) / 2f),
                l.s(largo * 0.95f), l.s(0.045f), color, angulo
            )
        }
    }

    // Corazón con semillas
    drawCircle(Color(0xFF6B4A2A), l.s(0.20f), c)
    drawCircle(Color(0xFF4A3218), l.s(0.20f), c, style = Stroke(width = l.s(0.014f)))
    // Semillas en espiral: "muchos elementos iguales".
    for (i in 0 until 90) {
        val t = i / 90f
        val angulo = i * 137.5f
        val radio = l.s(0.19f) * kotlin.math.sqrt(t)
        drawCircle(Color(0xFF3A2612), l.s(0.013f), polar(c, radio, angulo))
    }
}

// ========================== 7. Semilla al viento ==========================

private fun DrawScope.semillaViento(l: Lienzo) {
    val corona = l.p(0.5f, 0.30f)
    // Filamentos radiales: el paracaídas.
    for (i in 0 until 26) {
        val angulo = i * (360f / 26f)
        val fin = polar(corona, l.s(0.33f), angulo)
        linea(corona, fin, Color(0xFFF2EDE2), l.s(0.010f))
        drawCircle(Color.White, l.s(0.014f), fin)
        // Barbas finas de cada filamento
        for (k in 1..2) {
            val medio = polar(corona, l.s(0.33f * k / 3f), angulo)
            linea(medio, polar(medio, l.s(0.05f), angulo - 40f), Color(0xFFEDE7DA), l.s(0.005f))
            linea(medio, polar(medio, l.s(0.05f), angulo + 40f), Color(0xFFEDE7DA), l.s(0.005f))
        }
    }
    drawCircle(Color(0xFFDCD3C0), l.s(0.03f), corona)

    // Tallo y aquenio (la semilla propiamente dicha)
    linea(corona, l.p(0.5f, 0.82f), Color(0xFFB79B6E), l.s(0.012f))
    val semilla = Path().apply {
        moveTo(l.p(0.5f, 0.82f).x, l.p(0.5f, 0.82f).y)
        quadraticBezierTo(l.p(0.545f, 0.90f).x, l.p(0.545f, 0.90f).y, l.p(0.5f, 0.97f).x, l.p(0.5f, 0.97f).y)
        quadraticBezierTo(l.p(0.455f, 0.90f).x, l.p(0.455f, 0.90f).y, l.p(0.5f, 0.82f).x, l.p(0.5f, 0.82f).y)
        close()
    }
    drawPath(semilla, Tierra)
    for (k in 0..3) {
        linea(
            l.p(0.475f + k * 0.017f, 0.855f), l.p(0.475f + k * 0.017f, 0.945f),
            Color(0xFF5E4527).copy(alpha = 0.6f), l.s(0.004f)
        )
    }
}

// =============================== 8. Pluma ===============================

private fun DrawScope.pluma(l: Lienzo) {
    val base = l.p(0.30f, 0.95f)
    val punta = l.p(0.68f, 0.06f)

    // Barbas a ambos lados del raquis: estructura de filamentos ramificados.
    val pasos = 46
    for (i in 0..pasos) {
        val t = i / pasos.toFloat()
        val x = 0.30f + (0.68f - 0.30f) * t + 0.05f * sin(t * 3.14159f).toFloat()
        val y = 0.95f - 0.89f * t
        val ancho = 0.24f * sin(t * 3.14159f).toFloat() * (1f - t * 0.25f)
        val origen = l.p(x, y)
        for (lado in listOf(-1f, 1f)) {
            val fin = Offset(
                origen.x + lado * l.s(ancho),
                origen.y - l.s(ancho * 0.55f)
            )
            val color = if (i % 3 == 0) Color(0xFF9C8B6E) else Color(0xFFB9A88C)
            linea(origen, fin, color, l.s(0.011f))
        }
    }
    // Raquis
    val raquis = Path().apply {
        moveTo(base.x, base.y)
        quadraticBezierTo(l.p(0.44f, 0.5f).x, l.p(0.44f, 0.5f).y, punta.x, punta.y)
    }
    drawPath(raquis, Color(0xFF7B6A4E), style = Stroke(width = l.s(0.018f)))
    // Cañón hueco
    linea(base, l.p(0.335f, 0.80f), Color(0xFFE4DCC8), l.s(0.026f))
}

// ============================ 9. Estrella de mar ============================

private fun DrawScope.estrellaMar(l: Lienzo) {
    val c = l.centro
    val brazos = 5
    val estrella = Path()
    val pasos = 360
    for (i in 0..pasos) {
        val angulo = i * (360f / pasos) - 90f
        val rad = Math.toRadians((angulo * brazos).toDouble())
        // Radio que oscila cinco veces: cinco brazos iguales (simetría radial).
        val factor = 0.62f + 0.38f * ((cos(rad).toFloat() + 1f) / 2f)
        val radio = l.s(0.46f) * factor
        val punto = polar(c, radio, angulo)
        if (i == 0) estrella.moveTo(punto.x, punto.y) else estrella.lineTo(punto.x, punto.y)
    }
    estrella.close()
    drawPath(estrella, Color(0xFFE07B3C))
    drawPath(estrella, Color(0xFF9E4A18), style = Stroke(width = l.s(0.016f)))

    // Textura granulada y surco central de cada brazo
    for (b in 0 until brazos) {
        val angulo = b * (360f / brazos) - 90f
        for (k in 1..6) {
            val r = l.s(0.07f + k * 0.055f)
            drawCircle(Color(0xFFF2B58A), l.s(0.017f - k * 0.0012f), polar(c, r, angulo))
        }
    }
    for (i in 0 until 26) {
        val angulo = i * (360f / 26f)
        drawCircle(Color(0xFFC9642A), l.s(0.012f), polar(c, l.s(0.16f), angulo))
    }
    drawCircle(Color(0xFFCE6A2C), l.s(0.075f), c)
    drawCircle(Color(0xFFF4C39C), l.s(0.028f), c)
}

// ============================= 10. Escarabajo =============================

private fun DrawScope.escarabajo(l: Lienzo) {
    val c = l.p(0.5f, 0.54f)
    val verde = Color(0xFF2E8B57)

    // Patas (seis) y antenas
    for (lado in listOf(-1f, 1f)) {
        listOf(0.30f, 0.50f, 0.70f).forEachIndexed { i, y ->
            val a = Offset(c.x + lado * l.s(0.17f), l.p(0.5f, y).y)
            val codo = Offset(c.x + lado * l.s(0.30f), l.p(0.5f, y - 0.05f + i * 0.05f).y)
            val pie = Offset(c.x + lado * l.s(0.42f), l.p(0.5f, y - 0.10f + i * 0.12f).y)
            linea(a, codo, Color(0xFF1C3A2A), l.s(0.016f))
            linea(codo, pie, Color(0xFF1C3A2A), l.s(0.014f))
        }
        linea(
            Offset(c.x + lado * l.s(0.07f), l.p(0.5f, 0.22f).y),
            Offset(c.x + lado * l.s(0.19f), l.p(0.5f, 0.07f).y),
            Color(0xFF1C3A2A), l.s(0.013f)
        )
    }

    // Cabeza y pronoto
    elipse(l.p(0.5f, 0.22f), l.s(0.10f), l.s(0.07f), Color(0xFF1F6B43))
    elipse(l.p(0.5f, 0.33f), l.s(0.17f), l.s(0.09f), Color(0xFF25794C))

    // Élitros: caparazón duro y brillante, partido por el eje de simetría.
    elipse(c, l.s(0.26f), l.s(0.33f), verde)
    contornoElipse(c, l.s(0.26f), l.s(0.33f), Color(0xFF14402F), l.s(0.016f))
    linea(l.p(0.5f, 0.24f), l.p(0.5f, 0.86f), Color(0xFF14402F), l.s(0.014f))
    for (lado in listOf(-1f, 1f)) {
        for (k in 1..2) {
            linea(
                Offset(c.x + lado * l.s(0.07f * k), c.y - l.s(0.26f)),
                Offset(c.x + lado * l.s(0.09f * k), c.y + l.s(0.26f)),
                Color(0xFF1B5C3B).copy(alpha = 0.7f), l.s(0.008f)
            )
        }
    }
    // Reflejo metálico
    elipse(l.p(0.40f, 0.42f), l.s(0.07f), l.s(0.12f), Color.White.copy(alpha = 0.28f), -18f)
    // Ojos
    for (lado in listOf(-1f, 1f)) {
        drawCircle(Color(0xFF0E2A1D), l.s(0.026f), Offset(c.x + lado * l.s(0.07f), l.p(0.5f, 0.20f).y))
        drawCircle(Color.White, l.s(0.009f), Offset(c.x + lado * l.s(0.077f), l.p(0.5f, 0.19f).y))
    }
}

// =============================== 11. Seta ===============================

private fun DrawScope.seta(l: Lienzo) {
    // Pie
    val pie = Path().apply {
        moveTo(l.p(0.40f, 0.50f).x, l.p(0.40f, 0.50f).y)
        lineTo(l.p(0.37f, 0.90f).x, l.p(0.37f, 0.90f).y)
        quadraticBezierTo(l.p(0.50f, 0.97f).x, l.p(0.50f, 0.97f).y, l.p(0.63f, 0.90f).x, l.p(0.63f, 0.90f).y)
        lineTo(l.p(0.60f, 0.50f).x, l.p(0.60f, 0.50f).y)
        close()
    }
    drawPath(pie, Color(0xFFF2EADA))
    drawPath(pie, TierraClara, style = Stroke(width = l.s(0.012f)))

    // Laminillas bajo el sombrero
    for (i in 0..14) {
        val x = 0.26f + i * 0.034f
        linea(l.p(x, 0.48f), l.p(0.5f, 0.56f), Color(0xFFDCCFB6), l.s(0.008f))
    }
    // Anillo
    elipse(l.p(0.5f, 0.60f), l.s(0.16f), l.s(0.032f), Color(0xFFE8DEC8))

    // Sombrero radial
    val sombrero = Path().apply {
        moveTo(l.p(0.10f, 0.50f).x, l.p(0.10f, 0.50f).y)
        cubicTo(
            l.p(0.14f, 0.10f).x, l.p(0.14f, 0.10f).y,
            l.p(0.86f, 0.10f).x, l.p(0.86f, 0.10f).y,
            l.p(0.90f, 0.50f).x, l.p(0.90f, 0.50f).y
        )
        quadraticBezierTo(l.p(0.50f, 0.58f).x, l.p(0.50f, 0.58f).y, l.p(0.10f, 0.50f).x, l.p(0.10f, 0.50f).y)
        close()
    }
    drawPath(sombrero, CoralAviso)
    drawPath(sombrero, Color(0xFF9E3822), style = Stroke(width = l.s(0.014f)))

    // Motas blancas: colores de aviso
    val motas = listOf(
        0.24f to 0.40f, 0.36f to 0.28f, 0.50f to 0.22f,
        0.64f to 0.28f, 0.76f to 0.40f, 0.44f to 0.40f, 0.58f to 0.41f
    )
    motas.forEach { (x, y) ->
        drawCircle(Color(0xFFF7F2E6), l.s(0.045f), l.p(x, y))
    }
}

// ============================= 12. Cristal =============================

private fun DrawScope.cristal(l: Lienzo) {
    val claro = Color(0xFFDDEBF3)
    val medio = Color(0xFFB9D4E4)
    val oscuro = Color(0xFF8FB6CC)

    // Prisma hexagonal con punta: estructura cristalina de seis caras.
    val cuerpoIzq = Path().apply {
        moveTo(l.p(0.30f, 0.30f).x, l.p(0.30f, 0.30f).y)
        lineTo(l.p(0.50f, 0.20f).x, l.p(0.50f, 0.20f).y)
        lineTo(l.p(0.50f, 0.80f).x, l.p(0.50f, 0.80f).y)
        lineTo(l.p(0.30f, 0.88f).x, l.p(0.30f, 0.88f).y)
        close()
    }
    val cuerpoDer = Path().apply {
        moveTo(l.p(0.50f, 0.20f).x, l.p(0.50f, 0.20f).y)
        lineTo(l.p(0.70f, 0.30f).x, l.p(0.70f, 0.30f).y)
        lineTo(l.p(0.70f, 0.88f).x, l.p(0.70f, 0.88f).y)
        lineTo(l.p(0.50f, 0.80f).x, l.p(0.50f, 0.80f).y)
        close()
    }
    val puntaIzq = Path().apply {
        moveTo(l.p(0.30f, 0.30f).x, l.p(0.30f, 0.30f).y)
        lineTo(l.p(0.44f, 0.05f).x, l.p(0.44f, 0.05f).y)
        lineTo(l.p(0.50f, 0.20f).x, l.p(0.50f, 0.20f).y)
        close()
    }
    val puntaDer = Path().apply {
        moveTo(l.p(0.50f, 0.20f).x, l.p(0.50f, 0.20f).y)
        lineTo(l.p(0.44f, 0.05f).x, l.p(0.44f, 0.05f).y)
        lineTo(l.p(0.70f, 0.30f).x, l.p(0.70f, 0.30f).y)
        close()
    }
    drawPath(cuerpoIzq, claro)
    drawPath(cuerpoDer, medio)
    drawPath(puntaIzq, Color(0xFFEFF6FA))
    drawPath(puntaDer, oscuro)

    listOf(cuerpoIzq, cuerpoDer, puntaIzq, puntaDer).forEach {
        drawPath(it, AzulMedio.copy(alpha = 0.65f), style = Stroke(width = l.s(0.011f)))
    }
    // Destellos
    linea(l.p(0.36f, 0.40f), l.p(0.36f, 0.72f), Color.White.copy(alpha = 0.8f), l.s(0.014f))
    linea(l.p(0.60f, 0.36f), l.p(0.60f, 0.55f), Color.White.copy(alpha = 0.45f), l.s(0.010f))
    // Cristales pequeños de la base
    val menor = Path().apply {
        moveTo(l.p(0.16f, 0.88f).x, l.p(0.16f, 0.88f).y)
        lineTo(l.p(0.22f, 0.58f).x, l.p(0.22f, 0.58f).y)
        lineTo(l.p(0.30f, 0.66f).x, l.p(0.30f, 0.66f).y)
        lineTo(l.p(0.29f, 0.90f).x, l.p(0.29f, 0.90f).y)
        close()
    }
    drawPath(menor, medio)
    drawPath(menor, AzulMedio.copy(alpha = 0.6f), style = Stroke(width = l.s(0.010f)))
}

// ============================= 13. Telaraña =============================

private fun DrawScope.telarana(l: Lienzo) {
    val c = l.centro
    val hilo = Color(0xFFEDEFF2)
    val radios = 12

    // Anclajes exteriores
    for (i in 0 until radios) {
        val angulo = i * (360f / radios)
        linea(c, polar(c, l.s(0.48f), angulo), hilo, l.s(0.008f))
    }
    // Espiral que cruza los radios
    val espiral = Path()
    val pasos = 260
    for (i in 0..pasos) {
        val t = i / pasos.toFloat()
        val angulo = t * 4.2f * 360f
        val radio = l.s(0.06f + t * 0.42f)
        val punto = polar(c, radio, angulo)
        if (i == 0) espiral.moveTo(punto.x, punto.y) else espiral.lineTo(punto.x, punto.y)
    }
    drawPath(espiral, hilo, style = Stroke(width = l.s(0.007f)))

    // Gotas de rocío: hacen visible el hilo
    for (i in 0 until radios) {
        val angulo = i * (360f / radios) + 15f
        listOf(0.20f, 0.33f, 0.44f).forEach { r ->
            drawCircle(AzulNiebla.copy(alpha = 0.85f), l.s(0.011f), polar(c, l.s(r), angulo))
        }
    }
    drawCircle(hilo, l.s(0.035f), c)
    drawCircle(AzulClaro.copy(alpha = 0.5f), l.s(0.035f), c, style = Stroke(width = l.s(0.008f)))
}

// ============================ 14. Copo de nieve ============================

private fun DrawScope.copoNieve(l: Lienzo) {
    val c = l.centro
    val hielo = Color(0xFFDDEEF8)
    val borde = AzulClaro

    // Seis brazos idénticos: así se ordenan las moléculas de agua al congelarse.
    for (i in 0 until 6) {
        val angulo = i * 60f
        val punta = polar(c, l.s(0.46f), angulo)
        linea(c, punta, hielo, l.s(0.024f))
        linea(c, punta, borde.copy(alpha = 0.5f), l.s(0.008f))

        // Ramas laterales, cada vez más cortas hacia la punta
        listOf(0.18f to 0.14f, 0.28f to 0.11f, 0.37f to 0.08f).forEach { (r, largo) ->
            val nodo = polar(c, l.s(r), angulo)
            listOf(-55f, 55f).forEach { desvio ->
                val fin = polar(nodo, l.s(largo), angulo + desvio)
                linea(nodo, fin, hielo, l.s(0.016f))
                linea(nodo, fin, borde.copy(alpha = 0.45f), l.s(0.006f))
            }
        }
        // Remate en punta de flecha
        listOf(-45f, 45f).forEach { desvio ->
            linea(punta, polar(punta, l.s(0.07f), angulo + 180f + desvio), hielo, l.s(0.014f))
        }
    }
    drawCircle(Color.White, l.s(0.055f), c)
    drawCircle(borde.copy(alpha = 0.55f), l.s(0.055f), c, style = Stroke(width = l.s(0.008f)))
    for (i in 0 until 6) {
        drawCircle(hielo, l.s(0.016f), polar(c, l.s(0.035f), i * 60f + 30f))
    }
}

// =========================== 15. Célula vegetal ===========================

private fun DrawScope.celulaVegetal(l: Lienzo) {
    val pared = Color(0xFF6E8F5A)
    val citoplasma = Color(0xFFDCEBCB)

    // Retícula de células vecinas: estructura celular repetida.
    val celdas = listOf(
        Triple(0.03f, 0.05f, 0.44f), Triple(0.53f, 0.05f, 0.44f),
        Triple(0.03f, 0.53f, 0.44f), Triple(0.53f, 0.53f, 0.44f)
    )
    celdas.forEachIndexed { indice, (x, y, ancho) ->
        val alto = 0.42f
        val rect = Path().apply {
            moveTo(l.p(x + 0.03f, y).x, l.p(x + 0.03f, y).y)
            lineTo(l.p(x + ancho - 0.03f, y + 0.02f).x, l.p(x + ancho - 0.03f, y + 0.02f).y)
            lineTo(l.p(x + ancho, y + alto - 0.02f).x, l.p(x + ancho, y + alto - 0.02f).y)
            lineTo(l.p(x + 0.02f, y + alto).x, l.p(x + 0.02f, y + alto).y)
            close()
        }
        drawPath(rect, citoplasma)
        drawPath(rect, pared, style = Stroke(width = l.s(0.018f)))

        val cx = x + ancho / 2f
        val cy = y + alto / 2f
        // Núcleo
        drawCircle(MoradoMicroscopio.copy(alpha = 0.85f), l.s(0.055f), l.p(cx, cy))
        drawCircle(Color(0xFF43356B), l.s(0.022f), l.p(cx + 0.012f, cy - 0.01f))
        // Cloroplastos repartidos junto a la membrana
        val cantidad = 9 + indice
        for (k in 0 until cantidad) {
            val angulo = k * (360f / cantidad) + indice * 12f
            val punto = polar(l.p(cx, cy), l.s(0.14f), angulo)
            elipse(punto, l.s(0.030f), l.s(0.018f), VerdeNatural, angulo)
        }
    }
    // Etiqueta visual de aumento del microscopio
    drawCircle(AzulProfundo.copy(alpha = 0.12f), l.s(0.50f), l.centro, style = Stroke(width = l.s(0.03f)))
}

// =============================== 16. Rana ===============================

private fun DrawScope.rana(l: Lienzo) {
    val verde = Color(0xFF5CB25A)
    val verdeOsc = Color(0xFF2F7A32)

    // Patas traseras plegadas
    for (lado in listOf(-1f, 1f)) {
        val muslo = Path().apply {
            moveTo(l.p(0.5f + lado * 0.16f, 0.62f).x, l.p(0.5f + lado * 0.16f, 0.62f).y)
            quadraticBezierTo(
                l.p(0.5f + lado * 0.46f, 0.62f).x, l.p(0.5f + lado * 0.46f, 0.62f).y,
                l.p(0.5f + lado * 0.40f, 0.88f).x, l.p(0.5f + lado * 0.40f, 0.88f).y
            )
            quadraticBezierTo(
                l.p(0.5f + lado * 0.22f, 0.80f).x, l.p(0.5f + lado * 0.22f, 0.80f).y,
                l.p(0.5f + lado * 0.16f, 0.62f).x, l.p(0.5f + lado * 0.16f, 0.62f).y
            )
            close()
        }
        drawPath(muslo, verdeOsc)
        // Dedos con discos adhesivos
        for (k in -1..1) {
            val dedo = l.p(0.5f + lado * (0.40f + k * 0.02f), 0.90f + k * 0.02f)
            linea(l.p(0.5f + lado * 0.38f, 0.86f), dedo, verdeOsc, l.s(0.020f))
            drawCircle(Color(0xFFE0A02E), l.s(0.024f), dedo)
        }
        // Costado azul: solo se ve cuando la rana salta
        elipse(l.p(0.5f + lado * 0.20f, 0.60f), l.s(0.07f), l.s(0.10f), TurquesaAgua.copy(alpha = 0.75f), lado * 15f)
    }

    // Cuerpo y cabeza
    elipse(l.p(0.5f, 0.58f), l.s(0.28f), l.s(0.24f), verde)
    elipse(l.p(0.5f, 0.38f), l.s(0.30f), l.s(0.19f), verde)
    contornoElipse(l.p(0.5f, 0.58f), l.s(0.28f), l.s(0.24f), verdeOsc, l.s(0.012f))

    // Patas delanteras
    for (lado in listOf(-1f, 1f)) {
        linea(l.p(0.5f + lado * 0.18f, 0.60f), l.p(0.5f + lado * 0.26f, 0.80f), verde, l.s(0.045f))
        drawCircle(Color(0xFFE0A02E), l.s(0.026f), l.p(0.5f + lado * 0.27f, 0.82f))
    }

    // Ojos rojos enormes, marca de la especie
    for (lado in listOf(-1f, 1f)) {
        val ojo = l.p(0.5f + lado * 0.20f, 0.26f)
        drawCircle(verde, l.s(0.115f), ojo)
        drawCircle(Color(0xFFD93A2B), l.s(0.092f), ojo)
        drawCircle(Color(0xFF6E1208), l.s(0.030f), ojo)
        drawCircle(Color.White, l.s(0.024f), Offset(ojo.x - l.s(0.030f), ojo.y - l.s(0.032f)))
    }
    // Boca
    drawArc(
        color = verdeOsc,
        startAngle = 15f, sweepAngle = 150f, useCenter = false,
        topLeft = l.p(0.36f, 0.38f), size = Size(l.s(0.28f), l.s(0.16f)),
        style = Stroke(width = l.s(0.014f))
    )
    // Textura húmeda
    listOf(0.40f to 0.55f, 0.58f to 0.62f, 0.50f to 0.68f).forEach { (x, y) ->
        drawCircle(Color.White.copy(alpha = 0.22f), l.s(0.030f), l.p(x, y))
    }
}
