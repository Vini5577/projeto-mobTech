package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.MovieDetailActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.SerieDetailActivity
import com.mobtech.mobmovies.data.Favorite
import com.mobtech.mobmovies.data.Movie

class FavoriteAdapter(private val favcrites: List<Favorite>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    @SuppressLint("MissingInflatedId")
    fun bindView(gridLayout: GridLayout, favorites: List<Favorite>) {
        gridLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for ((index, favorite) in favcrites.withIndex()) {
            val view = inflater.inflate(R.layout.card_search, gridLayout, false)
            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            val rating: TextView = view.findViewById(R.id.avalicao_item)
            if(favorite.poster_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500${favorite.poster_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(imageView)
            }
            titleTextView.text = favorite.nome
            rating.text = "${(favorite.vote_average * 10).toInt()}%"

            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(index % 2)
                rowSpec = GridLayout.spec(index / 2)
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(10, 10, 10, 10)
            }

            gridLayout.addView(view, params)

            view.setOnClickListener {
                when (favorite.categoria) {
                    "movie" -> {
                        val intent = Intent(context, MovieDetailActivity::class.java)
                        intent.putExtra("movieId", favorite.id)
                        context.startActivity(intent)
                    }
                    "serie" -> {
                        val intent = Intent(context, SerieDetailActivity::class.java)
                        intent.putExtra("serieId", favorite.id)
                        context.startActivity(intent)
                    }
                    else -> {
                        // Categoria desconhecida
                        Log.e("FavoriteAdapter", "Categoria desconhecida: ${favorite.categoria}")
                    }
                }
            }
        }
    }
}