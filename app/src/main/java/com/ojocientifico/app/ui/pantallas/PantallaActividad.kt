package com.ojocientifico.app.ui.pantallas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.CategoriaRasgo
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.TonoFeedback
import com.ojocientifico.app.ui.componentes.BotonContorno
import com.ojocientifico.app.ui.componentes.BotonExpedicion
import com.ojocientifico.app.ui.componentes.CabeceraExpedicion
import com.ojocientifico.app.ui.componentes.ChipCaracteristica
import com.ojocientifico.app.ui.componentes.EstadoChip
import com.ojocientifico.app.ui.componentes.Estrellas
import com.ojocientifico.app.ui.componentes.GloboDeIris
import com.ojocientifico.app.ui.componentes.TarjetaCampo
import com.ojocientifico.app.ui.ilustraciones.GestoIris
import com.ojocientifico.app.ui.ilustraciones.IlustracionMuestra
import com.ojocientifico.app.ui.ilustraciones.InsigniaIlustrada
import com.ojocientifico.app.ui.theme.AmarilloDescubrimiento
import com.ojocientifico.app.ui.theme.AzulMedio
import com.ojocientifico.app.ui.theme.CoralAviso
import com.ojocientifico.app.ui.theme.LocalAjustesVisuales
import com.ojocientifico.app.ui.theme.TurquesaAgua
import com.ojocientifico.app.ui.theme.VerdeNatural
import com.ojocientifico.app.ui.viewmodel.ActividadViewModel
import com.ojocientifico.app.ui.viewmodel.EstadoActividad

/**
 * Pantalla de trabajo del explorador. Según la misión muestra un registro
 * morfológico, una comparación, una clasificación o una búsqueda de patrón, y
 * termina siempre con feedback educativo.
 */
@Composable
fun PantallaActividad(
    viewModel: ActividadViewModel,
    arranque: () -> Unit,
    onSalir: () -> Unit
) {
    LaunchedEffect(Unit) { arranque() }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val actual = estado) {
        is EstadoActividad.Cargando -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Preparando la muestra…", style = MaterialTheme.typography.titleMedium)
        }

        is EstadoActividad.NoDisponible -> Column(Modifier.fillMaxSize()) {
            CabeceraExpedicion("Sin datos", onVolver = onSalir)
            Text(
                actual.motivo,
                Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        is EstadoActividad.Observacion -> VistaObservacion(actual, viewModel, onSalir)
        is EstadoActividad.Comparacion -> VistaComparacion(actual, viewModel, onSalir)
        is EstadoActividad.Patron -> VistaPatron(actual, viewModel, onSalir)
        is EstadoActividad.Clasificacion -> VistaClasificacion(actual, viewModel, onSalir)
        is EstadoActividad.Terminada -> VistaResultado(actual, viewModel, onSalir)
    }
}

// ========================= Registro morfológico =========================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VistaObservacion(
    estado: EstadoActividad.Observacion,
    vm: ActividadViewModel,
    onSalir: () -> Unit
) {
    val ultima = estado.categoriaActiva == estado.categorias.last()
    val opciones = estado.opciones[estado.categoriaActiva].orEmpty()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion(
            titulo = estado.mision?.titulo ?: estado.muestra.nombre,
            subtitulo = "Paso ${estado.categoriasVisitadas} de ${estado.categorias.size}",
            onVolver = onSalir
        )

        VisorDeMuestra(
            muestra = estado.muestra,
            lupaActiva = estado.lupaActiva,
            onLupa = { vm.alternarLupa() },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(14.dp))

        // Selector de categorías: siempre se ve dónde estás y qué falta.
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            estado.categorias.forEach { categoria ->
                val activa = categoria == estado.categoriaActiva
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (activa) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { vm.cambiarCategoria(categoria) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .semantics {
                            contentDescription =
                                categoria.etiqueta + if (activa) ", categoría actual" else ""
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        categoria.simbolo,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activa) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        categoria.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (activa) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(Modifier.padding(horizontal = 16.dp)) {
            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = TurquesaAgua) {
                Text(
                    estado.categoriaActiva.pregunta,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Puedes marcar más de una. Solo cuentan las que realmente observes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opciones.forEach { opcionId ->
                        ChipCaracteristica(
                            etiqueta = CatalogoRasgos.etiqueta(opcionId),
                            estado = if (opcionId in estado.seleccion) EstadoChip.SELECCIONADO
                            else EstadoChip.NEUTRO,
                            onClick = { vm.alternarOpcion(opcionId) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            if (ultima) {
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = MaterialTheme.colorScheme.outline) {
                    Text(
                        "Nota de campo (opcional)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 76.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = estado.nota,
                            onValueChange = { vm.escribirNota(it) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "Nota de campo" }
                        )
                        if (estado.nota.isEmpty()) {
                            Text(
                                "¿Qué te ha llamado la atención de esta muestra?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (estado.categoriaActiva != estado.categorias.first()) {
                    BotonContorno("Atrás", { vm.retrocederCategoria() })
                }
                if (ultima) {
                    BotonExpedicion(
                        texto = "Guardar ficha",
                        onClick = { vm.comprobar() },
                        icono = Icons.Filled.Check,
                        habilitado = estado.puedeComprobar,
                        modifier = Modifier.weight(1f),
                        color = VerdeNatural
                    )
                } else {
                    BotonExpedicion(
                        texto = "Siguiente",
                        onClick = { vm.avanzarCategoria() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

/** Visor con lupa: acerca la ilustración para buscar detalles. */
@Composable
private fun VisorDeMuestra(
    muestra: Muestra,
    lupaActiva: Boolean,
    onLupa: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animaciones = LocalAjustesVisuales.current.animaciones
    val zoom by animateFloatAsState(
        targetValue = if (lupaActiva) 1.75f else 1f,
        animationSpec = tween(if (animaciones) 450 else 0),
        label = "zoomLupa"
    )
    TarjetaCampo(
        modifier = modifier.fillMaxWidth(),
        colorBorde = AzulMedio,
        relleno = 12.dp
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            IlustracionMuestra(
                clave = muestra.ilustracion,
                descripcion = "${muestra.nombre}. ${muestra.descripcion}",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .scale(zoom)
            )
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (lupaActiva) AmarilloDescubrimiento else MaterialTheme.colorScheme.primary
                    )
                    .clickable { onLupa() }
                    .semantics {
                        contentDescription = if (lupaActiva) "Alejar la lupa" else "Acercar la lupa"
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Search, null,
                    tint = if (lupaActiva) MaterialTheme.colorScheme.onTertiary
                    else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(muestra.nombre, style = MaterialTheme.typography.titleLarge)
        Text(
            muestra.nombreCientifico,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================== Comparación ==============================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VistaComparacion(
    estado: EstadoActividad.Comparacion,
    vm: ActividadViewModel,
    onSalir: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion(estado.mision.titulo, estado.modo.etiqueta, onSalir)

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniMuestra(estado.a, "A", TurquesaAgua, Modifier.weight(1f))
            MiniMuestra(estado.b, "B", CoralAviso, Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))
        Column(Modifier.padding(horizontal = 16.dp)) {
            GloboDeIris(estado.mision.instruccionGuia, gesto = GestoIris.PENSANDO)
            Spacer(Modifier.height(14.dp))

            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = AzulMedio) {
                Text(
                    estado.modo.consigna,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                CatalogoRasgos.agrupar(estado.opciones).forEach { (categoria, lista) ->
                    Text(
                        categoria.etiqueta,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lista.forEach { opcion ->
                            ChipCaracteristica(
                                etiqueta = opcion.etiqueta,
                                estado = if (opcion.id in estado.seleccion) EstadoChip.SELECCIONADO
                                else EstadoChip.NEUTRO,
                                onClick = { vm.alternarOpcion(opcion.id) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            BotonExpedicion(
                texto = "Comprobar comparación",
                onClick = { vm.comprobar() },
                icono = Icons.Filled.Check,
                habilitado = estado.puedeComprobar,
                modifier = Modifier.fillMaxWidth(),
                color = VerdeNatural
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

// =============================== Patrones ===============================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VistaPatron(
    estado: EstadoActividad.Patron,
    vm: ActividadViewModel,
    onSalir: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion(estado.mision.titulo, "Busca lo que se repite en TODAS", onSalir)

        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            estado.muestras.forEach { muestra ->
                MiniMuestra(muestra, null, AmarilloDescubrimiento, Modifier.width(150.dp))
            }
        }

        Spacer(Modifier.height(14.dp))
        Column(Modifier.padding(horizontal = 16.dp)) {
            GloboDeIris(estado.mision.instruccionGuia, gesto = GestoIris.PENSANDO)
            Spacer(Modifier.height(14.dp))

            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = CoralAviso) {
                Text(
                    "Marca solo las características que estén en las ${estado.muestras.size} muestras.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                CatalogoRasgos.agrupar(estado.opciones).forEach { (categoria, lista) ->
                    Text(
                        categoria.etiqueta,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        lista.forEach { opcion ->
                            ChipCaracteristica(
                                etiqueta = opcion.etiqueta,
                                estado = if (opcion.id in estado.seleccion) EstadoChip.SELECCIONADO
                                else EstadoChip.NEUTRO,
                                onClick = { vm.alternarOpcion(opcion.id) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            BotonExpedicion(
                texto = "Revelar el patrón",
                onClick = { vm.comprobar() },
                icono = Icons.Filled.Check,
                habilitado = estado.puedeComprobar,
                modifier = Modifier.fillMaxWidth(),
                color = CoralAviso
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

// ============================= Clasificación =============================

@Composable
private fun VistaClasificacion(
    estado: EstadoActividad.Clasificacion,
    vm: ActividadViewModel,
    onSalir: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion(estado.mision.titulo, estado.criterio.etiqueta, onSalir)

        Column(Modifier.padding(horizontal = 16.dp)) {
            GloboDeIris(estado.mision.instruccionGuia, gesto = GestoIris.NEUTRO)
            Spacer(Modifier.height(12.dp))
            Text(
                estado.criterio.consigna,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Toca una muestra y después el grupo donde quieras colocarla.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))

            // Bandeja de muestras pendientes
            TarjetaCampo(
                Modifier.fillMaxWidth(),
                colorBorde = if (estado.pendientes.isEmpty()) VerdeNatural
                else MaterialTheme.colorScheme.outline
            ) {
                Text(
                    if (estado.pendientes.isEmpty()) "Bandeja vacía: todas colocadas"
                    else "Bandeja de muestras (${estado.pendientes.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    estado.pendientes.forEach { muestra ->
                        FichaArrastrable(
                            muestra = muestra,
                            seleccionada = estado.seleccionada == muestra.id,
                            onClick = { vm.seleccionarMuestra(muestra.id) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            estado.grupos.forEach { grupo ->
                val dentro = estado.muestras.filter { estado.asignaciones[it.id] == grupo.id }
                val resaltado = estado.seleccionada != null
                TarjetaCampo(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colorBorde = if (resaltado) AmarilloDescubrimiento
                    else MaterialTheme.colorScheme.outline,
                    grosorBorde = if (resaltado) 3.dp else 2.dp,
                    onClick = { vm.asignarAGrupo(grupo.id) }
                ) {
                    Text(
                        grupo.etiqueta,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        grupo.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    if (dentro.isEmpty()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (resaltado) "Toca aquí para colocarla" else "Vacío",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            dentro.forEach { muestra ->
                                FichaArrastrable(
                                    muestra = muestra,
                                    seleccionada = false,
                                    onClick = { vm.retirarDeGrupo(muestra.id) }
                                )
                            }
                        }
                    }
                }
            }

            BotonExpedicion(
                texto = "Comprobar clasificación",
                onClick = { vm.comprobar() },
                icono = Icons.Filled.Check,
                habilitado = estado.puedeComprobar,
                modifier = Modifier.fillMaxWidth(),
                color = VerdeNatural
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FichaArrastrable(
    muestra: Muestra,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .width(96.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                if (seleccionada) 4.dp else 2.dp,
                if (seleccionada) AmarilloDescubrimiento else MaterialTheme.colorScheme.outline,
                MaterialTheme.shapes.small
            )
            .clickable { onClick() }
            .padding(6.dp)
            .semantics {
                contentDescription = muestra.nombre + if (seleccionada) ", seleccionada" else ""
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IlustracionMuestra(
            muestra.ilustracion, muestra.nombre,
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            muestra.nombre,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun MiniMuestra(
    muestra: Muestra,
    etiqueta: String?,
    acento: Color,
    modifier: Modifier = Modifier
) {
    TarjetaCampo(modifier = modifier, colorBorde = acento, relleno = 10.dp) {
        if (etiqueta != null) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(acento)
                    .size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            IlustracionMuestra(
                muestra.ilustracion,
                "${muestra.nombre}. ${muestra.descripcion}",
                Modifier.fillMaxSize().padding(6.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(muestra.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 2)
    }
}

// ============================== Resultado ==============================

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun VistaResultado(
    estado: EstadoActividad.Terminada,
    vm: ActividadViewModel,
    onSalir: () -> Unit
) {
    val resumen = estado.resumen
    val feedback = resumen.feedback
    val acento = when (feedback.tono) {
        TonoFeedback.EXCELENTE -> VerdeNatural
        TonoFeedback.BIEN -> TurquesaAgua
        TonoFeedback.CASI -> AmarilloDescubrimiento
        TonoFeedback.REINTENTAR -> CoralAviso
    }
    val gesto = when (feedback.tono) {
        TonoFeedback.EXCELENTE -> GestoIris.CELEBRANDO
        TonoFeedback.BIEN -> GestoIris.ANIMANDO
        else -> GestoIris.PENSANDO
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraExpedicion(estado.tituloActividad, "Resultado", onSalir)

        Column(Modifier.padding(horizontal = 16.dp)) {

            TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = acento, grosorBorde = 3.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            feedback.titulo,
                            style = MaterialTheme.typography.headlineSmall,
                            color = acento
                        )
                        Spacer(Modifier.height(4.dp))
                        Estrellas(resumen.estrellas, tamano = 28.dp)
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AmarilloDescubrimiento)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "+${resumen.xpGanado} XP",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                GloboDeIris(feedback.mensaje, gesto = gesto)
                if (feedback.pista != null) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(12.dp)
                    ) {
                        Text(
                            feedback.pista,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Desglose: qué acertó, qué se le escapó y qué marcó de más.
            TarjetaCampo(Modifier.fillMaxWidth()) {
                Text(
                    "Tu registro, característica a característica",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                val etiquetar: (String) -> String = { id ->
                    estado.nombres[id] ?: CatalogoRasgos.etiqueta(id)
                }
                if (estado.desgloseDeMuestras) {
                    BloqueRasgos("Colocaste bien", estado.aciertos, EstadoChip.ACIERTO, etiquetar)
                    BloqueRasgos("Te quedaron sin colocar", estado.omitidos, EstadoChip.OLVIDADO, etiquetar)
                    BloqueRasgos("En el grupo equivocado", estado.sobrantes, EstadoChip.FALLO, etiquetar)
                } else {
                    BloqueRasgos("Observaste bien", estado.aciertos, EstadoChip.ACIERTO, etiquetar)
                    BloqueRasgos("Se te escapó", estado.omitidos, EstadoChip.OLVIDADO, etiquetar)
                    BloqueRasgos("No corresponde", estado.sobrantes, EstadoChip.FALLO, etiquetar)
                }
                if (estado.aciertos.isEmpty() && estado.omitidos.isEmpty() && estado.sobrantes.isEmpty()) {
                    Text(
                        "No hay nada que desglosar en esta actividad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (resumen.insigniasNuevas.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = AmarilloDescubrimiento) {
                    Text(
                        if (resumen.insigniasNuevas.size == 1) "¡Insignia conseguida!"
                        else "¡${resumen.insigniasNuevas.size} insignias conseguidas!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(10.dp))
                    resumen.insigniasNuevas.forEach { insignia ->
                        Row(
                            Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InsigniaIlustrada(
                                insignia.clave, true, insignia.nombre,
                                Modifier.size(64.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(insignia.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    insignia.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (resumen.subioDeRango != null) {
                Spacer(Modifier.height(14.dp))
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = VerdeNatural, grosorBorde = 3.dp) {
                    Text(
                        "¡Nuevo rango: ${resumen.subioDeRango.titulo}!",
                        style = MaterialTheme.typography.titleLarge,
                        color = VerdeNatural
                    )
                    Text(
                        resumen.subioDeRango.lema,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (resumen.descubrimientosNuevos.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                TarjetaCampo(Modifier.fillMaxWidth(), colorBorde = TurquesaAgua) {
                    Text(
                        "Nueva tarjeta en tu colección",
                        style = MaterialTheme.typography.titleMedium,
                        color = TurquesaAgua
                    )
                    Text(
                        "Has observado esta muestra con suficiente detalle como para añadirla a tus descubrimientos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (feedback.puedeReintentar) {
                    BotonContorno(
                        "Intentarlo otra vez",
                        { vm.reintentar() },
                        icono = Icons.Filled.Refresh,
                        modifier = Modifier.weight(1f)
                    )
                }
                BotonExpedicion(
                    texto = "Volver al laboratorio",
                    onClick = onSalir,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BloqueRasgos(
    titulo: String,
    ids: Set<String>,
    estado: EstadoChip,
    etiquetar: (String) -> String
) {
    if (ids.isEmpty()) return
    Text(
        "$titulo (${ids.size})",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(6.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ids.sortedBy { etiquetar(it) }.forEach { id ->
            ChipCaracteristica(etiqueta = etiquetar(id), estado = estado)
        }
    }
    Spacer(Modifier.height(14.dp))
}
