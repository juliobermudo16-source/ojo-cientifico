package com.ojocientifico.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ojocientifico.app.data.local.entity.ConfiguracionEntity
import com.ojocientifico.app.data.local.entity.DescubrimientoEntity
import com.ojocientifico.app.data.local.entity.DiaActividadEntity
import com.ojocientifico.app.data.local.entity.InsigniaEntity
import com.ojocientifico.app.data.local.entity.PerfilEntity
import com.ojocientifico.app.data.local.entity.ProgresoMisionEntity
import kotlinx.coroutines.flow.Flow

/** Progreso persistente del explorador: XP, misiones, insignias y colección. */
@Dao
interface ProgresoDao {

    // ------------------------------ Perfil ------------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crearPerfil(perfil: PerfilEntity)

    @Update
    suspend fun actualizarPerfil(perfil: PerfilEntity)

    @Query("SELECT * FROM perfil WHERE id = 1")
    suspend fun perfil(): PerfilEntity?

    @Query("SELECT * FROM perfil WHERE id = 1")
    fun observarPerfil(): Flow<PerfilEntity?>

    @Query("UPDATE perfil SET xp = xp + :cantidad WHERE id = 1")
    suspend fun sumarXp(cantidad: Int)

    @Query("UPDATE perfil SET alias = :alias, avatar = :avatar WHERE id = 1")
    suspend fun renombrar(alias: String, avatar: Int)

    // --------------------------- Configuración ---------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crearConfiguracion(configuracion: ConfiguracionEntity)

    @Update
    suspend fun actualizarConfiguracion(configuracion: ConfiguracionEntity)

    @Query("SELECT * FROM configuracion WHERE id = 1")
    suspend fun configuracion(): ConfiguracionEntity?

    @Query("SELECT * FROM configuracion WHERE id = 1")
    fun observarConfiguracion(): Flow<ConfiguracionEntity?>

    // ------------------------- Progreso de misión -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarProgresoMision(progreso: ProgresoMisionEntity)

    @Query("SELECT * FROM progreso_mision WHERE misionId = :misionId")
    suspend fun progresoMision(misionId: String): ProgresoMisionEntity?

    @Query("SELECT * FROM progreso_mision")
    fun observarProgresoMisiones(): Flow<List<ProgresoMisionEntity>>

    @Query("SELECT * FROM progreso_mision")
    suspend fun progresoMisiones(): List<ProgresoMisionEntity>

    @Query("SELECT COUNT(*) FROM progreso_mision WHERE completada = 1")
    suspend fun contarMisionesCompletadas(): Int

    @Query("SELECT COUNT(*) FROM progreso_mision WHERE mejorEstrellas >= 3")
    suspend fun contarMisionesPerfectas(): Int

    /**
     * Registra un intento conservando siempre el mejor resultado histórico.
     * Repetir una misión nunca empeora lo ya conseguido.
     */
    @Transaction
    suspend fun registrarIntento(
        misionId: String,
        completada: Boolean,
        estrellas: Int,
        precision: Float,
        fechaMillis: Long
    ) {
        val previo = progresoMision(misionId)
        guardarProgresoMision(
            ProgresoMisionEntity(
                misionId = misionId,
                completada = previo?.completada == true || completada,
                mejorEstrellas = maxOf(previo?.mejorEstrellas ?: 0, estrellas),
                mejorPrecision = maxOf(previo?.mejorPrecision ?: 0f, precision),
                intentos = (previo?.intentos ?: 0) + 1,
                ultimaFechaMillis = fechaMillis
            )
        )
    }

    // ----------------------------- Insignias -----------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun sembrarInsignias(insignias: List<InsigniaEntity>)

    @Query("UPDATE insignia SET desbloqueada = 1, fechaMillis = :fechaMillis WHERE id = :id AND desbloqueada = 0")
    suspend fun desbloquearInsignia(id: String, fechaMillis: Long)

    @Query("SELECT * FROM insignia")
    fun observarInsignias(): Flow<List<InsigniaEntity>>

    @Query("SELECT id FROM insignia WHERE desbloqueada = 1")
    suspend fun insigniasDesbloqueadas(): List<String>

    // --------------------------- Descubrimientos ---------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun sembrarDescubrimientos(descubrimientos: List<DescubrimientoEntity>)

    @Query("UPDATE descubrimiento SET desbloqueado = 1, fechaMillis = :fechaMillis WHERE muestraId = :muestraId AND desbloqueado = 0")
    suspend fun desbloquearDescubrimiento(muestraId: String, fechaMillis: Long)

    @Query("SELECT * FROM descubrimiento")
    fun observarDescubrimientos(): Flow<List<DescubrimientoEntity>>

    @Query("SELECT muestraId FROM descubrimiento WHERE desbloqueado = 1")
    suspend fun descubrimientosDesbloqueados(): List<String>

    @Query("SELECT COUNT(*) FROM descubrimiento WHERE desbloqueado = 1")
    suspend fun contarDescubrimientos(): Int

    // ------------------------- Días de expedición -------------------------

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun marcarDia(dia: DiaActividadEntity)

    @Query("SELECT COUNT(*) FROM dia_actividad")
    suspend fun contarDias(): Int

    // ------------------------------ Reinicio ------------------------------

    @Query("DELETE FROM progreso_mision")
    suspend fun borrarProgresoMisiones()

    @Query("UPDATE insignia SET desbloqueada = 0, fechaMillis = NULL")
    suspend fun reiniciarInsignias()

    @Query("UPDATE descubrimiento SET desbloqueado = 0, fechaMillis = NULL")
    suspend fun reiniciarDescubrimientos()

    @Query("DELETE FROM dia_actividad")
    suspend fun borrarDias()

    @Query("UPDATE perfil SET xp = 0 WHERE id = 1")
    suspend fun reiniciarXp()
}
