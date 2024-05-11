package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Movie

class SearchMovieAdapter(private val movies: List<Movie>, private val context: Context) {


    @SuppressLint("MissingInflatedId")
    fun bindView(gridLayout: GridLayout, movies: List<Movie>) {
        gridLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for ((index, movie) in movies.withIndex()) {
            val view = inflater.inflate(R.layout.card_search, gridLayout, false)
            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            if(movie.poster_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(imageView)
            }
            titleTextView.text = movie.title

            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(index % 2)
                rowSpec = GridLayout.spec(index / 2)
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(10, 10, 10, 10)
            }

            gridLayout.addView(view, params)
        }
    }
}