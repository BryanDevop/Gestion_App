package com.boxing.gestioncanina.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R

class PetsAdapter(
    private val pets: MutableList<Pet>,
    private val onAddClick: () -> Unit,
    private val onPetClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petCircleCard: CardView = view.findViewById(R.id.petCircleCard)
        val petImage: ImageView = view.findViewById(R.id.petImage)
        val addIcon: ImageView = view.findViewById(R.id.addIcon)
        val petName: TextView = view.findViewById(R.id.petName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_pet_circle, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val isAddButton = position == pets.size

        if (isAddButton) {
            // Mostrar botón de agregar
            holder.petImage.visibility = View.GONE
            holder.addIcon.visibility = View.VISIBLE
            holder.petName.text = "Agregar"
            holder.petCircleCard.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.purple_light)
            )

            holder.itemView.setOnClickListener {
                onAddClick()
            }
        } else {
            // Mostrar mascota
            val pet = pets[position]
            holder.petImage.visibility = View.VISIBLE
            holder.addIcon.visibility = View.GONE
            holder.petName.text = pet.name
            holder.petCircleCard.setCardBackgroundColor(
                holder.itemView.context.getColor(R.color.white)
            )

            // Cargar imagen de la mascota (usa tu librería preferida: Glide, Picasso, etc.)
            // Si no tienes imagen, muestra el placeholder
            if (pet.imageUrl != null) {
                // Glide.with(holder.itemView.context).load(pet.imageUrl).into(holder.petImage)
            } else {
                holder.petImage.setImageResource(R.drawable.ic_pet_placeholder)
            }

            holder.itemView.setOnClickListener {
                onPetClick(pet)
            }
        }
    }

    override fun getItemCount(): Int = pets.size + 1 // +1 para el botón de agregar

    fun addPet(pet: Pet) {
        pets.add(pet)
        notifyItemInserted(pets.size - 1)
    }
}