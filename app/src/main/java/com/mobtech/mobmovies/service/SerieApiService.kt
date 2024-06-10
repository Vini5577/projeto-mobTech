package com.mobtech.mobmovies.service

import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.data.SerieCast
import com.mobtech.mobmovies.data.SerieDetails
import com.mobtech.mobmovies.data.SerieProvider
import retrofit2.Call
import com.mobtech.mobmovies.data.SerieResponse
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

    @GET("search/tv")
    fun searchSerie(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<SerieResponse>

    @GET("tv/{id}")
    fun getSerieDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<SerieDetails>

    @GET("tv/{serie_id}/credits")
    fun getSerieCast(
        @Path("serie_id") serieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<SerieCast>

    @GET("tv/{serie_id}/watch/providers")
    fun getSerieProvider(
        @Path("serie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<SerieProvider>

    @GET("tv/{serie_id}/similar")
    fun getSimilarSerie(
        @Path("serie_id") serieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<SerieResponse>
}