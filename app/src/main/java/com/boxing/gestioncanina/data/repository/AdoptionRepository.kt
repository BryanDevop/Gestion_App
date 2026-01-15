package com.boxing.gestioncanina.data.repository

import com.boxing.gestioncanina.data.models.AdoptionPetUI
import com.boxing.gestioncanina.data.network.Supabase
import io.github.jan.supabase.postgrest.from

class AdoptionRepository {

    suspend fun getAvailablePets(): List<AdoptionPetUI> {
        return try {
            Supabase.client
                .from("adoption_pets")
                .select()
                .decodeList<AdoptionPetUI>()
                .filter { !it.is_adopted }
        } catch (e: Exception) {
            throw Exception("Error al obtener mascotas: ${e.message}")
        }
    }

    suspend fun getPetById(id: String): AdoptionPetUI? {
        return try {
            Supabase.client
                .from("adoption_pets")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<AdoptionPetUI>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun markAsAdopted(petId: String): Boolean {
        return try {
            Supabase.client
                .from("adoption_pets")
                .update({
                    set("is_adopted", true)
                }) {
                    filter {
                        eq("id", petId)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }
}