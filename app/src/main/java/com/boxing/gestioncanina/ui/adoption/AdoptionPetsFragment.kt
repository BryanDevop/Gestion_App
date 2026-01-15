package com.boxing.gestioncanina.ui.adoption

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.models.AdoptionPetUI
import com.bumptech.glide.Glide

class AdoptionPetsAdapter(
    private val onPetClick: (AdoptionPetUI) -> Unit
) : ListAdapter<AdoptionPetUI, AdoptionPetsAdapter.PetViewHolder>(PetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adoption_card, parent, false)
        return PetViewHolder(view, onPetClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(
        itemView: View,
        private val onPetClick: (AdoptionPetUI) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val petImage: ImageView = itemView.findViewById(R.id.petImage)
        private val petName: TextView = itemView.findViewById(R.id.petName)
        private val petBreed: TextView = itemView.findViewById(R.id.petBreed)
        private val petAge: TextView = itemView.findViewById(R.id.petAge)

        fun bind(pet: AdoptionPetUI) {
            petName.text = pet.name
            petBreed.text = pet.breed
            petAge.text = "${pet.age} año${if (pet.age != 1) "s" else ""}"

            // Cargar imagen con Glide
            if (!pet.image_url.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pet.image_url)
                    .centerCrop()
                    .placeholder(R.drawable.logo_png)
                    .error(R.drawable.logo_png)
                    .into(petImage)
            } else {
                petImage.setImageResource(R.drawable.logo_png)
            }

            itemView.setOnClickListener {
                onPetClick(pet)
            }
        }
    }

    private class PetDiffCallback : DiffUtil.ItemCallback<AdoptionPetUI>() {
        override fun areItemsTheSame(oldItem: AdoptionPetUI, newItem: AdoptionPetUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AdoptionPetUI, newItem: AdoptionPetUI): Boolean {
            return oldItem == newItem
        }
    }
}