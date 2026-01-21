package com.boxing.gestioncanina.data.models

data class AdoptionPet(
    val id: String,
    val name: String,
    val breed: String,
    val imageUrl: String,
    val age: Int,
    val description: String? = null,

    // ⬇️⬇️⬇️ NUEVOS CAMPOS ⬇️⬇️⬇️
    val gender: String = "No especificado",
    val weight: Double? = null,
    val location: String = "Santo Domingo, República Dominicana",
    val shelterName: String = "Refugio Patitas Felices",
    val shelterPhone: String? = null
)