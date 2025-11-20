package com.boxing.gestioncanina.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.boxing.gestioncanina.R
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.databinding.ActivityBiographyAnimalBinding
import com.boxing.gestioncanina.databinding.DialogAdoptionFormBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Biography_Animal : AppCompatActivity() {

    private lateinit var binding: ActivityBiographyAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityBiographyAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
    }

    private fun setupUI() {
        // Configurar datos de ejemplo (reemplazar con datos reales)
        setupPetData()

        // Configurar listeners
        binding.btnAdopt.setOnClickListener {
            showAdoptionDialog()
        }

        binding.fabBack.setOnClickListener {
            finish()
        }

        binding.fabFavorite.setOnClickListener {
            // Implementar lógica de favoritos
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
        }

        binding.btnCallShelter.setOnClickListener {
            // Implementar lógica de llamada
            Toast.makeText(this, "Llamando al refugio...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPetData() {
        // Aquí deberías cargar los datos reales de la mascota
        // Este es un ejemplo con datos de prueba
        binding.apply {
            tvPetName.text = "Luna"
            tvPetBreed.text = "Golden Retriever"
            tvAge.text = "2 años"
            tvGender.text = "Hembra"
            tvWeight.text = "15 kg"
            tvLocation.text = "Santo Domingo, República Dominicana"
            tvDescription.text = "Luna es una perrita encantadora y llena de energía. Le encanta jugar y es muy cariñosa con las personas. Está completamente vacunada, desparasitada y lista para encontrar su hogar definitivo."
            tvShelterName.text = "Refugio Patitas Felices"
        }
    }

    private fun showAdoptionDialog() {
        val dialogBinding = DialogAdoptionFormBinding.inflate(LayoutInflater.from(this))

        val dialog = MaterialAlertDialogBuilder(this, R.style.RoundedMaterialDialog)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Configurar listeners del formulario
        dialogBinding.apply {
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (validateForm(dialogBinding)) {
                    submitAdoptionForm(dialogBinding)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun validateForm(dialogBinding: DialogAdoptionFormBinding): Boolean {
        var isValid = true

        dialogBinding.apply {
            // Validar nombre
            if (etFullName.text.isNullOrBlank()) {
                tilFullName.error = "El nombre es requerido"
                isValid = false
            } else {
                tilFullName.error = null
            }

            // Validar email
            val email = etEmail.text.toString()
            if (email.isBlank()) {
                tilEmail.error = "El email es requerido"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Email inválido"
                isValid = false
            } else {
                tilEmail.error = null
            }

            // Validar teléfono
            if (etPhone.text.isNullOrBlank()) {
                tilPhone.error = "El teléfono es requerido"
                isValid = false
            } else {
                tilPhone.error = null
            }

            // Validar dirección
            if (etAddress.text.isNullOrBlank()) {
                tilAddress.error = "La dirección es requerida"
                isValid = false
            } else {
                tilAddress.error = null
            }

            // Validar radio buttons
            if (rgPreviousPets.checkedRadioButtonId == -1) {
                Toast.makeText(this@Biography_Animal, "Por favor responde si has tenido mascotas antes", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (rgAdequateSpace.checkedRadioButtonId == -1) {
                Toast.makeText(this@Biography_Animal, "Por favor responde si tienes espacio adecuado", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            // Validar términos y condiciones
            if (!cbTerms.isChecked) {
                Toast.makeText(this@Biography_Animal, "Debes aceptar los términos y condiciones", Toast.LENGTH_LONG).show()
                isValid = false
            }
        }

        return isValid
    }

    private fun submitAdoptionForm(dialogBinding: DialogAdoptionFormBinding) {
        dialogBinding.apply {
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val address = etAddress.text.toString()
            val hasPreviousPets = rbYes.isChecked
            val hasAdequateSpace = rbSpaceYes.isChecked
            val comments = etComments.text.toString()

            // Aquí deberías enviar los datos a tu backend/base de datos
            // Por ahora solo mostramos un mensaje de éxito

            showSuccessDialog()

            // Log para debug
            println("Formulario de adopción:")
            println("Nombre: $fullName")
            println("Email: $email")
            println("Teléfono: $phone")
            println("Dirección: $address")
            println("Ha tenido mascotas: $hasPreviousPets")
            println("Tiene espacio: $hasAdequateSpace")
            println("Comentarios: $comments")
        }
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¡Solicitud Enviada!")
            .setMessage("Tu solicitud de adopción ha sido enviada exitosamente. El refugio se pondrá en contacto contigo pronto.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                // Opcional: regresar a la pantalla anterior
                // finish()
            }
            .setIcon(R.drawable.ic_check_circle)
            .show()
    }
}