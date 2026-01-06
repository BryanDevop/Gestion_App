package com.boxing.gestioncanina.veterinaria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.boxing.gestioncanina.veterinaria.data.local.dao.ConsultaVeterinariaDao
import com.boxing.gestioncanina.veterinaria.data.local.dao.MascotaDao
import com.boxing.gestioncanina.veterinaria.data.local.entity.ConsultaVeterinariaEntity
import com.boxing.gestioncanina.veterinaria.data.local.entity.MascotaEntity

/**
 * Base de datos Room para el módulo de veterinaria.
 *
 * Implementa el patrón Singleton para garantizar una única instancia
 * de la base de datos en toda la aplicación.
 *
 * @property entities Lista de entidades que componen la base de datos
 * @property version Versión de la base de datos (incrementar en migraciones)
 * @property exportSchema Exportar esquema para control de versiones
 */
@Database(
    entities = [
        MascotaEntity::class,
        ConsultaVeterinariaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VeterinariaDatabase : RoomDatabase() {

    // DAOs abstractos que Room implementará automáticamente
    abstract fun mascotaDao(): MascotaDao
    abstract fun consultaVeterinariaDao(): ConsultaVeterinariaDao

    companion object {
        // Volatile asegura que el valor de INSTANCE sea visible
        // inmediatamente para todos los threads
        @Volatile
        private var INSTANCE: VeterinariaDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos.
         * Utiliza double-checked locking para thread safety.
         *
         * @param context Contexto de la aplicación
         * @return Instancia de VeterinariaDatabase
         */
        fun getDatabase(context: Context): VeterinariaDatabase {
            // Si INSTANCE no es null, retornarla directamente
            return INSTANCE ?: synchronized(this) {
                // Verificación doble dentro del bloque sincronizado
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VeterinariaDatabase::class.java,
                    "veterinaria_database"
                )
                    // En producción, deberías implementar migraciones
                    // .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Solo para desarrollo
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Método para testing: permite limpiar la instancia.
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}