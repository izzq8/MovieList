package com.faizabhinaya.mymovielist2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faizabhinaya.mymovielist2.data.model.MovieReview
import com.faizabhinaya.mymovielist2.data.repository.FirebaseReviewRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val repository: FirebaseReviewRepository = FirebaseReviewRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    // Flow untuk semua review user
    private val _allReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val allReviews: StateFlow<List<MovieReview>> = _allReviews.asStateFlow()

    init {
        loadAllReviews()
        loadStatistics()
    }

    private fun loadAllReviews() {
        viewModelScope.launch {
            repository.getAllUserReviews().collect { reviews ->
                _allReviews.value = reviews
            }
        }
    }

    fun getReviewByMovieId(movieId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val review = repository.getReviewByMovieId(movieId)
                _uiState.update {
                    it.copy(
                        currentReview = review,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load review: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveReview(review: MovieReview, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val result = if (review.id.isEmpty()) {
                    repository.saveReview(review)
                } else {
                    repository.updateReview(review)
                }

                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Review saved successfully!"
                            )
                        }
                        loadAllReviews() // Refresh data
                        loadStatistics()
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to save review: ${exception.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteReview(reviewId)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(successMessage = "Review deleted successfully!")
                        }
                        loadAllReviews() // Refresh data
                        loadStatistics()
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(errorMessage = "Failed to delete review: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Unexpected error: ${e.message}")
                }
            }
        }
    }

    fun checkIfUserReviewedMovie(movieId: Int) {
        viewModelScope.launch {
            try {
                val hasReviewed = repository.hasUserReviewedMovie(movieId)
                _uiState.update { it.copy(hasUserReviewedMovie = hasReviewed) }
            } catch (e: Exception) {
                // Handle silently
            }
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getUserStatistics()
                _uiState.update {
                    it.copy(
                        averageRating = stats.averageRating,
                        totalReviews = stats.totalReviews
                    )
                }
            } catch (e: Exception) {
                // Handle silently
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(successMessage = null, errorMessage = null)
        }
    }
}

data class ReviewUiState(
    val isLoading: Boolean = false,
    val currentReview: MovieReview? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val hasUserReviewedMovie: Boolean = false
)