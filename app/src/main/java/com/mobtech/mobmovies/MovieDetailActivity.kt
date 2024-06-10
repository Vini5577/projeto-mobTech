package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.adapter.MovieCastAdapter
import com.mobtech.mobmovies.adapter.MovieProviderAdapter
import com.mobtech.mobmovies.adapter.SimilarMovieAdapter
import com.mobtech.mobmovies.service.MovieApiService
import com.mobtech.mobmovies.data.MovieCast
import com.mobtech.mobmovies.data.MovieDetails
import com.mobtech.mobmovies.data.MovieProvider
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.databinding.ActivityMovieDetailBinding
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovieDetailActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener, MovieCastAdapter.OnItemClickListener {

    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieOverview: TextView
    private lateinit var movieReleaseDate: TextView

    private lateinit var api: MovieApiService
    private val TAG: String = "CHECK_RESPONSE"

    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"

    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra("movieId", 0);
        Log.i(TAG, "onResponse: ${movieId}")

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)

        api.getMovieDetails(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    updateUI(movieDetails);
                } else {
                    Log.e(TAG, "Erro ao obter detalhes do filme: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes do filme", t)
            }
        })

        val actorList = binding.actorsList

        api.getMovieCast(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieCast> {
            override fun onResponse(call: Call<MovieCast>, response: Response<MovieCast>) {
                if (response.isSuccessful) {
                    response.body()?.cast?.let { casts ->
                        val adapter = MovieCastAdapter(casts, this@MovieDetailActivity, this@MovieDetailActivity)
                        adapter.bindView(actorList)
                    }
                } else {
                    Log.e(TAG, "Erro ao obter detalhes do filme: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieCast>, t: Throwable) {
                Log.e(TAG, "Falha ao obter detalhes do filme", t)
            }
        })

        val providerList = binding.providerList

        api.getMovieProvider(movieId, API_KEY, "pt-BR").enqueue(object : Callback<MovieProvider> {

            override fun onResponse(call: Call<MovieProvider>, response: Response<MovieProvider>) {
                if (response.isSuccessful) {
                    val movieProvider = response.body()?.results?.get("BR")
                    Log.e(TAG, "Response: ${response.body()}")

                    movieProvider?.let { providerData ->
                        val flatRateDataList = providerData.flatrate
                        Log.e(TAG, "FlatRateDataList: $flatRateDataList")
                        val adapter = MovieProviderAdapter(flatRateDataList, this@MovieDetailActivity)
                        adapter.bindView(providerList)

                        Log.e(TAG, "Teste: $providerData")
                    }
                } else {
                    Log.e(TAG, "Erro ao obter provedores de serviços: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieProvider>, t: Throwable) {
                Log.e(TAG, "Falha ao obter provedores de serviços", t)
            }
        })

        val similarMovies = binding.similarMovie

        api.getSimilarMovie(movieId, API_KEY, "pt-BR").enqueue(object: Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = SimilarMovieAdapter(movies, this@MovieDetailActivity, this@MovieDetailActivity)
                        adapter.bindView(similarMovies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun updateUI(movieDetails: MovieDetails?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val releaseDate = dateFormat.parse(movieDetails?.release_date ?: "") ?: Date()

        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(releaseDate)

        val genres = movieDetails?.genres?.joinToString(", ") { it.name }

        movieDetails?.let {
            binding.MovieTitle.text = it.title
            binding.movieSinopse.text = it.overview
            binding.movieReleaseDate.text = formattedDate
            val text: String = it.runtime?.toString() ?: ""
            val minutesText = if (it.runtime != null && it.runtime > 1) " minutos" else " minuto"
            binding.movieTime.text = text + minutesText
            binding.movieCategories.text = genres

            if(movieDetails.poster_path != null) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${movieDetails.poster_path}")
                    .into(binding.moviePoster)
            } else {
                Glide.with(this)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(binding.moviePoster)
            }

        }
    }

    override fun onItemClick(movieId: Int) {
        Log.d(TAG, "Filme selecionado - ID: $movieId")
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }

    override fun onItemClickPerson(personId: Int) {
        Log.d(TAG, "Filme selecionado - ID: $personId")
        val intent = Intent(this, ActorActivity::class.java)
        intent.putExtra("personId", personId)
        startActivity(intent)
    }

}