package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.Cast


class MovieCastAdapter(private val movieCast: List<Cast>, private val context: Context) {

    @SuppressLint("MissingInflatedId")
    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)

        for(cast in movieCast) {
            val view = inflater.inflate(R.layout.cast_card, linearLayout, false)
            val imageView: ImageView = view.findViewById(R.id.cast_image)
            val name: TextView = view.findViewById(R.id.actorName)
            val character: TextView = view.findViewById(R.id.character)

            if(cast.profile_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w185${cast.profile_path}")
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3HrKlpj9CgB10PH4EfObR0TOR_pT99Y8szryJU0zqiDrh_1xlVEzm0l07TmFwEs4STPA&usqp=CAU")
                    .into(imageView)
            }

            name.text = cast.name
            character.text = cast.character
            linearLayout.addView(view)
        }
    }
}