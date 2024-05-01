package com.mobtech.mobmovies.data

data class Movie (
    val backdrop_path: String,
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val title: String,
    val release_date: String,
    val vote_average: Double
)

data class MovieResponse(val results: List<Movie>)