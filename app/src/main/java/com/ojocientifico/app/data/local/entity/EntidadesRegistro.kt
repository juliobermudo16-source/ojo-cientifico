package com.ojocientifico.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Una ficha del cuaderno científico, tal y como la guardó el explorador. */
@Entity(
    tableName = "ficha",
    indices = [Index("muestraId"), Index("fechaMillis")]
)
data class FichaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val muestraId: String,
    val misionId: String?,
    val fechaMillis: Long,
    val aciertos: Int,
    val totalEsperado: Int,
    val marcasDeMas: Int,
    val estrellas: Int,
    val nota: String
)

/** Cada característica marcada dentro de una ficha, con su veredicto. */
@Entity(
    tableName = "observacion",
    foreignKeys = [
        ForeignKey(
            entity = FichaEntity::class,
            parentColumns = ["id"],
            childColumns = ["fichaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fichaId"), Index("opcionId")]
)
data class ObservacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fichaId: Long,
    val opcionId: String,
    val correcta: Boolean
)

/** Características que se escaparon, para alimentar "Vuelve a observar". */
@Entity(
    tableName = "rasgo_fallado",
    primaryKeys = ["muestraId", "opcionId"]
)
data class RasgoFalladoEntity(
    val muestraId: String,
    val opcionId: String,
    val veces: Int,
    val ultimaFechaMillis: Long
)

/** Historial completo de actividades realizadas. */
@Entity(tableName = "historial", indices = [Index("fechaMillis")])
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tipo: String,
    val referencia: String,
    val aciertos: Int,
    val fallos: Int,
    val estrellas: Int,
    val xpGanado: Int,
    val fechaMillis: Long
)
