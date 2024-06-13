package com.mobtech.mobmovies.data

data class Favorite (
    val id: Int,
    val categoria: String,
    val poster_path: String,
    val nome: String,
    val vote_average: Double
)