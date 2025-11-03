package com.boxing.gestioncanina.ui.dashboard

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boxing.gestioncanina.R
import com.boxing.gestioncanina.ui.auth.RegisterActivity
import com.google.android.material.button.MaterialButton

class Welcome : AppCompatActivity() {

    // Íconos
    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView
    private lateinit var icon5: ImageView
    private lateinit var icon6: ImageView
    private lateinit var icon7: ImageView
    private lateinit var icon8: ImageView

    // Botones
    private lateinit var btnClose: ImageView
    private lateinit var btnPhone: MaterialButton
    private lateinit var btnEmail: MaterialButton
    private lateinit var btnApple: MaterialButton
    private lateinit var btnGoogle: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        enableEdgeToEdge()

        // Layout
        setContentView(R.layout.activity_welcome)

        // Ajustar padding según barras del sistema
        val mainView: View = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        initViews()
        setupButtons()

    }

    private fun initViews() {
        // Íconos


        // Botones
        btnClose = findViewById(R.id.btnClose)
        btnPhone = findViewById(R.id.btnPhone)
        btnEmail = findViewById(R.id.btnRegister)
        btnApple = findViewById(R.id.btnApple)
        btnGoogle = findViewById(R.id.btnGoogle)
    }

    private fun setupButtons() {
        btnClose.setOnClickListener { finish() }
        btnPhone.setOnClickListener {
            // TODO: navegar a LoginActivity
        }
        btnEmail.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnApple.setOnClickListener {
            // TODO: acción Apple
        }
        btnGoogle.setOnClickListener {
            // TODO: acción Google
        }
    }




}
