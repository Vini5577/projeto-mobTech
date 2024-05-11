package com.mobtech.mobmovies.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.adapter.MovieAdapter
import com.mobtech.mobmovies.adapter.SearchMovieAdapter
import com.mobtech.mobmovies.data.MovieResponse
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.ActivitySearchBinding
import com.mobtech.mobmovies.databinding.FragmentSearchMovieBinding
import com.mobtech.mobmovies.service.MovieApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class SearchMovieFragment() : Fragment() {
    private lateinit var binding: FragmentSearchMovieBinding
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

        binding = FragmentSearchMovieBinding.inflate(layoutInflater)
        val view = binding.root

        val searchLayout = view.findViewById<GridLayout>(R.id.layout_results)

        val inputText: EditText = view.findViewById(R.id.busca_favoritos_movie)

        val searchQuery = requireActivity().intent.getStringExtra("search_query_filmes")

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)

        if (!searchQuery.isNullOrEmpty()) {
            inputText.setText(searchQuery)
            api.searchMovie(inputText.text.toString(), "pt-BR", API_KEY)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (isAdded && response.isSuccessful) {
                            response.body()?.results?.let { movies ->
                                val adapter = SearchMovieAdapter(movies, requireContext())
                                adapter.bindView(searchLayout, movies)
                            }
                        }
                    }

                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        if (isAdded) {
                            Log.i(TAG, "onFailure: ${t.message}")
                        }
                    }
                })
        }

        requireActivity().intent.putExtra("search_query_filmes", "")

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText.isNotEmpty() && isAdded) {

                    api.searchMovie(searchText, "pt-BR", API_KEY)
                        .enqueue(object : Callback<MovieResponse> {
                            override fun onResponse(
                                call: Call<MovieResponse>,
                                response: Response<MovieResponse>
                            ) {
                                if (isAdded && response.isSuccessful) {
                                    response.body()?.results?.let { movies ->
                                        val adapter = SearchMovieAdapter(movies, requireContext())
                                        adapter.bindView(searchLayout, movies)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchMovieFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchMovieFragment().apply {

            }
    }
}