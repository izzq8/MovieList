package com.faizabhinaya.mymovielist2.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.data.model.MovieDetails
import com.faizabhinaya.mymovielist2.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int
) : ViewModel() {
    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState

    init {
        loadMovieDetails()
        checkIfInWatchlist()
    }

    fun loadMovieDetails() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val movieDetails = movieRepository.getMovieDetails(movieId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movieDetails = movieDetails
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load movie details"
                    )
                }
            }
        }
    }

    private fun checkIfInWatchlist() {
        viewModelScope.launch {
            try {
                val isInWatchlist = movieRepository.isInWatchlist(movieId)
                _uiState.update {
                    it.copy(isInWatchlist = isInWatchlist)
                }
            } catch (e: Exception) {
                // Silently fail, not critical
            }
        }
    }

    fun addToWatchlist() {
        viewModelScope.launch {
            try {
                val movie = uiState.value.movieDetails?.let {
                    Movie(
                        id = it.id,
                        title = it.title,
                        posterPath = it.posterPath,
                        backdropPath = it.backdropPath,
                        overview = it.overview,
                        releaseDate = it.releaseDate,
                        rating = it.rating,
                        popularity = 0.0,
                        genreIds = null
                    )
                } ?: return@launch

                movieRepository.addToWatchlist(movie)
                _uiState.update {
                    it.copy(
                        isInWatchlist = true,
                        watchlistActionMessage = "Added to your watchlist"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        watchlistActionMessage = e.message ?: "Failed to add to watchlist"
                    )
                }
            }
        }
    }

    fun removeFromWatchlist() {
        viewModelScope.launch {
            try {
                movieRepository.removeFromWatchlist(movieId)
                _uiState.update {
                    it.copy(
                        isInWatchlist = false,
                        watchlistActionMessage = "Removed from your watchlist"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        watchlistActionMessage = e.message ?: "Failed to remove from watchlist"
                    )
                }
            }
        }
    }

    fun clearWatchlistActionMessage() {
        _uiState.update {
            it.copy(watchlistActionMessage = null)
        }
    }

    class Factory(private val movieId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MovieDetailViewModel::class.java)) {
                return MovieDetailViewModel(movieId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movieDetails: MovieDetails? = null,
    val isInWatchlist: Boolean = false,
    val error: String? = null,
    val watchlistActionMessage: String? = null
)
