package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.data.Movie
import com.mobtech.mobmovies.R

class MovieAdapter(private val movies: List<Movie>, private val context: Context) {

    @SuppressLint("MissingInflatedId")
    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)
        for (movie in movies) {
            val view = inflater.inflate(R.layout.item_movie, linearLayout, false)
            val imageView: ImageView = view.findViewById(R.id.trending_image)
            val titleTextView: TextView = view.findViewById(R.id.treding_nome)
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(imageView)
            titleTextView.text = movie.title
            linearLayout.addView(view)
        }
    }
}