package com.mobtech.mobmovies.data

data class SerieCast(val cast: List<TvCast>)

data class TvCast(
    val backdrop_path: String?,
    val genre_ids: List<Int>,
    val id: Int,
    val origin_country: List<String>,
    val original_language: String,
    val original_name: String,
    val profile_path: String?,
    val name: String,
    val character: String,
)