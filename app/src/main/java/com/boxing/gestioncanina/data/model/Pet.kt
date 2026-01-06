package com.boxing.gestioncanina.data.model
import kotlinx.serialization.Serializable
// data class Pet()
@Serializable
data class Pet(
    val id: String? = null,
    val name: String,
    val breed: String,
    val age: Int,
    val imageUrl: String? = null
)