package com.ojocientifico.app.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.ui.componentes.BotonContorno
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.componentes.TituloSeccion
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.AppViewModel

private val ColoresChapa = listOf(VerdeNatural, TurquesaAgua, AmarilloDescubrimiento, CoralAviso)

/**
 * Configuración del explorador: alias, chapa y accesibilidad.
 * No se pide ni se guarda ningún dato personal.
 */
@Composable
fun PantallaAjustes(appViewModel: AppViewModel, onVolver: () -> Unit) {
    val configuracion by appViewModel.configuracion.collectAsStateWithLifecycle()
    var alias by remember(configuracion.alias) { mutableStateOf(configuracion.alias) }
    var confirmandoReinicio by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion("Configuración", "Ajusta la aplicación a tu gusto", onVolver)

        Column(Modifier.padding(horizontal = 16.dp)) {

            TituloSeccion("Tu identidad de explorador")
            Spacer(Modifier.height(10.dp))
            TarjetaCampo(Modifier.fillMaxWidth()) {
                Text("Nombre de explorador", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                        .padding(horizontal = 14.dp),
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
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Es un alias. Nunca escribas tu nombre real, tu correo ni tu dirección.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))
                Text("Chapa de expedición", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColoresChapa.forEachIndexed { indice, color ->
                        val elegida = configuracion.avatar == indice
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (elegida) 5.dp else 2.dp,
                                    if (elegida) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                                .clickable {
                                    appViewModel.guardar(configuracion.copy(avatar = indice, alias = alias))
                                }
                                .semantics {
                                    contentDescription =
                                        "Chapa ${indice + 1}" + if (elegida) ", elegida" else ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (elegida) {
                                Text("✓", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                BotonExpedicion(
                    texto = "Guardar cambios",
                    onClick = { appViewModel.guardar(configuracion.copy(alias = alias)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))
            TituloSeccion("Cómo se ve y se siente")
            Spacer(Modifier.height(10.dp))
            TarjetaCampo(Modifier.fillMaxWidth()) {
                Interruptor(
                    "Texto grande",
                    "Aumenta el tamaño de todas las letras.",
                    configuracion.textoGrande
                ) { appViewModel.guardar(configuracion.copy(textoGrande = it, alias = alias)) }

                Interruptor(
                    "Alto contraste",
                    "Colores más marcados para leer mejor.",
                    configuracion.altoContraste
                ) { appViewModel.guardar(configuracion.copy(altoContraste = it, alias = alias)) }

                Interruptor(
                    "Animaciones",
                    "Movimientos suaves al cambiar de pantalla.",
                    configuracion.animacionesActivas
                ) { appViewModel.guardar(configuracion.copy(animacionesActivas = it, alias = alias)) }

                Interruptor(
                    "Vibración",
                    "Pequeña vibración al acertar.",
                    configuracion.vibracionActiva
                ) { appViewModel.guardar(configuracion.copy(vibracionActiva = it, alias = alias)) }

                Interruptor(
                    "Sonido",
                    "Efectos de sonido de la aplicación.",
                    configuracion.sonidoActivo
                ) { appViewModel.guardar(configuracion.copy(sonidoActivo = it, alias = alias)) }
            }

            Spacer(Modifier.height(20.dp))
            TituloSeccion("Privacidad", acento = VerdeNatural)
            Spacer(Modifier.height(10.dp))
            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = VerdeNatural) {
                Text(
                    "Ojo Científico funciona sin conexión a internet. No pide permisos de red, no recoge datos personales, no muestra anuncios y no hay compras. Todo lo que registras se guarda solo en este dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(20.dp))
            TituloSeccion("Empezar de nuevo", acento = CoralAviso)
            Spacer(Modifier.height(10.dp))
            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = CoralAviso) {
                Text(
                    "Borra tu XP, tus fichas, tus insignias y tus descubrimientos. Las muestras y las misiones se conservan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                if (!confirmandoReinicio) {
                    BotonContorno(
                        "Reiniciar mi progreso",
                        { confirmandoReinicio = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        "¿Seguro? Esto no se puede deshacer.",
                        style = MaterialTheme.typography.titleMedium,
                        color = CoralAviso
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BotonContorno(
                            "Cancelar",
                            { confirmandoReinicio = false },
                            modifier = Modifier.weight(1f)
                        )
                        BotonExpedicion(
                            texto = "Sí, reiniciar",
                            onClick = {
                                appViewModel.reiniciarProgreso()
                                confirmandoReinicio = false
                            },
                            modifier = Modifier.weight(1f),
                            color = CoralAviso
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Ojo Científico · versión 1.0.0\nSoftware educativo para el desarrollo de la observación científica y el registro morfológico en niños de 8 a 12 años.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun Interruptor(
    titulo: String,
    descripcion: String,
    valor: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .semantics {
                contentDescription = "$titulo. $descripcion. " +
                    if (valor) "Activado" else "Desactivado"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                descripcion,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = valor, onCheckedChange = onCambio)
    }
}
