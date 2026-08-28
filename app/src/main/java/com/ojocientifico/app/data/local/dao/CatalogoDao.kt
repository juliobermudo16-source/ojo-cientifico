package com.ojocientifico.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ojocientifico.app.data.local.entity.MisionEntity
import com.ojocientifico.app.data.local.entity.MuestraEntity
import com.ojocientifico.app.data.local.entity.OpcionRasgoEntity
import com.ojocientifico.app.data.local.entity.RasgoMuestraEntity
import kotlinx.coroutines.flow.Flow

/** Acceso al catálogo sembrado: características, muestras y misiones. */
@Dao
interface CatalogoDao {

    // ----------------------------- Escritura -----------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOpciones(opciones: List<OpcionRasgoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMuestras(muestras: List<MuestraEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRasgos(rasgos: List<RasgoMuestraEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMisiones(misiones: List<MisionEntity>)

    // ------------------------------ Lectura ------------------------------

    @Query("SELECT COUNT(*) FROM muestra")
    suspend fun contarMuestras(): Int

    @Query("SELECT COUNT(*) FROM mision")
    suspend fun contarMisiones(): Int

    @Query("SELECT * FROM opcion_rasgo")
    suspend fun opciones(): List<OpcionRasgoEntity>

    @Query("SELECT * FROM muestra ORDER BY nivelRequerido, nombre")
    suspend fun muestras(): List<MuestraEntity>

    @Query("SELECT * FROM muestra ORDER BY nivelRequerido, nombre")
    fun observarMuestras(): Flow<List<MuestraEntity>>

    @Query("SELECT * FROM muestra WHERE id = :id")
    suspend fun muestra(id: String): MuestraEntity?

    @Query("SELECT * FROM rasgo_muestra")
    suspend fun rasgos(): List<RasgoMuestraEntity>

    @Query("SELECT * FROM rasgo_muestra WHERE muestraId = :muestraId")
    suspend fun rasgosDe(muestraId: String): List<RasgoMuestraEntity>

    @Query("SELECT * FROM mision ORDER BY nivel, orden")
    suspend fun misiones(): List<MisionEntity>

    @Query("SELECT * FROM mision ORDER BY nivel, orden")
    fun observarMisiones(): Flow<List<MisionEntity>>

    @Query("SELECT * FROM mision WHERE id = :id")
    suspend fun mision(id: String): MisionEntity?
}
