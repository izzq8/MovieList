package com.faizabhinaya.mymovielist2.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.data.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadAllCategorizedMovies() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val popularDeferred = async { movieRepository.getPopularMovies() }
                val topRatedDeferred = async { movieRepository.getTopRatedMovies() }
                val upcomingDeferred = async { movieRepository.getUpcomingMovies() }
                val trendingDeferred = async { movieRepository.getTrendingMovies() }
                val actionDeferred = async { movieRepository.getActionMovies() }
                val comedyDeferred = async { movieRepository.getComedyMovies() }
                val indonesianDeferred = async { movieRepository.getIndonesianMovies() }
                val japaneseDeferred = async { movieRepository.getJapaneseMovies() }

                val categories = mapOf(
                    MovieCategory.POPULAR to popularDeferred.await().results,
                    MovieCategory.TOP_RATED to topRatedDeferred.await().results,
                    MovieCategory.UPCOMING to upcomingDeferred.await().results,
                    MovieCategory.TRENDING to trendingDeferred.await().results,
                    MovieCategory.ACTION to actionDeferred.await().results,
                    MovieCategory.COMEDY to comedyDeferred.await().results,
                    MovieCategory.INDONESIAN to indonesianDeferred.await().results,
                    MovieCategory.JAPANESE to japaneseDeferred.await().results
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        // Tetap menyimpan movies untuk backward compatibility
                        movies = popularDeferred.await().results
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load movies"
                    )
                }
            }
        }
    }

    // Untuk backward compatibility
    fun loadPopularMovies() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response = movieRepository.getPopularMovies()
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
                        error = e.message ?: "Failed to load popular movies"
                    )
                }
            }
        }
    }
}

enum class MovieCategory(val title: String) {
    POPULAR("Popular Movies"),
    TOP_RATED("Top Rated"),
    UPCOMING("Coming Soon"),
    TRENDING("Trending This Week"),
    ACTION("Action Movies"),
    COMEDY("Comedy Movies"),
    INDONESIAN("Indonesian Movies"),
    JAPANESE("Japanese Movies")
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(), // Untuk backward compatibility
    val categories: Map<MovieCategory, List<Movie>> = emptyMap(),
    val error: String? = null
)
