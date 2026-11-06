package org.app.project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.app.project.data.AuthService
import org.app.project.data.AuthState
import org.app.project.data.toAuthMessage

class AuthViewModel(
    private val auth: AuthService
) : ViewModel() {

    val authState: StateFlow<AuthState> =
        auth.authState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    val info: StateFlow<String?> = _info

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy

    fun signUp(email: String, password: String) {
        run {
            val needsConfirmation = auth.signUp(email.trim(), password)
            if (needsConfirmation) {
                _info.value = "Check your email to confirm your account, then sign in."
            }
        }
    }

    fun signIn(email: String, password: String) {
        run { auth.signIn(email.trim(), password) }
    }

    fun signOut() {
        run { auth.signOut() }
    }

    private fun run(block: suspend () -> Unit) {
        if (_busy.value) return
        viewModelScope.launch {
            _busy.value = true
            _error.value = null
            _info.value = null
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.toAuthMessage()
            } finally {
                _busy.value = false
            }
        }
    }

    fun clearError() { _error.value = null }

    fun clearMessages() {
        _error.value = null
        _info.value = null
    }
}
