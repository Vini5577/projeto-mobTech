package com.mobtech.mobmovies.data

data class PersonMovieCredits(
    val cast: List<MovieCredit>
)

data class MovieCredit(
    val backdrop_path: String?,
    val id: Int,
    val release_date: String,
    val title: String
)