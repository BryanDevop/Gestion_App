package com.boxing.gestioncanina.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.ui.auth.LoginActivity
import com.boxing.gestioncanina.ui.auth.RegisterActivity

class Load_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_load_screen) // ✅ nombre correcto del XML

        // Ajuste de márgenes con los bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loadScreenLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Animación suave de aparición (fade in)
        val logo = findViewById<ImageView>(R.id.logoImage)
        val title = findViewById<TextView>(R.id.appTitle)
        val subtitle = findViewById<TextView>(R.id.appSubtitle)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1200
        logo.startAnimation(fadeIn)
        title.startAnimation(fadeIn)
        subtitle.startAnimation(fadeIn)

        // ✅ Transición automática al Dashboard después de 2.5 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            finish() // Cierra el splash
        }, 2500)
    }
}
