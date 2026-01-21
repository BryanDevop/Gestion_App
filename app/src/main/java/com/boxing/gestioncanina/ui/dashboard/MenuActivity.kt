package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.ui.citas.RegistrarCitaActivity
import com.boxing.gestioncanina.ui.medical.ConsultasVeterinariaActivity
import com.boxing.gestioncanina.ui.settings.SettingsFragment

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Marcar INICIO como seleccionado al abrir
        setActiveMenu(R.id.nav_inicio)

        // ------------- EVENTOS DE CLIC -------------
        findViewById<LinearLayout>(R.id.nav_inicio).setOnClickListener {
            setActiveMenu(R.id.nav_inicio)
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_consulta).setOnClickListener {
            setActiveMenu(R.id.nav_consulta)
            startActivity(Intent(this, RegistrarCitaActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_recetas).setOnClickListener {
            setActiveMenu(R.id.nav_recetas)
            // AÚN NO ABRE ACTIVITY
        }

        findViewById<LinearLayout>(R.id.nav_farmacia).setOnClickListener {
            setActiveMenu(R.id.nav_farmacia)
            // AÚN NO ABRE ACTIVITY
        }

        findViewById<LinearLayout>(R.id.nav_setting).setOnClickListener {
            setActiveMenu(R.id.nav_setting)
            startActivity(Intent(this, SettingsFragment::class.java))
        }
    }

    // FUNCIÓN QUE CAMBIA EL COLOR DEL ICONO Y TEXTO
    private fun setActiveMenu(selectedId: Int) {

        // ------ 1. RESET A TODOS A GRIS ------
        val items = listOf(
            Pair(R.id.icon_inicio, R.id.text_inicio),
            Pair(R.id.icon_consulta, R.id.text_consulta),
            Pair(R.id.icon_recetas, R.id.text_recetas),
            Pair(R.id.icon_farmacia, R.id.text_farmacia),
            Pair(R.id.icon_menu, R.id.text_menu)
        )

        for (item in items) {
            findViewById<ImageView>(item.first).setColorFilter(getColor(R.color.nav_item_color))
            findViewById<TextView>(item.second).setTextColor(getColor(R.color.nav_item_color))
        }

        // ------ 2. ACTIVAR EL SELECCIONADO EN AZUL ------
        when (selectedId) {

            R.id.nav_inicio -> {
                findViewById<ImageView>(R.id.icon_inicio).setColorFilter(getColor(R.color.nav_item_selected))
                findViewById<TextView>(R.id.text_inicio).setTextColor(getColor(R.color.nav_item_selected))
            }

            R.id.nav_consulta -> {
                findViewById<ImageView>(R.id.icon_consulta).setColorFilter(getColor(R.color.nav_item_selected))
                findViewById<TextView>(R.id.text_consulta).setTextColor(getColor(R.color.nav_item_selected))
            }

            R.id.nav_recetas -> {
                findViewById<ImageView>(R.id.icon_recetas).setColorFilter(getColor(R.color.nav_item_selected))
                findViewById<TextView>(R.id.text_recetas).setTextColor(getColor(R.color.nav_item_selected))
            }

            R.id.nav_farmacia -> {
                findViewById<ImageView>(R.id.icon_farmacia).setColorFilter(getColor(R.color.nav_item_selected))
                findViewById<TextView>(R.id.text_farmacia).setTextColor(getColor(R.color.nav_item_selected))
            }

            R.id.nav_setting -> {
                findViewById<ImageView>(R.id.icon_menu).setColorFilter(getColor(R.color.nav_item_selected))
                findViewById<TextView>(R.id.text_menu).setTextColor(getColor(R.color.nav_item_selected))
            }
        }
    }
}
