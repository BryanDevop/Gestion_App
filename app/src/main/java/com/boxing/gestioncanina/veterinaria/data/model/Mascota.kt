package com.boxing.gestioncanina.veterinaria.data.model

import com.boxing.gestioncanina.veterinaria.data.local.entity.MascotaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modelo de dominio para Mascota.
 *
 * Separa la lógica de negocio de la capa de persistencia,
 * permitiendo agregar propiedades calculadas y métodos útiles.
 */
data class Mascota(
    val id: Long = 0,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int,
    val peso: Double,
    val sexo: String,
    val color: String,
    val foto: String? = null,
    val nombreDueno: String,
    val telefonoDueno: String,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val activa: Boolean = true
) {

    /**
     * Convierte el modelo de dominio a entidad de Room.
     */
    fun toEntity(): MascotaEntity {
        return MascotaEntity(
            id = id,
            nombre = nombre,
            especie = especie,
            raza = raza,
            edad = edad,
            peso = peso,
            sexo = sexo,
            color = color,
            foto = foto,
            nombreDueno = nombreDueno,
            telefonoDueno = telefonoDueno,
            fechaRegistro = fechaRegistro,
            activa = activa
        )
    }

    /**
     * Obtiene la fecha de registro formateada.
     */
    fun getFechaRegistroFormateada(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(fechaRegistro))
    }

    /**
     * Obtiene la descripción completa de la mascota.
     */
    fun getDescripcionCompleta(): String {
        return "$nombre - $especie ($raza) - $edad años"
    }

    /**
     * Obtiene el sexo formateado.
     */
    fun getSexoFormateado(): String {
        return when (sexo.uppercase()) {
            "M" -> "Macho"
            "F" -> "Hembra"
            else -> "No especificado"
        }
    }

    /**
     * Valida si los datos de la mascota son correctos.
     */
    fun isValid(): Boolean {
        return nombre.isNotBlank() &&
                especie.isNotBlank() &&
                raza.isNotBlank() &&
                edad >= 0 &&
                peso > 0 &&
                sexo.isNotBlank() &&
                nombreDueno.isNotBlank() &&
                telefonoDueno.isNotBlank()
    }

    companion object {
        /**
         * Convierte una entidad de Room a modelo de dominio.
         */
        fun fromEntity(entity: MascotaEntity): Mascota {
            return Mascota(
                id = entity.id,
                nombre = entity.nombre,
                especie = entity.especie,
                raza = entity.raza,
                edad = entity.edad,
                peso = entity.peso,
                sexo = entity.sexo,
                color = entity.color,
                foto = entity.foto,
                nombreDueno = entity.nombreDueno,
                telefonoDueno = entity.telefonoDueno,
                fechaRegistro = entity.fechaRegistro,
                activa = entity.activa
            )
        }

        /**
         * Lista de especies disponibles.
         */
        fun getEspeciesDisponibles(): List<String> {
            return listOf("Perro", "Gato", "Ave", "Conejo", "Hamster", "Reptil", "Otro")
        }
    }
}