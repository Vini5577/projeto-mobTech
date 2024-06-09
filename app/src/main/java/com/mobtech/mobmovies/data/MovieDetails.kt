package com.mobtech.mobmovies.data

data class MovieDetails(
    val adult: Boolean,
    val backdrop_path: String?,
    val genres: List<Genre>,
    val id: Int,
    val overview: String,
    val poster_path: String?,
    val release_date: String,
    val runtime: Int,
    val tagline: String,
    val title: String,
)

data class Genre(
    val id: Int,
    val name: String
)