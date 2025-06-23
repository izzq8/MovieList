package com.faizabhinaya.mymovielist2.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = authRepository.currentUser
        if (currentUser != null) {
            _uiState.update {
                it.copy(
                    email = currentUser.email ?: "",
                    isLoggedIn = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoggedIn = false
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update {
                it.copy(
                    isLoggedIn = false
                )
            }
        }
    }
}

data class ProfileUiState(
    val email: String = "",
    val isLoggedIn: Boolean = false
)
