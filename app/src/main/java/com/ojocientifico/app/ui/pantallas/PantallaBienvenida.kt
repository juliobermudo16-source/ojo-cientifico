package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ojocientifico.app.domain.model.ClaveIlustracion
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.FondoCuaderno
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.ilustraciones.IconoLaboratorio
import com.ojocientifico.app.ui.ilustraciones.Iris
import com.ojocientifico.app.ui.ilustraciones.ObjetoLaboratorio
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulProfundo
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural

private val ColoresAvatar = listOf(VerdeNatural, TurquesaAgua, AmarilloDescubrimiento, CoralAviso)

/**
 * Primera pantalla: en menos de treinta segundos el niño ve una escena
 * científica, elige alias y avatar, y entra al laboratorio.
 * Nada de textos largos ni de datos personales.
 */
@Composable
fun PantallaBienvenida(onEmpezar: (String, Int) -> Unit) {
    var alias by remember { mutableStateOf("") }
    var avatar by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(AzulProfundo)) {
        FondoCuaderno(
            modifier = Modifier.fillMaxSize(),
            colorLinea = Color.White,
            intensidad = 0.07f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Portada: instrumentos y muestras alrededor del título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f),
                contentAlignment = Alignment.Center
            ) {
                IconoLaboratorio(
                    ObjetoLaboratorio.LUPA, "Lupa de campo",
                    Modifier.size(112.dp).align(Alignment.TopStart)
                )
                IlustracionMuestra(
                    ClaveIlustracion.MARIPOSA, "Mariposa monarca",
                    Modifier.size(96.dp).align(Alignment.TopEnd)
                )
                IlustracionMuestra(
                    ClaveIlustracion.HOJA_HELECHO, "Hoja de helecho",
                    Modifier.size(88.dp).align(Alignment.BottomStart)
                )
                IlustracionMuestra(
                    ClaveIlustracion.CRISTAL, "Cristal de cuarzo",
                    Modifier.size(84.dp).align(Alignment.BottomEnd)
                )
                Iris(gesto = GestoIris.ANIMANDO, modifier = Modifier.size(150.dp))
            }

            Text(
                "OJO CIENTÍFICO",
                style = MaterialTheme.typography.displaySmall,
                color = AmarilloDescubrimiento,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Observa. Registra. Descubre.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            TarjetaCampo(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                colorBorde = AmarilloDescubrimiento
            ) {
                Text(
                    "Antes de entrar al laboratorio",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Elige un nombre de explorador. No hace falta tu nombre real.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = alias,
                        onValueChange = { if (it.length <= 16) alias = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Nombre de explorador" }
                    )
                    if (alias.isEmpty()) {
                        Text(
                            "Por ejemplo: Ojo de Lince",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    "Tu chapa de expedición",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColoresAvatar.forEachIndexed { indice, color ->
                        val elegido = avatar == indice
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (elegido) 5.dp else 2.dp,
                                    color = if (elegido) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { avatar = indice }
                                .semantics {
                                    contentDescription =
                                        "Chapa ${indice + 1}" + if (elegido) ", elegida" else ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (elegido) {
                                Text("✓", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            BotonExpedicion(
                texto = "Entrar al laboratorio",
                onClick = { onEmpezar(alias.trim().ifBlank { "Explorador" }, avatar) },
                icono = Icons.Filled.ArrowForward,
                modifier = Modifier.fillMaxWidth(),
                color = AmarilloDescubrimiento,
                colorTexto = AzulProfundo
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Funciona sin internet y no guarda ningún dato personal.",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
