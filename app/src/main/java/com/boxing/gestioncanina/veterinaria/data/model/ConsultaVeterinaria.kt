package com.boxing.gestioncanina.veterinaria.data.model

import com.boxing.gestioncanina.veterinaria.data.local.entity.ConsultaVeterinariaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modelo de dominio para Consulta Veterinaria.
 */
data class ConsultaVeterinaria(
    val id: Long = 0,
    val mascotaId: Long,
    val fecha: Long = System.currentTimeMillis(),
    val motivo: String,
    val sintomas: String,
    val diagnostico: String,
    val tratamiento: String,
    val medicamentos: String,
    val proximaCita: Long? = null,
    val observaciones: String = "",
    val veterinario: String,
    val costo: Double = 0.0
) {

    /**
     * Convierte el modelo de dominio a entidad de Room.
     */
    fun toEntity(): ConsultaVeterinariaEntity {
        return ConsultaVeterinariaEntity(
            id = id,
            mascotaId = mascotaId,
            fecha = fecha,
            motivo = motivo,
            sintomas = sintomas,
            diagnostico = diagnostico,
            tratamiento = tratamiento,
            medicamentos = medicamentos,
            proximaCita = proximaCita,
            observaciones = observaciones,
            veterinario = veterinario,
            costo = costo
        )
    }

    /**
     * Obtiene la fecha de la consulta formateada.
     */
    fun getFechaFormateada(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(fecha))
    }

    /**
     * Obtiene solo la fecha sin hora.
     */
    fun getFechaSoloFecha(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(fecha))
    }

    /**
     * Obtiene la próxima cita formateada.
     */
    fun getProximaCitaFormateada(): String? {
        return proximaCita?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(it))
        }
    }

    /**
     * Verifica si tiene próxima cita programada.
     */
    fun tieneProximaCita(): Boolean {
        return proximaCita != null && proximaCita > System.currentTimeMillis()
    }

    /**
     * Obtiene el costo formateado.
     */
    fun getCostoFormateado(): String {
        return String.format(Locale.getDefault(), "$%.2f", costo)
    }

    /**
     * Valida si los datos de la consulta son correctos.
     */
    fun isValid(): Boolean {
        return mascotaId > 0 &&
                motivo.isNotBlank() &&
                sintomas.isNotBlank() &&
                diagnostico.isNotBlank() &&
                tratamiento.isNotBlank() &&
                veterinario.isNotBlank() &&
                costo >= 0
    }

    /**
     * Obtiene un resumen corto de la consulta.
     */
    fun getResumen(): String {
        return "${getFechaSoloFecha()} - $motivo"
    }

    companion object {
        /**
         * Convierte una entidad de Room a modelo de dominio.
         */
        fun fromEntity(entity: ConsultaVeterinariaEntity): ConsultaVeterinaria {
            return ConsultaVeterinaria(
                id = entity.id,
                mascotaId = entity.mascotaId,
                fecha = entity.fecha,
                motivo = entity.motivo,
                sintomas = entity.sintomas,
                diagnostico = entity.diagnostico,
                tratamiento = entity.tratamiento,
                medicamentos = entity.medicamentos,
                proximaCita = entity.proximaCita,
                observaciones = entity.observaciones,
                veterinario = entity.veterinario,
                costo = entity.costo
            )
        }

        /**
         * Lista de motivos comunes de consulta.
         */
        fun getMotivosComunes(): List<String> {
            return listOf(
                "Consulta general",
                "Vacunación",
                "Desparasitación",
                "Control de salud",
                "Emergencia",
                "Cirugía",
                "Radiografía",
                "Análisis de laboratorio",
                "Otro"
            )
        }
    }
}