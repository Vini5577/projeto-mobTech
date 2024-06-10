package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Movie
import com.mobtech.mobmovies.data.Serie

class SimilarSerieAdapter (private val series: List<Serie>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClick(serieId: Int)
    }

    @SuppressLint("MissingInflatedId")
    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)
        for (serie in series) {
            val view = inflater.inflate(R.layout.item_card_compact, linearLayout, false)

            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            if(serie.poster_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500${serie.poster_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(imageView)
            }

            Log.e("OPAAA", "CHEGUEI AQUIIIII")

            titleTextView.text = serie.name
            linearLayout.addView(view)

            view.setOnClickListener {
                listener.onItemClick(serie.id)
            }
        }
    }
}