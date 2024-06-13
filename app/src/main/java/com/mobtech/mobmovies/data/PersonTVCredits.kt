package com.mobtech.mobmovies.data

data class PersonTVCredits(
    val cast: List<TVCredit>
)

data class TVCredit(
    val backdrop_path: String?,
    val id: Int,
    val first_air_date: String,
    val name: String,
    val character: String,
    val episode_count: Int,
    val poster_path: String?,
    val vote_average: Double,
    val vote_count: Int
)