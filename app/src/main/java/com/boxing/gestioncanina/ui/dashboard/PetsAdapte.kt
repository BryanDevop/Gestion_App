import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.data.model.Pet
import com.bumptech.glide.Glide

class PetsAdapter(
    private val pets: MutableList<Pet>,
    private val onAddClick: () -> Unit,
    private val onPetClick: (Pet) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_PET = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == pets.size) TYPE_ADD else TYPE_PET
    }

    override fun getItemCount(): Int = pets.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_pet_horizontal, parent, false)

        return if (viewType == TYPE_ADD) AddPetViewHolder(view)
        else PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PetViewHolder && position < pets.size) {
            holder.bind(pets[position])
        } else if (holder is AddPetViewHolder) {
            holder.bind()
        }
    }

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView.findViewById(R.id.petCircleCard)
        private val petImage: ImageView = itemView.findViewById(R.id.petImage)
        private val petName: TextView = itemView.findViewById(R.id.petName)
        private val addIcon: ImageView = itemView.findViewById(R.id.addIcon)

        fun bind(pet: Pet) {
            // Ocultar el ícono de agregar
            addIcon.visibility = View.GONE

            // Mostrar nombre de la mascota
            petName.text = pet.name

            // ✅ MANEJO CORRECTO DE imageUrl NULLABLE
            if (!pet.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .error(R.drawable.ic_pet_placeholder)
                    .centerCrop()
                    .into(petImage)
            } else {
                petImage.setImageResource(R.drawable.ic_pet_placeholder)
            }

            // Click en la tarjeta
            card.setOnClickListener {
                onPetClick(pet)
            }
        }
    }

    inner class AddPetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: CardView = itemView.findViewById(R.id.petCircleCard)
        private val petImage: ImageView = itemView.findViewById(R.id.petImage)
        private val addIcon: ImageView = itemView.findViewById(R.id.addIcon)
        private val petName: TextView = itemView.findViewById(R.id.petName)

        fun bind() {
            petImage.visibility = View.GONE
            addIcon.visibility = View.VISIBLE
            petName.text = "Agregar"

            card.setOnClickListener {
                onAddClick()
            }
        }
    }

    fun updatePets(newPets: List<Pet>) {
        pets.clear()
        pets.addAll(newPets)
        notifyDataSetChanged()
    }

    fun addPet(pet: Pet) {
        pets.add(pet)
        notifyItemInserted(pets.size - 1)
    }
}
