package com.ojocientifico.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Perfil del explorador. Solo alias y avatar: ningún dato personal. */
@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val id: Int = 1,
    val alias: String = "Explorador",
    val avatar: Int = 0,
    val xp: Int = 0,
    val creadoMillis: Long = 0L
)

/** Avance real en cada misión. */
@Entity(tableName = "progreso_mision")
data class ProgresoMisionEntity(
    @PrimaryKey val misionId: String,
    val completada: Boolean = false,
    val mejorEstrellas: Int = 0,
    val mejorPrecision: Float = 0f,
    val intentos: Int = 0,
    val ultimaFechaMillis: Long = 0L
)

/** Insignias concedidas. Una vez ganada, no se retira. */
@Entity(tableName = "insignia")
data class InsigniaEntity(
    @PrimaryKey val id: String,
    val desbloqueada: Boolean = false,
    val fechaMillis: Long? = null
)

/** Tarjetas de la colección de descubrimientos. */
@Entity(tableName = "descubrimiento")
data class DescubrimientoEntity(
    @PrimaryKey val muestraId: String,
    val desbloqueado: Boolean = false,
    val fechaMillis: Long? = null
)

/** Un día en el que hubo actividad. Sirve para contar días de expedición. */
@Entity(tableName = "dia_actividad")
data class DiaActividadEntity(
    @PrimaryKey val fecha: String
)

/** Preferencias de la aplicación. */
@Entity(tableName = "configuracion")
data class ConfiguracionEntity(
    @PrimaryKey val id: Int = 1,
    val sonidoActivo: Boolean = true,
    val vibracionActiva: Boolean = true,
    val animacionesActivas: Boolean = true,
    val textoGrande: Boolean = false,
    val altoContraste: Boolean = false,
    val onboardingHecho: Boolean = false
)
