package com.mobtech.mobmovies.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SearchActivity
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.databinding.FragmentMovieBinding
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
class MovieFragment : Fragment(), MovieAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMovieBinding
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var api: MovieApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)

        binding = FragmentMovieBinding.inflate(layoutInflater)
        val view = binding.root

        val trendingLayout = view.findViewById<LinearLayout>(R.id.trending_layout)

        api.getTrendingMovies("pt-BR", API_KEY).enqueue(object : Callback<MovieResponse>{
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { movies ->
                        val adapter = MovieAdapter(movies, requireContext(), this@MovieFragment)
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
                        val adapter = MovieAdapter(movies, requireContext(), this@MovieFragment)
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
                        val adapter = MovieAdapter(movies, requireContext(), this@MovieFragment)
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
                        val adapter = MovieAdapter(movies, requireContext(), this@MovieFragment)
                        adapter.bindView(playingLayout)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        loadRecommendations()

        val inputText: EditText = view.findViewById(R.id.busca_filme)

        val listener = View.OnClickListener {
            if (inputText.text.isBlank()) {
                // Se o texto estiver vazio, exibir um diálogo
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Atenção")
                alertDialogBuilder.setMessage("O campo de busca está vazio. Por favor, insira um termo de busca.")
                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                intent.putExtra("search_query_filmes", inputText.text.toString())
                startActivity(intent)
            }
        }

        inputText.setOnClickListener(listener)


        return view
    }

    private fun loadRecommendations() {
        val view = binding.root

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

                        if(TextUtils.isEmpty(randomMovie.poster_path)) {
                            Glide.with(requireContext())
                                .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                                .into(recomendacaoFilme)
                        } else {
                            Glide.with(requireContext())
                                .load("https://image.tmdb.org/t/p/w500${randomMovie.poster_path}")
                                .into(recomendacaoFilme)
                        }

                        recomendacaoTexto.text = randomMovie.title
                        recomendacaoAvaliacao.text = "${(randomMovie.vote_average * 10).toInt()}%"
                    } else {
                        Log.d(TAG, "Lista de recomendações vazia, fazendo nova requisição")
                        loadRecommendations()
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun onItemClick(movieId: Int) {
        Log.d(TAG, "Filme selecionado - ID: $movieId")
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
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