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

    private fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
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

    // Get semua review dari semua pengguna
    fun getAllReviews(): Flow<List<MovieReview>> = flow {
        try {
            val snapshot = reviewsCollection
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
            val userName = getCurrentUserName()
            val reviewWithUserInfo = review.copy(
                userId = getCurrentUserId(),
                userName = userName
            )
            val docRef = reviewsCollection.add(reviewWithUserInfo.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update review yang sudah ada
    suspend fun updateReview(review: MovieReview): Result<Unit> {
        return try {
            if (review.id.isNotEmpty()) {
                val userName = getCurrentUserName()
                val updatedReview = review.copy(
                    userId = getCurrentUserId(),
                    userName = userName,
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

    // Get semua review untuk movie tertentu dari semua pengguna
    fun getAllReviewsForMovie(movieId: Int): Flow<List<MovieReview>> = flow {
        try {
            val snapshot = reviewsCollection
                .whereEqualTo("movieId", movieId)
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

    // Fungsi debug untuk mengecek raw data dari Firestore
    suspend fun debugGetAllReviewsRaw(movieId: Int): List<String> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("movieId", movieId)
                .get()
                .await()

            snapshot.documents.map { doc ->
                "ID: ${doc.id}, " +
                "MovieID: ${doc.getLong("movieId")}, " +
                "UserID: ${doc.getString("userId")}, " +
                "UserName: ${doc.getString("userName") ?: "Unknown"}, " +
                "Rating: ${doc.get("rating")}, " +
                "Review: ${doc.getString("review")?.take(20)}..."
            }
        } catch (e: Exception) {
            listOf("Error: ${e.message}")
        }
    }
}

data class ReviewStatistics(
    val totalReviews: Int,
    val averageRating: Float
)