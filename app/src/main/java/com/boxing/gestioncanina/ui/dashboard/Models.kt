package com.boxing.gestioncanina.ui.dashboard

data class Pet(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val type: String, // "dog", "cat", etc.
    val breed: String? = null
)

// Modelo para las mascotas en adopción
data class AdoptionPet(
    val id: String,
    val name: String,
    val breed: String,
    val imageUrl: String?,
    val age: Int? = null,
    val description: String? = null
)