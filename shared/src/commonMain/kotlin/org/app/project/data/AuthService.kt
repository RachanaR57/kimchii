package org.app.project.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AuthState { Loading, SignedOut, SignedIn }

class AuthService {

    /** Returns true when the account still has to be confirmed by email before signing in. */
    suspend fun signUp(email: String, password: String): Boolean {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return supabase.auth.currentSessionOrNull() == null
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun currentEmail(): String? = supabase.auth.currentUserOrNull()?.email

    fun currentUserId(): String? =
        supabase.auth.currentUserOrNull()?.id

    val authState: Flow<AuthState> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.NotAuthenticated -> AuthState.SignedOut
                // RefreshFailure = a stored session that couldn't be refreshed (e.g. offline).
                // The user is still signed in, so keep them in the app.
                is SessionStatus.Authenticated,
                is SessionStatus.RefreshFailure -> AuthState.SignedIn
            }
        }
}

/** A short message that is safe to show in the UI (the raw ones include URLs and headers). */
fun Throwable.toAuthMessage(): String = when (this) {
    is AuthRestException -> errorDescription
    is RestException -> description ?: error
    is HttpRequestException -> "Can't reach the server. Check your connection and try again."
    else -> message?.lineSequence()?.firstOrNull()?.takeIf { it.isNotBlank() } ?: "Something went wrong"
}
