package com.ojocientifico.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Ajustes de accesibilidad que atraviesan toda la interfaz. */
data class AjustesVisuales(
    val textoGrande: Boolean = false,
    val altoContraste: Boolean = false,
    val animaciones: Boolean = true
)

val LocalAjustesVisuales = staticCompositionLocalOf { AjustesVisuales() }

private val EsquemaClaro = lightColorScheme(
    primary = AzulCientifico,
    onPrimary = BlancoRoto,
    primaryContainer = AzulNiebla,
    onPrimaryContainer = AzulProfundo,
    secondary = VerdeNatural,
    onSecondary = BlancoRoto,
    secondaryContainer = VerdeNiebla,
    onSecondaryContainer = VerdeOscuro,
    tertiary = AmbarCalido,
    onTertiary = AzulProfundo,
    tertiaryContainer = AmarilloSuave,
    onTertiaryContainer = MarronOscuro,
    background = BlancoRoto,
    onBackground = Grafito,
    surface = PapelCuaderno,
    onSurface = Grafito,
    surfaceVariant = PapelSombra,
    onSurfaceVariant = GrafitoSuave,
    outline = TierraClara,
    outlineVariant = ArenaClara,
    error = CoralAviso,
    onError = BlancoRoto
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulClaro,
    onPrimary = AzulProfundo,
    primaryContainer = AzulCientifico,
    onPrimaryContainer = AzulNiebla,
    secondary = VerdeClaro,
    onSecondary = VerdeOscuro,
    secondaryContainer = VerdeNatural,
    onSecondaryContainer = VerdeNiebla,
    tertiary = AmarilloDescubrimiento,
    onTertiary = AzulProfundo,
    tertiaryContainer = AmbarCalido,
    onTertiaryContainer = AzulProfundo,
    background = NocheProfunda,
    onBackground = ArenaClara,
    surface = NocheSuperficie,
    onSurface = ArenaClara,
    surfaceVariant = NocheVariante,
    onSurfaceVariant = TierraClara,
    outline = GrafitoSuave,
    outlineVariant = NocheVariante,
    error = CoralAviso,
    onError = AzulProfundo
)

private val EsquemaAltoContraste = lightColorScheme(
    primary = ContrasteAcento,
    onPrimary = ContrasteFondo,
    primaryContainer = ContrasteFondo,
    onPrimaryContainer = ContrasteTinta,
    secondary = ContrasteTinta,
    onSecondary = ContrasteFondo,
    secondaryContainer = ContrasteFondo,
    onSecondaryContainer = ContrasteTinta,
    tertiary = AmbarCalido,
    onTertiary = ContrasteTinta,
    background = ContrasteFondo,
    onBackground = ContrasteTinta,
    surface = ContrasteFondo,
    onSurface = ContrasteTinta,
    surfaceVariant = ContrasteFondo,
    onSurfaceVariant = ContrasteTinta,
    outline = ContrasteTinta,
    error = RojoContraste,
    onError = ContrasteFondo
)

private fun tipografia(escala: Float) = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = (30 * escala).sp,
        lineHeight = (36 * escala).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = (25 * escala).sp,
        lineHeight = (31 * escala).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = (21 * escala).sp,
        lineHeight = (27 * escala).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = (19 * escala).sp,
        lineHeight = (25 * escala).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = (17 * escala).sp,
        lineHeight = (23 * escala).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = (17 * escala).sp,
        lineHeight = (25 * escala).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = (15 * escala).sp,
        lineHeight = (22 * escala).sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = (15 * escala).sp,
        letterSpacing = 0.3.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = (13 * escala).sp,
        letterSpacing = 0.4.sp
    )
)

@Composable
fun OjoCientificoTheme(
    ajustes: AjustesVisuales = AjustesVisuales(),
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquema = when {
        ajustes.altoContraste -> EsquemaAltoContraste
        oscuro -> EsquemaOscuro
        else -> EsquemaClaro
    }
    val escala = if (ajustes.textoGrande) 1.18f else 1f

    CompositionLocalProvider(LocalAjustesVisuales provides ajustes) {
        MaterialTheme(
            colorScheme = esquema,
            typography = tipografia(escala),
            shapes = FormasOjo,
            content = content
        )
    }
}
