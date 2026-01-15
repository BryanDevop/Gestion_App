package com.boxing.gestioncanina.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    @SerialName("image_url")
    val image_url: String? = null,

    @SerialName("is_adopted")
    val is_adopted: Boolean = false,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_at")
    val created_at: String? = null
)