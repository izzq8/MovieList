package com.faizabhinaya.mymovielist2.data.repository

import com.faizabhinaya.mymovielist2.data.model.Movie
import com.faizabhinaya.mymovielist2.data.model.MovieDetails
import com.faizabhinaya.mymovielist2.data.remote.ApiClient
import com.faizabhinaya.mymovielist2.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MovieRepository {
    private val movieApiService = ApiClient.movieApiService
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getPopularMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.getPopularMovies(Constants.API_KEY, page)
    }

    suspend fun getNowPlayingMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.getNowPlayingMovies(Constants.API_KEY, page)
    }

    suspend fun getTopRatedMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.getTopRatedMovies(Constants.API_KEY, page)
    }

    suspend fun getUpcomingMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.getUpcomingMovies(Constants.API_KEY, page)
    }

    suspend fun getTrendingMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.getTrendingMovies(Constants.API_KEY, page)
    }

    suspend fun getActionMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        // Action genre ID is 28
        movieApiService.getMoviesByGenre(Constants.API_KEY, 28, page)
    }

    suspend fun getComedyMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        // Comedy genre ID is 35
        movieApiService.getMoviesByGenre(Constants.API_KEY, 35, page)
    }

    suspend fun getIndonesianMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        // Indonesian language code is "id"
        movieApiService.getMoviesByCountry(Constants.API_KEY, "id", page)
    }

    suspend fun getJapaneseMovies(page: Int = 1) = withContext(Dispatchers.IO) {
        // Japanese language code is "ja"
        movieApiService.getMoviesByCountry(Constants.API_KEY, "ja", page)
    }

    // Existing methods
    suspend fun searchMovies(query: String, page: Int = 1) = withContext(Dispatchers.IO) {
        movieApiService.searchMovies(Constants.API_KEY, query, page)
    }

    suspend fun getMovieDetails(movieId: Int) = withContext(Dispatchers.IO) {
        movieApiService.getMovieDetails(movieId, Constants.API_KEY)
    }

    suspend fun addToWatchlist(movie: Movie) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val watchlistItem = hashMapOf(
            "movieId" to movie.id,
            "title" to movie.title,
            "posterPath" to movie.posterPath,
            "backdropPath" to movie.backdropPath,
            "overview" to movie.overview,
            "releaseDate" to movie.releaseDate,
            "rating" to movie.rating,
            "addedAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .collection("watchlist")
            .document(movie.id.toString())
            .set(watchlistItem)
            .await()
    }

    suspend fun removeFromWatchlist(movieId: Int) = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        firestore.collection("users")
            .document(userId)
            .collection("watchlist")
            .document(movieId.toString())
            .delete()
            .await()
    }

    suspend fun getWatchlist(): List<Movie> = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("watchlist")
            .get()
            .await()

        snapshot.documents.map { document ->
            Movie(
                id = document.getLong("movieId")?.toInt() ?: 0,
                title = document.getString("title") ?: "",
                posterPath = document.getString("posterPath"),
                backdropPath = document.getString("backdropPath"),
                overview = document.getString("overview"),
                releaseDate = document.getString("releaseDate"),
                rating = document.getDouble("rating") ?: 0.0,
                popularity = 0.0, // Not stored in watchlist
                genreIds = null // Not stored in watchlist
            )
        }
    }

    suspend fun isInWatchlist(movieId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext false

            val document = firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .document(movieId.toString())
                .get()
                .await()

            document.exists()
        } catch (e: Exception) {
            false
        }
    }
}
