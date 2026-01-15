package com.boxing.gestioncanina.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.model.Pet
import com.google.android.material.chip.Chip

/**
 * Adapter específico para la pantalla MyPetsInfo
 * Muestra las mascotas en un grid de 2 columnas con diseño profesional
 */
class MyPetsAdapter(
    private var pets: List<Pet> = emptyList(),
    private val onPetClick: (Pet) -> Unit
) : RecyclerView.Adapter<MyPetsAdapter.PetViewHolder>() {

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPetImage: ImageView = itemView.findViewById(R.id.ivPetImage)
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetBreed: TextView = itemView.findViewById(R.id.tvPetBreed)
        private val chipAge: Chip = itemView.findViewById(R.id.chipAge)

        fun bind(pet: Pet) {
            tvPetName.text = pet.name
            tvPetBreed.text = pet.breed
            chipAge.text = if (pet.age == 1) "${pet.age} año" else "${pet.age} años"

            // Cargar imagen con Glide
            if (!pet.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .error(R.drawable.ic_pet_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivPetImage)
            } else {
                // Imagen por defecto
                ivPetImage.setImageResource(R.drawable.ic_pet_placeholder)
            }

            // Click listener
            itemView.setOnClickListener {
                onPetClick(pet)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(pets[position])
    }

    override fun getItemCount(): Int = pets.size

    fun updatePets(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged()
    }
}