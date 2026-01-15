package com.boxing.gestioncanina.data.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage // ✅ ESTE ES EL BUENO

object Supabase {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://nnjurrfxrgqfmwpgzzew.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5uanVycmZ4cmdxZm13cGd6emV3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ2NTk3NDUsImV4cCI6MjA4MDIzNTc0NX0.OAIxRPBP62Er8fFVIn-Vm7OFVXR33BQTsSeqjyxxDCA"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage) // 🚀 YA NO FALLA
    }
}
