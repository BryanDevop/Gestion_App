package com.boxing.gestioncanina.veterinaria.data.local.dao

import androidx.room.*
import com.boxing.gestioncanina.veterinaria.data.local.entity.ConsultaVeterinariaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de base de datos de consultas veterinarias.
 */
@Dao
interface ConsultaVeterinariaDao {

    /**
     * Inserta una nueva consulta veterinaria.
     * @return ID de la consulta insertada
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(consulta: ConsultaVeterinariaEntity): Long

    /**
     * Actualiza una consulta veterinaria existente.
     */
    @Update
    suspend fun updateConsulta(consulta: ConsultaVeterinariaEntity)

    /**
     * Elimina una consulta veterinaria.
     */
    @Delete
    suspend fun deleteConsulta(consulta: ConsultaVeterinariaEntity)

    /**
     * Obtiene todas las consultas de una mascota específica,
     * ordenadas por fecha descendente (más reciente primero).
     */
    @Query("SELECT * FROM consultas_veterinarias WHERE mascotaId = :mascotaId ORDER BY fecha DESC")
    fun getConsultasByMascotaId(mascotaId: Long): Flow<List<ConsultaVeterinariaEntity>>

    /**
     * Obtiene una consulta específica por su ID.
     */
    @Query("SELECT * FROM consultas_veterinarias WHERE id = :consultaId")
    fun getConsultaById(consultaId: Long): Flow<ConsultaVeterinariaEntity?>

    /**
     * Obtiene la última consulta de una mascota.
     */
    @Query("SELECT * FROM consultas_veterinarias WHERE mascotaId = :mascotaId ORDER BY fecha DESC LIMIT 1")
    fun getUltimaConsulta(mascotaId: Long): Flow<ConsultaVeterinariaEntity?>

    /**
     * Obtiene todas las consultas en un rango de fechas.
     */
    @Query("SELECT * FROM consultas_veterinarias WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getConsultasByFechaRange(fechaInicio: Long, fechaFin: Long): Flow<List<ConsultaVeterinariaEntity>>

    /**
     * Obtiene el conteo de consultas de una mascota.
     */
    @Query("SELECT COUNT(*) FROM consultas_veterinarias WHERE mascotaId = :mascotaId")
    fun getConsultasCount(mascotaId: Long): Flow<Int>

    /**
     * Obtiene consultas con próxima cita programada.
     */
    @Query("SELECT * FROM consultas_veterinarias WHERE proximaCita IS NOT NULL AND proximaCita > :fechaActual ORDER BY proximaCita ASC")
    fun getConsultasConProximaCita(fechaActual: Long = System.currentTimeMillis()): Flow<List<ConsultaVeterinariaEntity>>

    /**
     * Elimina todas las consultas de una mascota específica.
     */
    @Query("DELETE FROM consultas_veterinarias WHERE mascotaId = :mascotaId")
    suspend fun deleteConsultasByMascotaId(mascotaId: Long)
}