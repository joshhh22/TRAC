package com.example.trac.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientManager {
    const val SUPABASE_URL = "https://muasljelvaptbfylcsrx.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im11YXNsamVsdmFwdGJmeWxjc3J4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTAwNTY0NjcsImV4cCI6MjEwNTYzMjQ2N30.SYFwySMuFJKV2cC6H5QCpk8rhgNDsLtw-9IOnITnSpo"

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
