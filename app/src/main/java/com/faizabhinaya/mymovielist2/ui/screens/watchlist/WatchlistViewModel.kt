package com.faizabhinaya.mymovielist2.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WatchlistViewModel : ViewModel() {
    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState

    fun loadWatchlist() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val watchlist = movieRepository.getWatchlist()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        watchlist = watchlist
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load watchlist"
                    )
                }
            }
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            try {
                movieRepository.removeFromWatchlist(movieId)

                // Update the local state by removing the movie
                _uiState.update { currentState ->
                    val updatedWatchlist = currentState.watchlist.filter { it.id != movieId }
                    currentState.copy(watchlist = updatedWatchlist)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to remove from watchlist"
                    )
                }
            }
        }
    }
}

data class WatchlistUiState(
    val isLoading: Boolean = false,
    val watchlist: List<Movie> = emptyList(),
    val error: String? = null
)
