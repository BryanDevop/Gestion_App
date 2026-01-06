package com.boxing.gestioncanina.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelo para mapear la tabla "adoption_pets" de Supabase
 * Este modelo debe coincidir EXACTAMENTE con la estructura de tu tabla en Supabase
 */
@Serializable
data class AdoptionPetUI(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("breed")
    val breed: String,

    @SerialName("age")
    val age: Int,

    @SerialName("gender")
    val gender: String? = null,

    @SerialName("size")
    val size: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("image_url")
    val image_url: String? = null,

    @SerialName("is_adopted")
    val is_adopted: Boolean = false,

    @SerialName("adoption_date")
    val adoption_date: String? = null,

    @SerialName("adopted_by")
    val adopted_by: String? = null,

    @SerialName("shelter_name")
    val shelter_name: String? = null,

    @SerialName("contact_phone")
    val contact_phone: String? = null,

    @SerialName("contact_email")
    val contact_email: String? = null,

    @SerialName("location")
    val location: String? = null,

    @SerialName("created_at")
    val created_at: String? = null,

    @SerialName("updated_at")
    val updated_at: String? = null
)