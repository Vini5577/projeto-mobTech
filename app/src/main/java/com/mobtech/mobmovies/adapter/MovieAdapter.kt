package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.data.Movie
import com.mobtech.mobmovies.R

class MovieAdapter(private val movies: List<Movie>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClick(movieId: Int)
    }

    @SuppressLint("MissingInflatedId")
    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)
        for (movie in movies) {
            val view = inflater.inflate(R.layout.item_card, linearLayout, false)

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
            linearLayout.addView(view)

            // Adiciona o OnClickListener à view do item do filme
            view.setOnClickListener {
                listener.onItemClick(movie.id)
            }
        }
    }
}