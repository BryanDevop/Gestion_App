package com.boxing.gestioncanina.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cita(
    val id: String? = null,

    @SerialName("mascota_id")
    val mascotaId: String,

    @SerialName("usuario_id")
    val usuarioId: String? = null,

    @SerialName("tipo_cita")
    val tipoCita: String,

    val motivo: String,

    val fecha: String,

    val hora: String,

    val veterinario: String,

    val clinica: String,

    @SerialName("recordatorio_activo")
    val recordatorioActivo: Boolean = false,

    @SerialName("tiempo_recordatorio")
    val tiempoRecordatorio: String? = null,

    val notas: String? = null,

    val estado: String = "Programada", // Programada, Completada, Cancelada

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class Mascota(
    val id: String? = null,

    @SerialName("usuario_id")
    val usuarioId: String? = null,

    val nombre: String,

    val especie: String,

    val raza: String? = null,

    val sexo: String? = null,

    @SerialName("fecha_nacimiento")
    val fechaNacimiento: String? = null,

    val peso: Float? = null,

    val color: String? = null,

    @SerialName("foto_url")
    val fotoUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)