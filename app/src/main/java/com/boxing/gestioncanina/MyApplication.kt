package com.boxing.gestioncanina

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
            Log.d("MyApplication", "✅ Firebase inicializado correctamente")

            // 🔧 Usar emuladores locales para EVITAR reCAPTCHA
            if (BuildConfig.DEBUG) {
                // Conectar a emuladores locales
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
                FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
                Log.d("MyApplication", "🔧 Usando Firebase Emulators (sin reCAPTCHA)")
            }

        } catch (e: Exception) {
            Log.e("MyApplication", "❌ Error inicializando Firebase", e)
            e.printStackTrace()
        }
    }
}