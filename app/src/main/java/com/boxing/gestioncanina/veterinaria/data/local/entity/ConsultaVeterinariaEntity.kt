package com.boxing.gestioncanina.veterinaria.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa una consulta veterinaria en la base de datos.
 *
 * Utiliza ForeignKey para mantener integridad referencial con la tabla mascotas.
 * Si se elimina una mascota, se eliminan automáticamente sus consultas (CASCADE).
 *
 * @property id Identificador único de la consulta
 * @property mascotaId ID de la mascota asociada (Foreign Key)
 * @property fecha Timestamp de la fecha de la consulta
 * @property motivo Motivo de la consulta
 * @property sintomas Síntomas presentados por la mascota
 * @property diagnostico Diagnóstico del veterinario
 * @property tratamiento Tratamiento prescrito
 * @property medicamentos Medicamentos recetados
 * @property proximaCita Timestamp de la próxima cita (opcional)
 * @property observaciones Observaciones adicionales
 * @property veterinario Nombre del veterinario que atendió
 * @property costo Costo de la consulta
 */
@Entity(
    tableName = "consultas_veterinarias",
    foreignKeys = [
        ForeignKey(
            entity = MascotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mascotaId"])]
)
data class ConsultaVeterinariaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mascotaId: Long,
    val fecha: Long = System.currentTimeMillis(),

    // Información de la consulta
    val motivo: String,
    val sintomas: String,
    val diagnostico: String,
    val tratamiento: String,
    val medicamentos: String,
    val proximaCita: Long? = null,
    val observaciones: String = "",

    // Información del veterinario
    val veterinario: String,
    val costo: Double = 0.0
)