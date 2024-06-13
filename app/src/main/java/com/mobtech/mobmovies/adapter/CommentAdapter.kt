package com.mobtech.mobmovies.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobtech.mobmovies.Comment
import com.mobtech.mobmovies.R

class CommentAdapter(private val comments: List<Comment>, private val context: Context) {

    fun bindView(linearLayout: LinearLayout) {

        val inflater = LayoutInflater.from(context)

        for (comment in comments) {
            val view = inflater.inflate(R.layout.commetery_card, linearLayout, false)

            val titleTextView: TextView = view.findViewById(R.id.user)
            val dateComment: TextView = view.findViewById(R.id.date)
            val commentary: TextView = view.findViewById(R.id.commentary)


            titleTextView.text = comment.username
            dateComment.text = comment.data_hora.toString()
            commentary.text = comment.comentario

            linearLayout.addView(view)
        }
    }
}