package com.mobtech.mobmovies.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.data.FlatRateData
import com.mobtech.mobmovies.data.ProviderData

class MovieProviderAdapter(private val movieProvider: List<FlatRateData>, private val context: Context) {

    fun bindView(linearLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)

        for (provider in movieProvider) {
            val view = inflater.inflate(R.layout.provider_card, linearLayout, false)
            val imageView: ImageView = view.findViewById(R.id.img_provider)
            val textView: TextView = view.findViewById(R.id.txt_provider)

            if(provider.logo_path != null) {
                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w185${provider.logo_path}")
                    .into(imageView)

                imageView.visibility = View.VISIBLE
                textView.visibility = View.GONE
            } else {
                textView.text = "Não disponível em nenhum streaming"
                imageView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }

            linearLayout.addView(view)
        }
    }
}
