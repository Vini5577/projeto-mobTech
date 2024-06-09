package com.mobtech.mobmovies.service

import com.mobtech.mobmovies.data.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("trending/movie/day")
    fun getTrendingMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("movie/")

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getUpComingMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("movie/now_playing")
    fun getNowPlaying(
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendationsMovie(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>

    @GET("search/movie")
    fun searchMovie(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Call<MovieResponse>
}