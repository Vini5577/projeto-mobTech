package com.mobtech.mobmovies.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SearchActivity
import com.mobtech.mobmovies.SerieDetailActivity
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.adapter.SerieAdapter
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.data.SerieResponse
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.FragmentFavoritosBinding
import com.mobtech.mobmovies.databinding.FragmentSeriesBinding
import com.mobtech.mobmovies.service.MovieApiService
import com.mobtech.mobmovies.service.SerieApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SeriesFragment : Fragment(),SerieAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSeriesBinding
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var api: SerieApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerieApiService::class.java)

        binding = FragmentSeriesBinding.inflate(layoutInflater);
        val view = binding.root


       // val view = inflater.inflate(R.layout.fragment_series, container, false)
        val trendingLayout = view.findViewById<LinearLayout>(R.id.trending_layout_serie)

        api.getTrendingSeries("pt-BR", API_KEY).enqueue(object : Callback<SerieResponse>{
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { series ->
                        val adapter = SerieAdapter(series, requireContext(), this@SeriesFragment)
                        adapter.bindView(trendingLayout)
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val topRatedSeries = view.findViewById<LinearLayout>(R.id.top_rated_series)
        api.getTopRatedSeries("pt-BR", API_KEY).enqueue(object : Callback<SerieResponse>{
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { series ->
                        val adapter = SerieAdapter(series, requireContext(), this@SeriesFragment)
                        adapter.bindView(topRatedSeries)
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        val airingTodayLayout = view.findViewById<LinearLayout>(R.id.airing_today_layout_serie)
        api.getAiringToday("pt-BR", API_KEY).enqueue(object : Callback<SerieResponse>{
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { series ->
                        val adapter = SerieAdapter(series, requireContext(), this@SeriesFragment)
                        adapter.bindView(airingTodayLayout)
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })

        loadRecommendations();

        val inputText: EditText = view.findViewById(R.id.busca_serie)

        val listener = View.OnClickListener {
            if (inputText.text.isBlank()) {
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
                val bundle = Bundle()
                bundle.putInt("fragmentIndex", 1)
                intent.putExtras(bundle)
                intent.putExtra("search_query_series", inputText.text.toString())
                startActivity(intent)
            }
        }

        inputText.setOnClickListener(listener)

        return view
    }

    private fun loadRecommendations() {
        val view = binding.root

        val serieId = (1..1000).random()

        api.getRecommendationsSerie(serieId,"pt-BR", API_KEY).enqueue(object : Callback<SerieResponse>{
            override fun onResponse(call: Call<SerieResponse>, response: Response<SerieResponse>) {
                if (response.isSuccessful) {
                    val recommendations = response.body()?.results
                    if (recommendations != null && recommendations.isNotEmpty()) {
                        val randomSerie = recommendations.random()

                        val recomendacaoSerie =
                            view.findViewById<ShapeableImageView>(R.id.recomendacao_serie)
                        val recomendacaoTexto = view.findViewById<TextView>(R.id.recomendacao_texto_serie)
                        val recomendacaoAvaliacao = view.findViewById<TextView>(R.id.recomendacao_avaliacao_serie)
                        val serieRecomendation = view.findViewById<FrameLayout>(R.id.serie_recomendation)

                        Glide.with(requireContext())
                            .load("https://image.tmdb.org/t/p/w500${randomSerie.poster_path}")
                            .into(recomendacaoSerie)

                        serieRecomendation.setOnClickListener {
                            val intent = Intent(requireContext(), SerieDetailActivity::class.java)
                            intent.putExtra("serieId", randomSerie.id)
                            startActivity(intent)
                        }

                        recomendacaoTexto.text = randomSerie.name
                        recomendacaoAvaliacao.text = "${(randomSerie.vote_average * 10).toInt()}%"
                    } else {
                        Log.d(TAG, "Lista de recomendações vazia")
                    }
                }
            }

            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun onItemClickSerie(serieId: Int) {
        val intent = Intent(requireContext(), SerieDetailActivity::class.java)
        intent.putExtra("serieId", serieId)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SeriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}