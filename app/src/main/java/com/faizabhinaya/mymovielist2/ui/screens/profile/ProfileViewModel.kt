package com.faizabhinaya.mymovielist2.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.MyMovieListApplication
import com.faizabhinaya.mymovielist2.data.repository.AuthRepository
import com.faizabhinaya.mymovielist2.data.repository.UserPreferencesRepository
import com.faizabhinaya.mymovielist2.ui.theme.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val themeManager = MyMovieListApplication.getInstance().themeManager

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserProfile()
        loadUserPreferences()

        // Observe theme changes from ThemeManager
        viewModelScope.launch {
            themeManager.isDarkMode.collect { isDarkMode ->
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = authRepository.currentUser
        if (currentUser != null) {
            _uiState.update {
                it.copy(
                    email = currentUser.email ?: "",
                    displayName = currentUser.displayName ?: "",
                    photoUrl = currentUser.photoUrl?.toString() ?: "",
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

    private fun loadUserPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { preferences ->
                _uiState.update { state ->
                    state.copy(
                        isDarkMode = preferences.isDarkMode,
                        language = preferences.language,
                        // Hanya gunakan displayName dari preferences jika Firebase tidak menyediakannya
                        displayName = state.displayName.ifEmpty { preferences.displayName },
                        photoUrl = state.photoUrl.ifEmpty { preferences.photoUrl }
                    )
                }
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

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.updateProfile(name, null)
                userPreferencesRepository.updateDisplayName(name)
                _uiState.update {
                    it.copy(
                        displayName = name,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update display name: ${e.message}"
                    )
                }
            }
        }
    }

    fun updatePhotoUrl(url: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.updateProfile(_uiState.value.displayName, url)
                userPreferencesRepository.updatePhotoUrl(url)
                _uiState.update {
                    it.copy(
                        photoUrl = url,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update profile picture: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Update both UserPreferencesRepository and ThemeManager
                userPreferencesRepository.updateDarkMode(isDarkMode)
                themeManager.setDarkMode(isDarkMode)

                _uiState.update {
                    it.copy(
                        isDarkMode = isDarkMode,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update theme: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                userPreferencesRepository.updateLanguage(language)
                _uiState.update {
                    it.copy(
                        language = language,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update language: ${e.message}"
                    )
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Re-autentikasi diperlukan untuk operasi sensitif
                authRepository.reauthenticate(currentPassword)
                authRepository.updatePassword(newPassword)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to change password: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Re-autentikasi diperlukan untuk operasi sensitif
                authRepository.reauthenticate(password)
                authRepository.deleteAccount()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = false
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete account: ${e.message}"
                    )
                }
            }
        }
    }
}

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val isLoggedIn: Boolean = false,
    val isDarkMode: Boolean = false,
    val language: String = "en", // Default bahasa Inggris
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
