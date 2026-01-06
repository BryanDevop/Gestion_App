package com.boxing.gestioncanina.veterinaria.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una mascota en la base de datos Room.
 *
 * @property id Identificador único de la mascota (auto-generado)
 * @property nombre Nombre de la mascota
 * @property especie Especie (perro, gato, ave, etc.)
 * @property raza Raza específica de la mascota
 * @property edad Edad en años
 * @property peso Peso en kilogramos
 * @property sexo Sexo de la mascota (M/F)
 * @property color Color o colores del pelaje
 * @property foto URI de la foto de la mascota (opcional)
 * @property nombreDueno Nombre del dueño de la mascota
 * @property telefonoDueno Teléfono de contacto del dueño
 * @property fechaRegistro Timestamp de cuando se registró la mascota
 * @property activa Indica si la mascota está activa en el sistema
 */
@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int,
    val peso: Double,
    val sexo: String,
    val color: String,
    val foto: String? = null,

    // Datos del dueño
    val nombreDueno: String,
    val telefonoDueno: String,

    // Metadata
    val fechaRegistro: Long = System.currentTimeMillis(),
    val activa: Boolean = true
)