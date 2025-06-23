package com.faizabhinaya.mymovielist2.ui.screens.actor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.faizabhinaya.mymovielist2.data.model.ActorDetails
import com.faizabhinaya.mymovielist2.data.remote.ApiClient
import com.faizabhinaya.mymovielist2.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActorDetailViewModel(private val actorId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(ActorDetailUiState())
    val uiState: StateFlow<ActorDetailUiState> = _uiState.asStateFlow()

    init {
        loadActorDetails()
    }

    fun loadActorDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val actorDetails = ApiClient.movieApiService.getActorDetails(
                    personId = actorId,
                    apiKey = Constants.API_KEY
                )
                _uiState.update { it.copy(isLoading = false, actorDetails = actorDetails) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory: (Int) -> ViewModelProvider.Factory = { actorId ->
            viewModelFactory {
                initializer {
                    ActorDetailViewModel(actorId)
                }
            }
        }
    }
}

data class ActorDetailUiState(
    val isLoading: Boolean = true,
    val actorDetails: ActorDetails? = null,
    val error: String? = null
)
