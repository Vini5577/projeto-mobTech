package com.mobtech.mobmovies.data

data class SerieDetails (
    val id: Int,
    val name: String,
    val overview: String,
    val first_air_date: String,
    val last_air_date: String,
    val genres: List<GenreTv>,
    val poster_path: String?,
    val backdrop_path: String?,
    val number_of_episodes: Int,
    val number_of_seasons: Int,
    val last_episode_to_air: Episode?,
    val next_episode_to_air: Episode?
)

data class GenreTv(
    val id: Int,
    val name: String
)

data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val episode_number: Int,
    val season_number: Int,
    val air_date: String,
    val vote_average: Double,
    val vote_count: Int,
    val still_path: String?
)