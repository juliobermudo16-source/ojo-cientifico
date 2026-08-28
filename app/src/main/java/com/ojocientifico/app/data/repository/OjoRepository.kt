package com.ojocientifico.app.data.repository

import com.ojocientifico.app.data.local.OjoDatabase
import com.ojocientifico.app.data.local.aDominio
import com.ojocientifico.app.data.local.aEntidad
import com.ojocientifico.app.data.local.aRasgosEntidad
import com.ojocientifico.app.data.local.entity.ConfiguracionEntity
import com.ojocientifico.app.data.local.entity.DescubrimientoEntity
import com.ojocientifico.app.data.local.entity.DiaActividadEntity
import com.ojocientifico.app.data.local.entity.FichaEntity
import com.ojocientifico.app.data.local.entity.HistorialEntity
import com.ojocientifico.app.data.local.entity.InsigniaEntity
import com.ojocientifico.app.data.local.entity.PerfilEntity
import com.ojocientifico.app.data.seed.MisionesSemilla
import com.ojocientifico.app.data.seed.MuestrasSemilla
import com.ojocientifico.app.domain.logica.CalculadoraProgreso
import com.ojocientifico.app.domain.logica.MotorFeedback
import com.ojocientifico.app.domain.logica.MotorInsignias
import com.ojocientifico.app.domain.logica.PlanificadorRepaso
import com.ojocientifico.app.domain.logica.SistemaXp
import com.ojocientifico.app.domain.model.CatalogoInsignias
import com.ojocientifico.app.domain.model.CatalogoRasgos
import com.ojocientifico.app.domain.model.Configuracion
import com.ojocientifico.app.domain.model.CriterioClasificacion
import com.ojocientifico.app.domain.model.EstadisticasExplorador
import com.ojocientifico.app.domain.model.Feedback
import com.ojocientifico.app.domain.model.FichaCientifica
import com.ojocientifico.app.domain.model.Mision
import com.ojocientifico.app.domain.model.Muestra
import com.ojocientifico.app.domain.model.RasgoFallado
import com.ojocientifico.app.domain.model.ResultadoClasificacion
import com.ojocientifico.app.domain.model.ResultadoSeleccion
import com.ojocientifico.app.domain.model.ResumenMision
import com.ojocientifico.app.domain.model.TipoActividad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Único punto de acceso a los datos de Ojo Científico.
 *
 * Coordina la siembra inicial, la persistencia de cada actividad y el cálculo
 * de recompensas. Ninguna recompensa se concede sin quedar antes registrada.
 */
class OjoRepository(
    private val db: OjoDatabase,
    private val ahora: () -> Long = System::currentTimeMillis
) {

    private val catalogo = db.catalogoDao()
    private val progreso = db.progresoDao()
    private val registro = db.registroDao()

    // Caché en memoria del catálogo: es contenido fijo y se consulta constantemente.
    private var muestrasCache: List<Muestra> = emptyList()
    private var misionesCache: List<Mision> = emptyList()

    // La siembra puede pedirse a la vez desde la Application y desde un ViewModel.
    private val cerrojoSemilla = Mutex()

    // ============================== Siembra ==============================

    /**
     * Rellena la base con el contenido inicial si hace falta y crea el perfil.
     * Es idempotente: llamarla varias veces no duplica nada.
     */
    suspend fun asegurarSemilla() = cerrojoSemilla.withLock {
        if (catalogo.contarMuestras() == 0) {
            catalogo.insertarOpciones(CatalogoRasgos.todas.map { it.aEntidad() })
            catalogo.insertarMuestras(MuestrasSemilla.todas.map { it.aEntidad() })
            catalogo.insertarRasgos(MuestrasSemilla.todas.flatMap { it.aRasgosEntidad() })
        }
        if (catalogo.contarMisiones() == 0) {
            catalogo.insertarMisiones(MisionesSemilla.todas.map { it.aEntidad() })
        }
        progreso.crearPerfil(PerfilEntity(creadoMillis = ahora()))
        progreso.crearConfiguracion(ConfiguracionEntity())
        progreso.sembrarInsignias(CatalogoInsignias.todas.map { InsigniaEntity(it.id) })
        progreso.sembrarDescubrimientos(
            MuestrasSemilla.todas.map { DescubrimientoEntity(it.id) }
        )
        recargarCatalogo()
    }

    private suspend fun recargarCatalogo() {
        val rasgosPorMuestra = catalogo.rasgos().groupBy { it.muestraId }
        muestrasCache = catalogo.muestras().map { entidad ->
            entidad.aDominio(rasgosPorMuestra[entidad.id].orEmpty())
        }
        misionesCache = catalogo.misiones().map { it.aDominio() }
    }

    suspend fun muestras(): List<Muestra> {
        if (muestrasCache.isEmpty()) recargarCatalogo()
        return muestrasCache
    }

    suspend fun muestra(id: String): Muestra? = muestras().firstOrNull { it.id == id }

    suspend fun misiones(): List<Mision> {
        if (misionesCache.isEmpty()) recargarCatalogo()
        return misionesCache
    }

    suspend fun mision(id: String): Mision? = misiones().firstOrNull { it.id == id }

    // ============================== Flujos ==============================

    val perfil: Flow<PerfilEntity?> = progreso.observarPerfil()

    val xp: Flow<Int> = perfil.map { it?.xp ?: 0 }

    val configuracion: Flow<Configuracion> =
        combine(progreso.observarConfiguracion(), progreso.observarPerfil()) { conf, per ->
            (conf ?: ConfiguracionEntity()).aDominio(
                alias = per?.alias ?: "Explorador",
                avatar = per?.avatar ?: 0
            )
        }

    val misionesCompletadas: Flow<Set<String>> =
        progreso.observarProgresoMisiones().map { lista ->
            lista.filter { it.completada }.map { it.misionId }.toSet()
        }

    val estrellasPorMision: Flow<Map<String, Int>> =
        progreso.observarProgresoMisiones().map { lista ->
            lista.associate { it.misionId to it.mejorEstrellas }
        }

    val insigniasDesbloqueadas: Flow<Set<String>> =
        progreso.observarInsignias().map { lista ->
            lista.filter { it.desbloqueada }.map { it.id }.toSet()
        }

    val descubrimientosDesbloqueados: Flow<Set<String>> =
        progreso.observarDescubrimientos().map { lista ->
            lista.filter { it.desbloqueado }.map { it.muestraId }.toSet()
        }

    val historial: Flow<List<com.ojocientifico.app.domain.model.EntradaHistorial>> =
        registro.observarHistorial().map { lista -> lista.map { it.aDominio() } }

    val fallos: Flow<List<RasgoFallado>> =
        registro.observarFallos().map { lista -> lista.map { it.aDominio() } }

    /** Fichas del cuaderno, ya con las características que se marcaron. */
    val fichas: Flow<List<FichaCientifica>> =
        registro.observarFichas().map { lista ->
            lista.map { entidad ->
                entidad.aDominio(
                    registro.observacionesDe(entidad.id).map { it.opcionId }
                )
            }
        }

    fun fichasDe(muestraId: String): Flow<List<FichaCientifica>> =
        registro.observarFichasDe(muestraId).map { lista ->
            lista.map { entidad ->
                entidad.aDominio(registro.observacionesDe(entidad.id).map { it.opcionId })
            }
        }

    suspend fun ficha(id: Long): FichaCientifica? {
        val entidad = registro.ficha(id) ?: return null
        return entidad.aDominio(registro.observacionesDe(id).map { it.opcionId })
    }

    // =========================== Configuración ===========================

    suspend fun guardarConfiguracion(configuracion: Configuracion) {
        progreso.actualizarConfiguracion(
            ConfiguracionEntity(
                sonidoActivo = configuracion.sonidoActivo,
                vibracionActiva = configuracion.vibracionActiva,
                animacionesActivas = configuracion.animacionesActivas,
                textoGrande = configuracion.textoGrande,
                altoContraste = configuracion.altoContraste,
                onboardingHecho = configuracion.onboardingHecho
            )
        )
        progreso.renombrar(configuracion.alias.ifBlank { "Explorador" }, configuracion.avatar)
    }

    suspend fun completarOnboarding(alias: String, avatar: Int) {
        val actual = progreso.configuracion() ?: ConfiguracionEntity()
        progreso.actualizarConfiguracion(actual.copy(onboardingHecho = true))
        progreso.renombrar(alias.trim().take(16).ifBlank { "Explorador" }, avatar)
    }

    // ========================= Registro de actividad =========================

    /**
     * Guarda un registro morfológico completo: ficha, marcas, fallos, historial,
     * XP, progreso de misión, insignias y descubrimientos.
     */
    suspend fun registrarObservacion(
        muestra: Muestra,
        misionId: String?,
        resultado: ResultadoSeleccion,
        nota: String
    ): ResumenMision {
        val fecha = ahora()
        val xpBase = misionId?.let { mision(it)?.xpBase } ?: XP_EXPLORACION_LIBRE
        val yaCompletada = misionId?.let { progreso.progresoMision(it)?.completada } == true

        registro.guardarFichaCompleta(
            ficha = FichaEntity(
                muestraId = muestra.id,
                misionId = misionId,
                fechaMillis = fecha,
                aciertos = resultado.aciertos.size,
                totalEsperado = resultado.esperados.size,
                marcasDeMas = resultado.falsosPositivos.size,
                estrellas = resultado.estrellas,
                nota = nota.trim().take(280)
            ),
            marcadas = resultado.seleccionados.map { it to (it in resultado.esperados) }
        )

        resultado.omitidos.forEach { registro.acumularFallo(muestra.id, it, fecha) }
        if (resultado.aciertos.isNotEmpty()) {
            registro.resolverFallos(muestra.id, resultado.aciertos.toList())
        }

        // Una muestra entra en la colección cuando se observa con solvencia.
        val descubierto = resultado.precision >= UMBRAL_DESCUBRIMIENTO &&
            muestra.id !in progreso.descubrimientosDesbloqueados()
        if (descubierto) progreso.desbloquearDescubrimiento(muestra.id, fecha)

        return cerrarActividad(
            tipo = TipoActividad.OBSERVACION,
            referencia = misionId ?: muestra.id,
            misionId = misionId,
            aciertos = resultado.aciertos.size,
            fallos = resultado.omitidos.size + resultado.falsosPositivos.size,
            estrellas = resultado.estrellas,
            precision = resultado.precision,
            xpBase = xpBase,
            yaCompletada = yaCompletada,
            fecha = fecha,
            feedback = MotorFeedback.paraObservacion(resultado, muestra.nombre),
            descubrimientosNuevos = if (descubierto) listOf(muestra.id) else emptyList()
        )
    }

    suspend fun registrarComparacion(
        mision: Mision,
        a: Muestra,
        b: Muestra,
        resultado: ResultadoSeleccion,
        buscabaSemejanzas: Boolean
    ): ResumenMision {
        val fecha = ahora()
        val yaCompletada = progreso.progresoMision(mision.id)?.completada == true
        return cerrarActividad(
            tipo = TipoActividad.COMPARACION,
            referencia = mision.id,
            misionId = mision.id,
            aciertos = resultado.aciertos.size,
            fallos = resultado.omitidos.size + resultado.falsosPositivos.size,
            estrellas = resultado.estrellas,
            precision = resultado.precision,
            xpBase = mision.xpBase,
            yaCompletada = yaCompletada,
            fecha = fecha,
            feedback = MotorFeedback.paraComparacion(resultado, a.nombre, b.nombre, buscabaSemejanzas)
        )
    }

    suspend fun registrarPatron(
        mision: Mision,
        muestras: List<Muestra>,
        resultado: ResultadoSeleccion
    ): ResumenMision {
        val fecha = ahora()
        val yaCompletada = progreso.progresoMision(mision.id)?.completada == true
        return cerrarActividad(
            tipo = TipoActividad.PATRON,
            referencia = mision.id,
            misionId = mision.id,
            aciertos = resultado.aciertos.size,
            fallos = resultado.omitidos.size + resultado.falsosPositivos.size,
            estrellas = resultado.estrellas,
            precision = resultado.precision,
            xpBase = mision.xpBase,
            yaCompletada = yaCompletada,
            fecha = fecha,
            feedback = MotorFeedback.paraPatron(resultado, muestras.size)
        )
    }

    suspend fun registrarClasificacion(
        mision: Mision,
        criterio: CriterioClasificacion,
        resultado: ResultadoClasificacion,
        nombrePorId: (String) -> String
    ): ResumenMision {
        val fecha = ahora()
        val yaCompletada = progreso.progresoMision(mision.id)?.completada == true
        return cerrarActividad(
            tipo = TipoActividad.CLASIFICACION,
            referencia = mision.id,
            misionId = mision.id,
            aciertos = resultado.correctas.size,
            fallos = resultado.incorrectas.size + resultado.sinAsignar.size,
            estrellas = resultado.estrellas,
            precision = resultado.precision,
            xpBase = mision.xpBase,
            yaCompletada = yaCompletada,
            fecha = fecha,
            feedback = MotorFeedback.paraClasificacion(resultado, criterio, nombrePorId)
        )
    }

    /**
     * Paso común a todas las actividades: historial, XP, progreso de misión,
     * día de expedición e insignias. Devuelve el resumen que verá el niño.
     */
    private suspend fun cerrarActividad(
        tipo: TipoActividad,
        referencia: String,
        misionId: String?,
        aciertos: Int,
        fallos: Int,
        estrellas: Int,
        precision: Float,
        xpBase: Int,
        yaCompletada: Boolean,
        fecha: Long,
        feedback: Feedback,
        descubrimientosNuevos: List<String> = emptyList()
    ): ResumenMision {
        val xpGanado = SistemaXp.calcular(xpBase, precision, estrellas, yaCompletada)
        val xpAntes = progreso.perfil()?.xp ?: 0

        registro.insertarHistorial(
            HistorialEntity(
                tipo = tipo.name,
                referencia = referencia,
                aciertos = aciertos,
                fallos = fallos,
                estrellas = estrellas,
                xpGanado = xpGanado,
                fechaMillis = fecha
            )
        )
        if (xpGanado > 0) progreso.sumarXp(xpGanado)
        progreso.marcarDia(DiaActividadEntity(diaDe(fecha)))

        if (misionId != null) {
            progreso.registrarIntento(
                misionId = misionId,
                completada = estrellas >= ESTRELLAS_PARA_COMPLETAR,
                estrellas = estrellas,
                precision = precision,
                fechaMillis = fecha
            )
        }

        val estado = estadisticas()
        val yaGanadas = progreso.insigniasDesbloqueadas().toSet()
        val nuevas = MotorInsignias.nuevas(estado, yaGanadas)
        nuevas.forEach { progreso.desbloquearInsignia(it.id, fecha) }

        val xpDespues = xpAntes + xpGanado

        return ResumenMision(
            misionId = misionId ?: referencia,
            estrellas = estrellas,
            precision = precision,
            xpGanado = xpGanado,
            feedback = feedback,
            insigniasNuevas = nuevas,
            descubrimientosNuevos = descubrimientosNuevos,
            subioDeRango = CalculadoraProgreso.rangoGanado(xpAntes, xpDespues)
        )
    }

    // ============================ Estadísticas ============================

    /** Foto del avance real, siempre leída de la base de datos. */
    suspend fun estadisticas(): EstadisticasExplorador = EstadisticasExplorador(
        xp = progreso.perfil()?.xp ?: 0,
        misionesCompletadas = progreso.contarMisionesCompletadas(),
        misionesPerfectas = progreso.contarMisionesPerfectas(),
        fichasRegistradas = registro.contarFichas(),
        observacionesCorrectas = registro.contarPorTipo(TipoActividad.OBSERVACION.name),
        comparacionesCompletadas = registro.contarPorTipo(TipoActividad.COMPARACION.name),
        clasificacionesCompletadas = registro.contarPorTipo(TipoActividad.CLASIFICACION.name),
        patronesEncontrados = registro.contarPorTipo(TipoActividad.PATRON.name),
        descubrimientosDesbloqueados = progreso.contarDescubrimientos(),
        rasgosDistintosRegistrados = registro.contarRasgosDistintosAcertados(),
        categoriasExploradas = PlanificadorRepaso.categoriasExploradas(registro.rasgosAcertados()),
        diasDeExpedicion = progreso.contarDias()
    )

    // ============================== Reinicio ==============================

    /** Borra el progreso conservando el catálogo sembrado. */
    suspend fun reiniciarProgreso() {
        registro.borrarFichas()
        registro.borrarFallos()
        registro.borrarHistorial()
        progreso.borrarProgresoMisiones()
        progreso.reiniciarInsignias()
        progreso.reiniciarDescubrimientos()
        progreso.borrarDias()
        progreso.reiniciarXp()
    }

    /** Nivel de rango actual del explorador, leído del XP persistido. */
    suspend fun nivelActual(): Int =
        CalculadoraProgreso.rango(progreso.perfil()?.xp ?: 0).nivel

    private fun diaDe(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))

    companion object {
        /** Estrellas mínimas para dar una misión por superada. */
        const val ESTRELLAS_PARA_COMPLETAR = 2

        /** Precisión mínima para añadir una muestra a la colección. */
        const val UMBRAL_DESCUBRIMIENTO = 0.75f

        /** XP de una observación fuera de misión, en el laboratorio libre. */
        const val XP_EXPLORACION_LIBRE = 25
    }
}
