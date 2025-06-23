package com.faizabhinaya.mymovielist2.data.model

import com.google.gson.annotations.SerializedName

data class ActorDetails(
    val id: Int,
    val name: String,
    val biography: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    val birthday: String?,
    @SerializedName("place_of_birth")
    val placeOfBirth: String?,
    @SerializedName("known_for_department")
    val knownForDepartment: String?,
    val popularity: Double,
    @SerializedName("movie_credits")
    val movieCredits: ActorMovieCredits?
)

data class ActorMovieCredits(
    val cast: List<Movie>
)
