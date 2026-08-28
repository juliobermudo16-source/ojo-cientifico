package com.ojocientifico.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ojocientifico.app.data.local.dao.CatalogoDao
import com.ojocientifico.app.data.local.dao.ProgresoDao
import com.ojocientifico.app.data.local.dao.RegistroDao
import com.ojocientifico.app.data.local.entity.ConfiguracionEntity
import com.ojocientifico.app.data.local.entity.DescubrimientoEntity
import com.ojocientifico.app.data.local.entity.DiaActividadEntity
import com.ojocientifico.app.data.local.entity.FichaEntity
import com.ojocientifico.app.data.local.entity.HistorialEntity
import com.ojocientifico.app.data.local.entity.InsigniaEntity
import com.ojocientifico.app.data.local.entity.MisionEntity
import com.ojocientifico.app.data.local.entity.MuestraEntity
import com.ojocientifico.app.data.local.entity.ObservacionEntity
import com.ojocientifico.app.data.local.entity.OpcionRasgoEntity
import com.ojocientifico.app.data.local.entity.PerfilEntity
import com.ojocientifico.app.data.local.entity.ProgresoMisionEntity
import com.ojocientifico.app.data.local.entity.RasgoFalladoEntity
import com.ojocientifico.app.data.local.entity.RasgoMuestraEntity

/**
 * Base de datos local de Ojo Científico.
 * Todo el progreso vive aquí: la aplicación no envía ni recibe nada por red.
 */
@Database(
    entities = [
        OpcionRasgoEntity::class,
        MuestraEntity::class,
        RasgoMuestraEntity::class,
        MisionEntity::class,
        PerfilEntity::class,
        ProgresoMisionEntity::class,
        InsigniaEntity::class,
        DescubrimientoEntity::class,
        DiaActividadEntity::class,
        ConfiguracionEntity::class,
        FichaEntity::class,
        ObservacionEntity::class,
        RasgoFalladoEntity::class,
        HistorialEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class OjoDatabase : RoomDatabase() {

    abstract fun catalogoDao(): CatalogoDao
    abstract fun progresoDao(): ProgresoDao
    abstract fun registroDao(): RegistroDao

    companion object {
        const val NOMBRE = "ojo_cientifico.db"

        @Volatile
        private var instancia: OjoDatabase? = null

        fun obtener(context: Context): OjoDatabase =
            instancia ?: synchronized(this) {
                instancia ?: construir(context.applicationContext).also { instancia = it }
            }

        private fun construir(context: Context): OjoDatabase =
            Room.databaseBuilder(context, OjoDatabase::class.java, NOMBRE)
                // La base solo contiene contenido sembrado y progreso local:
                // si cambia el esquema se regenera sin bloquear al usuario.
                .fallbackToDestructiveMigration()
                .build()
    }
}
