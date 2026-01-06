package com.boxing.gestioncanina

import android.app.Application
import android.util.Log
import com.boxing.gestioncanina.data.network.Supabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // Inicializar Supabase
            Supabase.initialize()
            Log.d("MyApplication", "✅ Supabase inicializado correctamente")

            if (BuildConfig.DEBUG) {
                Log.d("MyApplication", "🔧 Modo DEBUG - Usando Supabase en producción")
            }

        } catch (e: Exception) {
            Log.e("MyApplication", "❌ Error inicializando Supabase", e)
            e.printStackTrace()
        }
    }
}

private fun Supabase.initialize() {
    //Nada que implementar por ahora
}
