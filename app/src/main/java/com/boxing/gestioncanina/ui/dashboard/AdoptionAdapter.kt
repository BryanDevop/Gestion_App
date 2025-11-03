package com.boxing.gestioncanina.ui.dashboard



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R

class AdoptionAdapter(
    private val pets: List<AdoptionPet>,
    private val onAdoptClick: (AdoptionPet) -> Unit
) : RecyclerView.Adapter<AdoptionAdapter.AdoptionViewHolder>() {

    class AdoptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petImageAdoption: ImageView = view.findViewById(R.id.petImageAdoption)
        val adoptButton: CardView = view.findViewById(R.id.adoptButton)
        val petNameAdoption: TextView = view.findViewById(R.id.petNameAdoption)
        val petBreed: TextView = view.findViewById(R.id.petBreed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdoptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_adoption_card, parent, false)
        return AdoptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdoptionViewHolder, position: Int) {
        val pet = pets[position]

        holder.petNameAdoption.text = pet.name
        holder.petBreed.text = pet.breed

        // Cargar imagen de la mascota
        if (pet.imageUrl != null) {
            // Glide.with(holder.itemView.context).load(pet.imageUrl).into(holder.petImageAdoption)
        } else {
            holder.petImageAdoption.setImageResource(R.drawable.ic_dog_placeholder)
        }

        holder.adoptButton.setOnClickListener {
            onAdoptClick(pet)
        }

        holder.itemView.setOnClickListener {
            onAdoptClick(pet)
        }
    }

    override fun getItemCount(): Int = pets.size
}