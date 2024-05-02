package com.mobtech.mobmovies.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SearchActivity
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.service.MovieApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MovieFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieFragment : Fragment() {

    private lateinit var binding: ActivityMainBinding
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"
    private val TAG: String = "CHECK_RESPONSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)

        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        val trendingLayout = view.findViewById<LinearLayout>(R.id.trending_layout)

        api.getTrendingMovies("pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = MovieAdapter(movies, requireContext())
                        adapter.bindView(trendingLayout)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val topRatedLayout = view.findViewById<LinearLayout>(R.id.top_rated_Layout)

        api.getTopRatedMovies("pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = MovieAdapter(movies, requireContext())
                        adapter.bindView(topRatedLayout)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val upComingLayout = view.findViewById<LinearLayout>(R.id.upcoming_layout)

        api.getUpComingMovies("pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = MovieAdapter(movies, requireContext())
                        adapter.bindView(upComingLayout)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val playingLayout = view.findViewById<LinearLayout>(R.id.playing_layout)

        api.getNowPlaying("pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = MovieAdapter(movies, requireContext())
                        adapter.bindView(playingLayout)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val movieId = (1..1000).random()

        api.getRecommendationsMovie(movieId,"pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val recommendations = response.body()?.results
                    if (recommendations != null && recommendations.isNotEmpty()) {
                        val randomMovie = recommendations.random()

                        val recomendacaoFilme =
                            view.findViewById<ShapeableImageView>(R.id.recomendacao_filme)
                        val recomendacaoTexto = view.findViewById<TextView>(R.id.recomendacao_texto)
                        val recomendacaoAvaliacao = view.findViewById<TextView>(R.id.recomendacao_avaliacao)

                        Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w500${randomMovie.poster_path}")
                            .into(recomendacaoFilme)
                        recomendacaoTexto.text = randomMovie.title
                        recomendacaoAvaliacao.text = "${(randomMovie.vote_average * 10).toInt()}%"
                    } else {
                        Log.d(TAG, "Lista de recomendações vazia")
                    }


                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val inputText: EditText = view.findViewById(R.id.busca_filme)

        inputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
                true
            } else {
                false
            }
        }


        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MovieFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MovieFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}