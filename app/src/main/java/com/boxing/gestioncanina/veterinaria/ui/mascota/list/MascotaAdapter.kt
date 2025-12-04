package com.boxing.gestioncanina.veterinaria.ui.mascota.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.veterinaria.data.model.Mascota
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.card.MaterialCardView

/**
 * Adapter para el RecyclerView de mascotas.
 *
 * Utiliza ListAdapter con DiffUtil para actualizaciones eficientes.
 * Implementa callbacks para manejar eventos de clic.
 */
class MascotaAdapter : ListAdapter<Mascota, MascotaAdapter.MascotaViewHolder>(MascotaDiffCallback()) {

    // Callbacks para manejar eventos
    var onItemClick: ((Mascota) -> Unit)? = null
    var onEditClick: ((Mascota) -> Unit)? = null
    var onDeleteClick: ((Mascota) -> Unit)? = null
    var onHistorialClick: ((Mascota) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardMascota: MaterialCardView = itemView.findViewById(R.id.cardMascota)
        private val ivFotoMascota: ImageView = itemView.findViewById(R.id.ivFotoMascota)
        private val tvEspecieBadge: TextView = itemView.findViewById(R.id.tvEspecieBadge)
        private val tvNombreMascota: TextView = itemView.findViewById(R.id.tvNombreMascota)
        private val tvRaza: TextView = itemView.findViewById(R.id.tvRaza)
        private val tvEdad: TextView = itemView.findViewById(R.id.tvEdad)
        private val tvSexo: TextView = itemView.findViewById(R.id.tvSexo)
        private val ivSexoIcon: ImageView = itemView.findViewById(R.id.ivSexoIcon)
        private val tvNombreDueno: TextView = itemView.findViewById(R.id.tvNombreDueno)
        private val tvTelefonoDueno: TextView = itemView.findViewById(R.id.tvTelefonoDueno)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)

        fun bind(mascota: Mascota) {
            // Establecer datos básicos
            tvNombreMascota.text = mascota.nombre
            tvRaza.text = mascota.raza
            tvEspecieBadge.text = mascota.especie
            tvEdad.text = "${mascota.edad} años"
            tvSexo.text = mascota.getSexoFormateado()
            tvNombreDueno.text = mascota.nombreDueno
            tvTelefonoDueno.text = mascota.telefonoDueno

            // Configurar ícono de sexo
            when (mascota.sexo.uppercase()) {
                "M" -> ivSexoIcon.setImageResource(R.drawable.ic_male)
                "F" -> ivSexoIcon.setImageResource(R.drawable.ic_female)
            }

            // Cargar foto con Glide
            if (!mascota.foto.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(Uri.parse(mascota.foto))
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_pet_placeholder)
                    .error(R.drawable.ic_pet_placeholder)
                    .into(ivFotoMascota)
            } else {
                ivFotoMascota.setImageResource(R.drawable.ic_pet_placeholder)
            }

            // Click en el card completo
            cardMascota.setOnClickListener {
                onItemClick?.invoke(mascota)
            }

            // Menú de opciones
            btnMenu.setOnClickListener {
                showPopupMenu(it, mascota)
            }
        }

        /**
         * Muestra el menú popup con opciones.
         */
        private fun showPopupMenu(view: View, mascota: Mascota) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_mascota_item, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_ver_detalles -> {
                        onItemClick?.invoke(mascota)
                        true
                    }
                    R.id.action_editar -> {
                        onEditClick?.invoke(mascota)
                        true
                    }
                    R.id.action_historial -> {
                        onHistorialClick?.invoke(mascota)
                        true
                    }
                    R.id.action_eliminar -> {
                        onDeleteClick?.invoke(mascota)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    /**
     * DiffUtil Callback para calcular diferencias eficientemente.
     */
    private class MascotaDiffCallback : DiffUtil.ItemCallback<Mascota>() {
        override fun areItemsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem == newItem
        }
    }
}