package com.ojocientifico.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ojocientifico.app.data.local.entity.FichaEntity
import com.ojocientifico.app.data.local.entity.HistorialEntity
import com.ojocientifico.app.data.local.entity.ObservacionEntity
import com.ojocientifico.app.data.local.entity.RasgoFalladoEntity
import kotlinx.coroutines.flow.Flow

/** Cuaderno científico: fichas, observaciones, fallos e historial. */
@Dao
interface RegistroDao {

    // ------------------------------ Fichas ------------------------------

    @Insert
    suspend fun insertarFicha(ficha: FichaEntity): Long

    @Insert
    suspend fun insertarObservaciones(observaciones: List<ObservacionEntity>)

    /** Guarda la ficha y todas sus marcas en una sola transacción. */
    @Transaction
    suspend fun guardarFichaCompleta(
        ficha: FichaEntity,
        marcadas: List<Pair<String, Boolean>>
    ): Long {
        val id = insertarFicha(ficha)
        if (marcadas.isNotEmpty()) {
            insertarObservaciones(
                marcadas.map { (opcionId, correcta) ->
                    ObservacionEntity(fichaId = id, opcionId = opcionId, correcta = correcta)
                }
            )
        }
        return id
    }

    @Query("SELECT * FROM ficha ORDER BY fechaMillis DESC")
    fun observarFichas(): Flow<List<FichaEntity>>

    @Query("SELECT * FROM ficha WHERE muestraId = :muestraId ORDER BY fechaMillis DESC")
    fun observarFichasDe(muestraId: String): Flow<List<FichaEntity>>

    @Query("SELECT * FROM ficha WHERE id = :id")
    suspend fun ficha(id: Long): FichaEntity?

    @Query("SELECT * FROM observacion WHERE fichaId = :fichaId")
    suspend fun observacionesDe(fichaId: Long): List<ObservacionEntity>

    @Query("SELECT COUNT(*) FROM ficha")
    suspend fun contarFichas(): Int

    @Query("SELECT COUNT(DISTINCT opcionId) FROM observacion WHERE correcta = 1")
    suspend fun contarRasgosDistintosAcertados(): Int

    @Query("SELECT DISTINCT opcionId FROM observacion WHERE correcta = 1")
    suspend fun rasgosAcertados(): List<String>

    @Query("SELECT DISTINCT muestraId FROM ficha")
    suspend fun muestrasConFicha(): List<String>

    // ------------------------- Rasgos que se escapan -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarFallo(fallo: RasgoFalladoEntity)

    @Query("SELECT * FROM rasgo_fallado WHERE muestraId = :muestraId AND opcionId = :opcionId")
    suspend fun fallo(muestraId: String, opcionId: String): RasgoFalladoEntity?

    /** Suma una repetición al contador de un rasgo que se escapó. */
    @Transaction
    suspend fun acumularFallo(muestraId: String, opcionId: String, fechaMillis: Long) {
        val previo = fallo(muestraId, opcionId)
        guardarFallo(
            RasgoFalladoEntity(
                muestraId = muestraId,
                opcionId = opcionId,
                veces = (previo?.veces ?: 0) + 1,
                ultimaFechaMillis = fechaMillis
            )
        )
    }

    /** Una observación acertada retira el rasgo de la lista de repaso. */
    @Query("DELETE FROM rasgo_fallado WHERE muestraId = :muestraId AND opcionId IN (:opciones)")
    suspend fun resolverFallos(muestraId: String, opciones: List<String>)

    @Query("SELECT * FROM rasgo_fallado ORDER BY veces DESC, ultimaFechaMillis DESC")
    fun observarFallos(): Flow<List<RasgoFalladoEntity>>

    @Query("SELECT * FROM rasgo_fallado")
    suspend fun fallos(): List<RasgoFalladoEntity>

    // ----------------------------- Historial -----------------------------

    @Insert
    suspend fun insertarHistorial(entrada: HistorialEntity)

    @Query("SELECT * FROM historial ORDER BY fechaMillis DESC LIMIT :limite")
    fun observarHistorial(limite: Int = 60): Flow<List<HistorialEntity>>

    @Query("SELECT COUNT(*) FROM historial WHERE tipo = :tipo")
    suspend fun contarPorTipo(tipo: String): Int

    @Query("SELECT COALESCE(SUM(xpGanado), 0) FROM historial")
    suspend fun xpAcumulado(): Int

    // ------------------------------ Reinicio ------------------------------

    @Query("DELETE FROM ficha")
    suspend fun borrarFichas()

    @Query("DELETE FROM rasgo_fallado")
    suspend fun borrarFallos()

    @Query("DELETE FROM historial")
    suspend fun borrarHistorial()
}
