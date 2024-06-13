package com.mobtech.mobmovies.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SerieDetailActivity
import com.mobtech.mobmovies.adapter.SearchMovieAdapter
import com.mobtech.mobmovies.adapter.SearchSerieAdapter
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.data.SerieResponse
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.ActivitySearchBinding
import com.mobtech.mobmovies.databinding.FragmentSearchSeriesBinding
import com.mobtech.mobmovies.service.MovieApiService
import com.mobtech.mobmovies.service.SerieApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Use the [SearchSeriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchSeriesFragment : Fragment(), SearchSerieAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentSearchSeriesBinding
    private val BASE_URL = "https://api.themoviedb.org/3/"
    private val API_KEY = "92f5a194730faec7789a4c569d9ca999"
    private val TAG: String = "CHECK_RESPONSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchSeriesBinding.inflate(layoutInflater)
        val view = binding.root

        val searchLayout = view.findViewById<GridLayout>(R.id.layout_results)

        val inputText: EditText = view.findViewById(R.id.busca_favoritos_series)

        val searchQuery = requireActivity().intent.getStringExtra("search_query_series")

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerieApiService::class.java)

        if (!searchQuery.isNullOrEmpty()) {
            inputText.setText(searchQuery)
            api.searchSerie(inputText.text.toString(), "pt-BR", API_KEY)
                .enqueue(object : Callback<SerieResponse> {
                    override fun onResponse(
                        call: Call<SerieResponse>,
                        response: Response<SerieResponse>
                    ) {
                        if (isAdded && response.isSuccessful) {
                            response.body()?.results?.let { series ->
                                val adapter = SearchSerieAdapter(series, requireContext(), this@SearchSeriesFragment)
                                adapter.bindView(searchLayout, series)
                            }
                        }
                    }

                    override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                        if (isAdded) {
                            Log.i(TAG, "onFailure: ${t.message}")
                        }
                    }
                })
        }

        requireActivity().intent.putExtra("search_query_series", "")

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty() && isAdded) {

                    api.searchSerie(searchText, "pt-BR", API_KEY)
                        .enqueue(object : Callback<SerieResponse> {
                            override fun onResponse(
                                call: Call<SerieResponse>,
                                response: Response<SerieResponse>
                            ) {
                                if (isAdded && response.isSuccessful) {
                                    response.body()?.results?.let { series ->
                                        val adapter = SearchSerieAdapter(series, requireContext(), this@SearchSeriesFragment)
                                        adapter.bindView(searchLayout, series)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<SerieResponse>, t: Throwable) {
                                if (isAdded) {
                                    Log.i(TAG, "onFailure: ${t.message}")
                                }
                            }
                        })
                } else {
                    searchLayout.removeAllViews()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    override fun onItemClick(serieId: Int) {
        val intent = Intent(requireContext(), SerieDetailActivity::class.java)
        intent.putExtra("serieId", serieId)
        startActivity(intent)
    }
}