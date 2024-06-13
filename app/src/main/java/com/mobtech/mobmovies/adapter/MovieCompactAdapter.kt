package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.ColorRating
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.MovieCredit

class MovieCompactAdapter(private val personData: List<MovieCredit>, private val context: Context, private val listener: OnItemClickListener) {

    interface OnItemClickListener {
        fun onItemClick(movieId: Int)
    }

    @SuppressLint("MissingInflatedId")
    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)
        for (movie in personData) {
            val view = inflater.inflate(R.layout.item_card_compact, linearLayout, false)

            val imageView: ImageView = view.findViewById(R.id.image_post)
            val titleTextView: TextView = view.findViewById(R.id.title_template)
            val border: LinearLayout = view.findViewById(R.id.border)

            if(movie.poster_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://www.movienewz.com/img/films/poster-holder.jpg")
                    .into(imageView)
            }

            val rating: TextView = view.findViewById(R.id.avalicao_item)
            rating.text = "${(movie.vote_average * 10).toInt()}%"
            titleTextView.text = movie.title

            val color = ColorRating().getColorForRating((movie.vote_average * 10).toInt())
            val background = GradientDrawable()
            background.shape = GradientDrawable.OVAL
            background.setColor(android.graphics.Color.parseColor("#4DDADADA"))
            background.setStroke(2, color)
            border.background = background

            linearLayout.addView(view)

            view.setOnClickListener {
                listener.onItemClick(movie.id)
            }
        }
    }
}