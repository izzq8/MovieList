package com.faizabhinaya.mymovielist2.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.data.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private var searchJob: Job? = null

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }

        // Cancel previous search if it's still running
        searchJob?.cancel()

        // Start a new search with debounce
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isInitialState = false) }

            try {
                // Add a small delay to avoid too many API calls while typing
                delay(500)

                val response = movieRepository.searchMovies(query)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movies = response.results
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to search movies"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update {
            SearchUiState(isInitialState = true)
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
    val isInitialState: Boolean = true
)
