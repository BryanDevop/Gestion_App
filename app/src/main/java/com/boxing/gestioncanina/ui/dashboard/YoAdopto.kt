// Al final del archivo DashboardActivity.kt o en un archivo separado Models.kt

import kotlinx.serialization.Serializable

@Serializable
data class AdoptionPetSupabase(
    val id: String,
    val name: String,
    val breed: String,
    val image_url: String? = null,
    val age: Int,
    val description: String? = null,
    val species: String? = null, // "dog", "cat", etc.
    val gender: String? = null,
    val size: String? = null,
    val is_adopted: Boolean = false,
    val owner_id: String? = null,
    val created_at: String? = null
)