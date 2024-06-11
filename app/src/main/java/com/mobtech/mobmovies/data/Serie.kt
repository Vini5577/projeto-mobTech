package com.mobtech.mobmovies.data

data class Serie (
    val id: Int,
    val original_name: String,
    val overview: String,
    val poster_path: String,
    val name: String,
    val vote_average: Double,
    val vote_count: Int
)

data class SerieResponse(val results: List<Serie>)