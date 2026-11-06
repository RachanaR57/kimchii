package org.app.project.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

val supabase: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://gbvsfkrjenpdwbxcpjub.supabase.co",
    supabaseKey = "sb_publishable_0AdzdJRntMj1Um6uW6yMNw_CIfQhXMz"
) {
    install(Auth)
    install(Postgrest)
}