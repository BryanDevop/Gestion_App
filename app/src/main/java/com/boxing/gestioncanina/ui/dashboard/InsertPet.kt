package com.boxing.gestioncanina.ui.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class InsertPet(
    val user_id: String,
    val name: String,
    val breed: String,
    val age: Int,
    val image_url: String? = null
)
