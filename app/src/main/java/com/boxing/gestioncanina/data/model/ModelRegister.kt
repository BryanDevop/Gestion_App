package com.boxing.gestioncanina.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("location")
    val location: String,

    @SerialName("created_at")
    val createdAt: Long
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val location: String,
    val password: String
)

data class RegisterResult(
    val success: Boolean,
    val message: String,
    val user: User? = null
)