package com.boxing.gestioncanina.ui.medical

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.R
import com.google.android.material.card.MaterialCardView

class ConsultasVeterinariaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultas_veterinaria)

        // Manejo de insets (opcional por ahora)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        */

        // Referencias a las tarjetas
        val cardDiagnostico = findViewById<MaterialCardView>(R.id.card_diagnostico)
        val cardTratamiento = findViewById<MaterialCardView>(R.id.card_tratamiento)
        val cardMedicamentos = findViewById<MaterialCardView>(R.id.card_medicamentos)
        val cardObservaciones = findViewById<MaterialCardView>(R.id.card_observaciones)

        // Click listeners
        cardDiagnostico.setOnClickListener {
            startActivity(Intent(this, Diagnostico::class.java))
        }

        cardTratamiento.setOnClickListener {
            startActivity(Intent(this, Tratamiento::class.java))
        }

        cardMedicamentos.setOnClickListener {
            startActivity(Intent(this, Medicamento::class.java))
        }

        cardObservaciones.setOnClickListener {
            startActivity(Intent(this, Observaciones::class.java))
        }

        // Opcional: botón de volver en toolbar
        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarConsultas)
            ?.setNavigationOnClickListener {
                finish()
            }
    }
}