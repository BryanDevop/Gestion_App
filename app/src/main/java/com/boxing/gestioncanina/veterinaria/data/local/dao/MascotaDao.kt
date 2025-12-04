package com.boxing.gestioncanina.veterinaria.data.local.dao

import androidx.room.*
import com.boxing.gestioncanina.veterinaria.data.local.entity.MascotaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones de base de datos de mascotas.
 *
 * Utiliza Flow para observar cambios en tiempo real y Suspend functions
 * para operaciones asíncronas con coroutines.
 */
@Dao
interface MascotaDao {

    /**
     * Inserta una nueva mascota en la base de datos.
     * @return ID de la mascota insertada
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMascota(mascota: MascotaEntity): Long

    /**
     * Actualiza los datos de una mascota existente.
     */
    @Update
    suspend fun updateMascota(mascota: MascotaEntity)

    /**
     * Elimina una mascota de la base de datos.
     * Esto también eliminará todas sus consultas (CASCADE).
     */
    @Delete
    suspend fun deleteMascota(mascota: MascotaEntity)

    /**
     * Obtiene todas las mascotas activas ordenadas por fecha de registro.
     * Retorna un Flow para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM mascotas WHERE activa = 1 ORDER BY fechaRegistro DESC")
    fun getAllMascotasActivas(): Flow<List<MascotaEntity>>

    /**
     * Obtiene una mascota por su ID.
     */
    @Query("SELECT * FROM mascotas WHERE id = :mascotaId")
    fun getMascotaById(mascotaId: Long): Flow<MascotaEntity?>

    /**
     * Busca mascotas por nombre (búsqueda parcial, case-insensitive).
     */
    @Query("SELECT * FROM mascotas WHERE activa = 1 AND nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchMascotasByNombre(query: String): Flow<List<MascotaEntity>>

    /**
     * Obtiene mascotas filtradas por especie.
     */
    @Query("SELECT * FROM mascotas WHERE activa = 1 AND especie = :especie ORDER BY nombre ASC")
    fun getMascotasByEspecie(especie: String): Flow<List<MascotaEntity>>

    /**
     * Marca una mascota como inactiva (soft delete).
     */
    @Query("UPDATE mascotas SET activa = 0 WHERE id = :mascotaId")
    suspend fun deactivateMascota(mascotaId: Long)

    /**
     * Obtiene el conteo total de mascotas activas.
     */
    @Query("SELECT COUNT(*) FROM mascotas WHERE activa = 1")
    fun getMascotasCount(): Flow<Int>

    /**
     * Obtiene mascotas por nombre del dueño.
     */
    @Query("SELECT * FROM mascotas WHERE activa = 1 AND nombreDueno LIKE '%' || :nombreDueno || '%'")
    fun getMascotasByDueno(nombreDueno: String): Flow<List<MascotaEntity>>
}