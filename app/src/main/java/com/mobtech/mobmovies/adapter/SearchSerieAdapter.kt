package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.ColorRating
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Movie
import com.mobtech.mobmovies.data.Serie

class SearchSerieAdapter(private val series: List<Serie>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClick(serieId: Int)
    }

    @SuppressLint("MissingInflatedId")
    fun bindView(gridLayout: GridLayout, series: List<Serie>) {
        gridLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for ((index, serie) in series.withIndex()) {
            val view = inflater.inflate(R.layout.card_search, gridLayout, false)
            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            val border: LinearLayout = view.findViewById(R.id.border)

            if(serie.poster_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500${serie.poster_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(imageView)
            }
            titleTextView.text = serie.name

            val rating: TextView = view.findViewById(R.id.avalicao_item)
            rating.text = "${(serie.vote_average * 10).toInt()}%"

            val params = GridLayout.LayoutParams().apply {
                columnSpec = GridLayout.spec(index % 2)
                rowSpec = GridLayout.spec(index / 2)
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(10, 10, 10, 10)
            }

            val color = ColorRating().getColorForRating((serie.vote_average * 10).toInt())
            val background = GradientDrawable()
            background.shape = GradientDrawable.OVAL
            background.setColor(android.graphics.Color.parseColor("#4DDADADA"))
            background.setStroke(2, color)
            border.background = background

            gridLayout.addView(view, params)

            view.setOnClickListener {
                listener.onItemClick(serie.id)
            }
        }
    }
}