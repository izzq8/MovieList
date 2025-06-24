package com.faizabhinaya.mymovielist2.data.repository

import com.faizabhinaya.mymovielist2.data.model.MovieReview
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseReviewRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val reviewsCollection = firestore.collection("movie_reviews")

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "anonymous"
    }

    // Get semua review user saat ini
    fun getAllUserReviews(): Flow<List<MovieReview>> = flow {
        try {
            val userId = getCurrentUserId()
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .orderBy("dateModified", Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    MovieReview.fromMap(doc.id, data)
                }
            }
            emit(reviews)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get review untuk movie tertentu
    suspend fun getReviewByMovieId(movieId: Int): MovieReview? {
        return try {
            val userId = getCurrentUserId()
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("movieId", movieId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                doc.data?.let { data ->
                    MovieReview.fromMap(doc.id, data)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Simpan review baru
    suspend fun saveReview(review: MovieReview): Result<String> {
        return try {
            val reviewWithUserId = review.copy(userId = getCurrentUserId())
            val docRef = reviewsCollection.add(reviewWithUserId.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update review yang sudah ada
    suspend fun updateReview(review: MovieReview): Result<Unit> {
        return try {
            if (review.id.isNotEmpty()) {
                val updatedReview = review.copy(
                    userId = getCurrentUserId(),
                    dateModified = System.currentTimeMillis()
                )
                reviewsCollection.document(review.id)
                    .set(updatedReview.toMap())
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Review ID is empty"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete review
    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserStatistics(): ReviewStatistics {
        return try {
            val userId = getCurrentUserId()
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                (doc.data?.get("rating") as? Double)?.toFloat()
            }

            val totalReviews = reviews.size
            val averageRating = if (reviews.isNotEmpty()) {
                reviews.average().toFloat()
            } else 0f

            ReviewStatistics(
                totalReviews = totalReviews,
                averageRating = averageRating
            )
        } catch (e: Exception) {
            ReviewStatistics(0, 0f)
        }
    }

    // Check apakah user sudah review movie ini
    suspend fun hasUserReviewedMovie(movieId: Int): Boolean {
        return try {
            val userId = getCurrentUserId()
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("movieId", movieId)
                .limit(1)
                .get()
                .await()

            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}

data class ReviewStatistics(
    val totalReviews: Int,
    val averageRating: Float
)