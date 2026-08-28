package com.ojocientifico.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Catálogo de características observables. Se siembra desde el dominio. */
@Entity(tableName = "opcion_rasgo")
data class OpcionRasgoEntity(
    @PrimaryKey val id: String,
    val categoria: String,
    val etiqueta: String,
    val pista: String
)

/** Ficha de catálogo de una muestra. */
@Entity(tableName = "muestra")
data class MuestraEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val nombreCientifico: String,
    val reino: String,
    val habitat: String,
    val ilustracion: String,
    val descripcion: String,
    val datoCurioso: String,
    val nivelRequerido: Int
)

/**
 * Relación entre una muestra y una característica.
 * [verdadero] distingue los rasgos reales de los distractores que se ofrecen
 * en pantalla, de modo que la verdad morfológica vive en la base de datos.
 */
@Entity(
    tableName = "rasgo_muestra",
    primaryKeys = ["muestraId", "opcionId"],
    foreignKeys = [
        ForeignKey(
            entity = MuestraEntity::class,
            parentColumns = ["id"],
            childColumns = ["muestraId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("opcionId")]
)
data class RasgoMuestraEntity(
    val muestraId: String,
    val opcionId: String,
    val verdadero: Boolean
)

/** Definición persistida de una misión. */
@Entity(tableName = "mision", indices = [Index("nivel"), Index("orden")])
data class MisionEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val consigna: String,
    val instruccionGuia: String,
    val tipo: String,
    val nivel: Int,
    val orden: Int,
    val muestrasIds: String,
    val categorias: String,
    val modoComparacion: String?,
    val criterio: String?,
    val xpBase: Int,
    val requiere: String?
)
