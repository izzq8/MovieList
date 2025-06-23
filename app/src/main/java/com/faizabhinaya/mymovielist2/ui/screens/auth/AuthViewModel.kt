package com.faizabhinaya.mymovielist2.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun signIn() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        if (!validateInputs()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            try {
                authRepository.signIn(_uiState.value.email, _uiState.value.password)
                _uiState.update {
                    it.copy(isLoading = false, isAuthenticated = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Authentication failed")
                }
            }
        }
    }

    fun signUp() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        if (!validateInputs()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            try {
                authRepository.signUp(_uiState.value.email, _uiState.value.password)
                _uiState.update {
                    it.copy(isLoading = false, isAuthenticated = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Registration failed")
                }
            }
        }
    }

    fun resetPassword() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        if (_uiState.value.email.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "Please enter your email address")
            }
            return
        }

        viewModelScope.launch {
            try {
                authRepository.resetPassword(_uiState.value.email)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resetEmailSent = true,
                        successMessage = "Password reset email sent to ${_uiState.value.email}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to send reset email")
                }
            }
        }
    }

    fun logout() {
        authRepository.signOut()
        // Reset state setelah logout
        _uiState.update {
            AuthUiState() // Reset ke state awal
        }
    }

    private fun validateInputs(): Boolean {
        if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email and password cannot be empty")
            }
            return false
        }
        return true
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val resetEmailSent: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
