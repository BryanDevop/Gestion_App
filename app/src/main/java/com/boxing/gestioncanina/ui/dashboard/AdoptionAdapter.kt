package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.models.AdoptionPet  // ⬅️ ASEGÚRATE DE IMPORTAR DESDE data.models
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class AdoptionAdapter(
    private var pets: MutableList<AdoptionPet>,
    private val onAdoptClick: (AdoptionPet) -> Unit
) : RecyclerView.Adapter<AdoptionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adoption_pet_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount(): Int = pets.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val petImage: ImageView = itemView.findViewById(R.id.ivAnimalPhoto)
        private val petName: TextView = itemView.findViewById(R.id.tvAnimalName)
        private val petBreed: TextView = itemView.findViewById(R.id.tvAnimalBreed)
        private val btnAdopt: MaterialButton = itemView.findViewById(R.id.btnAdopt)

        fun bind(pet: AdoptionPet) {
            petName.text = pet.name
            petBreed.text = pet.breed

            // Cargar imagen con Glide
            Glide.with(itemView.context)
                .load(pet.imageUrl)
                .placeholder(R.drawable.ic_pet_placeholder)
                .error(R.drawable.ic_pet_placeholder)
                .centerCrop()
                .into(petImage)

            // ⬇️⬇️⬇️ CAMBIO PRINCIPAL: Click en botón "Detalle" ⬇️⬇️⬇️
            btnAdopt.setOnClickListener {
                val intent = Intent(itemView.context, Biography_Animal::class.java).apply {
                    putExtra("PET_ID", pet.id)
                    putExtra("PET_NAME", pet.name)
                    putExtra("PET_BREED", pet.breed)
                    putExtra("PET_AGE", pet.age)
                    putExtra("PET_IMAGE_URL", pet.imageUrl)
                    putExtra("PET_DESCRIPTION", pet.description ?: "Mascota cariñosa en busca de un hogar lleno de amor.")

                    // ⬇️⬇️⬇️ NUEVOS DATOS ⬇️⬇️⬇️
                    putExtra("PET_GENDER", pet.gender)
                    putExtra("PET_WEIGHT", pet.weight ?: 0.0)
                    putExtra("PET_LOCATION", pet.location)
                    putExtra("SHELTER_NAME", pet.shelterName)
                    putExtra("SHELTER_PHONE", pet.shelterPhone ?: "")
                }
                itemView.context.startActivity(intent)
            }

            // También actualiza el click en toda la tarjeta
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Biography_Animal::class.java).apply {
                    putExtra("PET_ID", pet.id)
                    putExtra("PET_NAME", pet.name)
                    putExtra("PET_BREED", pet.breed)
                    putExtra("PET_AGE", pet.age)
                    putExtra("PET_IMAGE_URL", pet.imageUrl)
                    putExtra("PET_DESCRIPTION", pet.description ?: "Mascota cariñosa en busca de un hogar lleno de amor.")

                    // ⬇️⬇️⬇️ NUEVOS DATOS ⬇️⬇️⬇️
                    putExtra("PET_GENDER", pet.gender)
                    putExtra("PET_WEIGHT", pet.weight ?: 0.0)
                    putExtra("PET_LOCATION", pet.location)
                    putExtra("SHELTER_NAME", pet.shelterName)
                    putExtra("SHELTER_PHONE", pet.shelterPhone ?: "")
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    fun updatePets(newPets: List<AdoptionPet>) {
        pets.clear()
        pets.addAll(newPets)
        notifyDataSetChanged()
    }
}