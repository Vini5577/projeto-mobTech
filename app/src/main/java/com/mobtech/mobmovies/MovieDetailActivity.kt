package com.mobtech.mobmovies

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.service.MovieApiService
import com.mobtech.mobmovies.fragments.MovieFragment
import com.mobtech.mobmovies.data.Movie
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieOverview: TextView
    private lateinit var movieReleaseDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        moviePoster = findViewById(R.id.moviePoster)
        movieTitle = findViewById(R.id.movieTitle)
        movieOverview = findViewById(R.id.movieOverview)
        movieReleaseDate = findViewById(R.id.movieReleaseDate)

        val movieId = intent.getIntExtra("MOVIE_ID", 0)
        if (movieId != 0) {
            fetchMovieDetails(movieId)
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieApiService = retrofit.create(MovieApiService::class.java)
        movieApiService.getMovieDetails(movieId).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    movie?.let {
                        updateUI(it)
                    }
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                // Tratar falha
            }
        })
    }

    private fun updateUI(movie: Movie) {
        movieTitle.text = movie.title
        movieOverview.text = movie.overview
        movieReleaseDate.text = "Release Date: ${movie.releaseDate}"

        val posterUrl = "https://image.tmdb.org/t/p/w500/${movie.posterPath}"
        Glide.with(this).load(posterUrl).into(moviePoster)
    }
}