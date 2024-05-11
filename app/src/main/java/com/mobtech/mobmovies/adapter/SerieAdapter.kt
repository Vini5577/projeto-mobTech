package com.mobtech.mobmovies.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Serie

class SerieAdapter(private val series: List<Serie>, private val context: Context) {

    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)

        for(serie in series) {
            val view = inflater.inflate(R.layout.item_card, linearLayout, false)
            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500${serie.poster_path}")
                .into(imageView)
            titleTextView.text = serie.name
            linearLayout.addView(view)
        }
    }
}