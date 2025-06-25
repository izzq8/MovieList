package com.faizabhinaya.mymovielist2.data.model

data class MovieReview(
    val id: String = "",
    val movieId: Int = 0,
    val movieTitle: String = "",
    val rating: Float = 0f, // 0.0 - 5.0
    val review: String = "",
    val userId: String = "", // untuk multi-user support
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis()
) {
    // Constructor kosong untuk Firebase
    constructor() : this("", 0, "", 0f, "", "", 0L, 0L)

    // Convert ke Map untuk Firebase
    fun toMap(): Map<String, Any> {
        return mapOf(
            "movieId" to movieId,
            "movieTitle" to movieTitle,
            "rating" to rating,
            "review" to review,
            "userId" to userId,
            "dateCreated" to dateCreated,
            "dateModified" to dateModified
        )
    }

    companion object {
        // Convert dari Firebase DocumentSnapshot
        fun fromMap(id: String, data: Map<String, Any>): MovieReview {
            return MovieReview(
                id = id,
                movieId = (data["movieId"] as? Long)?.toInt() ?: 0,
                movieTitle = data["movieTitle"] as? String ?: "",
                rating = (data["rating"] as? Double)?.toFloat() ?: 0f,
                review = data["review"] as? String ?: "",
                userId = data["userId"] as? String ?: "",
                dateCreated = data["dateCreated"] as? Long ?: 0L,
                dateModified = data["dateModified"] as? Long ?: 0L
            )
        }
    }
}