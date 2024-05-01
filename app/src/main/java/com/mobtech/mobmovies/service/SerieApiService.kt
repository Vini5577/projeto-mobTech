package com.mobtech.mobmovies.service

import com.mobtech.mobmovies.data.MovieResponse
import retrofit2.Call
import com.mobtech.mobmovies.data.SerieResponse
import org.intellij.lang.annotations.Language
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SerieApiService {

    @GET("trending/tv/day")
    fun getTrendingSeries(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<SerieResponse>

    @GET("tv/top_rated")
    fun getTopRatedSeries(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<SerieResponse>

    @GET("tv/airing_today?")
    fun getAiringToday (
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<SerieResponse>

    @GET("tv/{series_id}/recommendations")
    fun getRecommendationsSerie(
        @Path("series_id") serieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<SerieResponse>
}