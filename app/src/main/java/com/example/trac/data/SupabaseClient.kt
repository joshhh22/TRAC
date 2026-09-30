package com.example.trac.data

import com.example.trac.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientManager {
    // Loaded securely from local.properties via BuildConfig (never committed to git)
    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL.ifBlank { "https://placeholder.supabase.co" }
    val SUPABASE_ANON_KEY: String = BuildConfig.SUPABASE_ANON_KEY.ifBlank { "placeholder-anon-key" }

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
