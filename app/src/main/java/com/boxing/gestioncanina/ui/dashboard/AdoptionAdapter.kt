package com.boxing.gestioncanina.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.models.AdoptionPet
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
        // IDs que coinciden con tu XML
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

            // Click en el botón de adoptar
            btnAdopt.setOnClickListener {
                onAdoptClick(pet)
            }

            // También permitir click en toda la tarjeta
            itemView.setOnClickListener {
                onAdoptClick(pet)
            }
        }
    }

    // Método para actualizar la lista
    fun updatePets(newPets: List<AdoptionPet>) {
        pets.clear()
        pets.addAll(newPets)
        notifyDataSetChanged()
    }
}