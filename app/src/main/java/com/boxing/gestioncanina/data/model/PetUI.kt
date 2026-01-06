package com.boxing.gestioncanina.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// Modelo para Supabase (debe coincidir con tu tabla)
@Serializable
data class PetSupabase(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val user_id: String,

    @SerialName("name")
    val name: String,

    @SerialName("breed")
    val breed: String,

    @SerialName("age")
    val age: Int?,              // 👈 CLAVE

    @SerialName("image_url")
    val image_url: String? = null,

    @SerialName("created_at")
    val created_at: String? = null
)
