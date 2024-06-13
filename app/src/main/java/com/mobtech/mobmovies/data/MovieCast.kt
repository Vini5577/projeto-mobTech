package com.mobtech.mobmovies.data

 data class MovieCast(val id: Int, val cast: List<Cast>)

data class Cast(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val name: String,
    val popularity: Double,
    val profile_path: String?,
    val cast_id: Int,
    val character: String,
)
