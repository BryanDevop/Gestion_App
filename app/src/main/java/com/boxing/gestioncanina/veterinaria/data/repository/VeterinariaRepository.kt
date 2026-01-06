package com.boxing.gestioncanina.veterinaria.data.repository

import com.boxing.gestioncanina.veterinaria.data.local.dao.ConsultaVeterinariaDao
import com.boxing.gestioncanina.veterinaria.data.local.dao.MascotaDao
import com.boxing.gestioncanina.veterinaria.data.model.ConsultaVeterinaria
import com.boxing.gestioncanina.veterinaria.data.model.Mascota
import com.boxing.gestioncanina.veterinaria.data.model.MascotaConConsultas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Repositorio que actúa como fuente única de verdad para los datos de veterinaria.
 *
 * Implementa el patrón Repository para abstraer el acceso a datos,
 * permitiendo cambiar la implementación sin afectar la capa de UI.
 *
 * @property mascotaDao DAO para operaciones de mascotas
 * @property consultaDao DAO para operaciones de consultas
 */
class VeterinariaRepository(
    private val mascotaDao: MascotaDao,
    private val consultaDao: ConsultaVeterinariaDao
) {

    // ==================== OPERACIONES DE MASCOTA ====================

    /**
     * Obtiene todas las mascotas activas como Flow.
     * Los cambios en la BD se reflejan automáticamente.
     */
    fun getAllMascotas(): Flow<List<Mascota>> {
        return mascotaDao.getAllMascotasActivas().map { entities ->
            entities.map { Mascota.fromEntity(it) }
        }
    }

    /**
     * Obtiene una mascota específica por ID.
     */
    fun getMascotaById(mascotaId: Long): Flow<Mascota?> {
        return mascotaDao.getMascotaById(mascotaId).map { entity ->
            entity?.let { Mascota.fromEntity(it) }
        }
    }

    /**
     * Busca mascotas por nombre.
     */
    fun searchMascotasByNombre(query: String): Flow<List<Mascota>> {
        return mascotaDao.searchMascotasByNombre(query).map { entities ->
            entities.map { Mascota.fromEntity(it) }
        }
    }

    /**
     * Obtiene mascotas filtradas por especie.
     */
    fun getMascotasByEspecie(especie: String): Flow<List<Mascota>> {
        return mascotaDao.getMascotasByEspecie(especie).map { entities ->
            entities.map { Mascota.fromEntity(it) }
        }
    }

    /**
     * Inserta o actualiza una mascota.
     * @return ID de la mascota
     */
    suspend fun insertMascota(mascota: Mascota): Long {
        return mascotaDao.insertMascota(mascota.toEntity())
    }

    /**
     * Actualiza una mascota existente.
     */
    suspend fun updateMascota(mascota: Mascota) {
        mascotaDao.updateMascota(mascota.toEntity())
    }

    /**
     * Elimina una mascota (también eliminará sus consultas por CASCADE).
     */
    suspend fun deleteMascota(mascota: Mascota) {
        mascotaDao.deleteMascota(mascota.toEntity())
    }

    /**
     * Desactiva una mascota (soft delete).
     */
    suspend fun deactivateMascota(mascotaId: Long) {
        mascotaDao.deactivateMascota(mascotaId)
    }

    /**
     * Obtiene el conteo total de mascotas.
     */
    fun getMascotasCount(): Flow<Int> {
        return mascotaDao.getMascotasCount()
    }

    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * Obtiene todas las consultas de una mascota.
     */
    fun getConsultasByMascotaId(mascotaId: Long): Flow<List<ConsultaVeterinaria>> {
        return consultaDao.getConsultasByMascotaId(mascotaId).map { entities ->
            entities.map { ConsultaVeterinaria.fromEntity(it) }
        }
    }

    /**
     * Obtiene una consulta específica por ID.
     */
    fun getConsultaById(consultaId: Long): Flow<ConsultaVeterinaria?> {
        return consultaDao.getConsultaById(consultaId).map { entity ->
            entity?.let { ConsultaVeterinaria.fromEntity(it) }
        }
    }

    /**
     * Inserta una nueva consulta.
     * @return ID de la consulta
     */
    suspend fun insertConsulta(consulta: ConsultaVeterinaria): Long {
        return consultaDao.insertConsulta(consulta.toEntity())
    }

    /**
     * Actualiza una consulta existente.
     */
    suspend fun updateConsulta(consulta: ConsultaVeterinaria) {
        consultaDao.updateConsulta(consulta.toEntity())
    }

    /**
     * Elimina una consulta.
     */
    suspend fun deleteConsulta(consulta: ConsultaVeterinaria) {
        consultaDao.deleteConsulta(consulta.toEntity())
    }

    /**
     * Obtiene la última consulta de una mascota.
     */
    fun getUltimaConsulta(mascotaId: Long): Flow<ConsultaVeterinaria?> {
        return consultaDao.getUltimaConsulta(mascotaId).map { entity ->
            entity?.let { ConsultaVeterinaria.fromEntity(it) }
        }
    }

    /**
     * Obtiene el conteo de consultas de una mascota.
     */
    fun getConsultasCount(mascotaId: Long): Flow<Int> {
        return consultaDao.getConsultasCount(mascotaId)
    }

    // ==================== OPERACIONES COMBINADAS ====================

    /**
     * Obtiene una mascota con todas sus consultas.
     * Combina dos Flows en uno solo.
     */
    fun getMascotaConConsultas(mascotaId: Long): Flow<MascotaConConsultas?> {
        return combine(
            getMascotaById(mascotaId),
            getConsultasByMascotaId(mascotaId)
        ) { mascota, consultas ->
            mascota?.let {
                MascotaConConsultas(it, consultas)
            }
        }
    }

    /**
     * Obtiene todas las consultas con próxima cita programada.
     */
    fun getConsultasConProximaCita(): Flow<List<ConsultaVeterinaria>> {
        return consultaDao.getConsultasConProximaCita().map { entities ->
            entities.map { ConsultaVeterinaria.fromEntity(it) }
        }
    }
}