package com.mobtech.mobmovies.adapter

import android.content.Context
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(private val comments: List<Comment>, private val context: Context) {

    fun bindView(linearLayout: LinearLayout) {
        linearLayout.removeAllViews();
        val inflater = LayoutInflater.from(context)
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        for (comment in comments) {
            val view = inflater.inflate(R.layout.commetery_card, null)

            val titleTextView: TextView = view.findViewById(R.id.user)
            val dateComment: TextView = view.findViewById(R.id.date)
            val commentary: TextView = view.findViewById(R.id.commentary)

            val date = inputDateFormat.parse(comment.data_hora.toString())
            titleTextView.text = comment.username
            dateComment.text = outputDateFormat.format(date)
            commentary.text = comment.comentario

            linearLayout.addView(view)
        }
    }
}