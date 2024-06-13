package com.mobtech.mobmovies.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.ColorRating
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Serie

class SerieAdapter(private val series: List<Serie>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClickSerie(serieId: Int)
    }

    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)

        for(serie in series) {
            val view = inflater.inflate(R.layout.item_card, linearLayout, false)
            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            val rating: TextView = view.findViewById(R.id.avalicao_item)
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
            rating.text = "${(serie.vote_average * 10).toInt()}%"

            val color = ColorRating().getColorForRating((serie.vote_average * 10).toInt())
            val background = GradientDrawable()
            background.shape = GradientDrawable.OVAL
            background.setColor(android.graphics.Color.parseColor("#4DDADADA"))
            background.setStroke(2, color)
            border.background = background

            linearLayout.addView(view)

            view.setOnClickListener {
                listener.onItemClickSerie(serie.id)
            }
        }
    }
}