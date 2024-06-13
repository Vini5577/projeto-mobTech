package com.mobtech.mobmovies

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mobtech.mobmovies.adapter.CommentAdapter
import com.mobtech.mobmovies.databinding.ActivityMoreCommentsBinding

class MoreCommentsActivity : AppCompatActivity() {

    lateinit var binding: ActivityMoreCommentsBinding

    private var contentId: Int = 0
    private lateinit var contentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageView = binding.backButton

        backButton.setOnClickListener({
            onBackPressed()
        })

        contentId = intent.getStringExtra("CONTENT_ID")?.toIntOrNull() ?: 0
        contentType = intent.getStringExtra("CONTENT_TYPE") ?: ""

        if (contentId == 0) {
            Log.e(ContentValues.TAG, "Content ID is not provided in the Intent")
            finish()
        }

        getComment(contentId, contentType) { comments ->
            val commentAdapter = CommentAdapter(comments, this)
            commentAdapter.bindView(binding.commentaryBox)
        }
    }

    private fun getComment(id: Int, contentType: String, callback: (List<Comment>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comentarios")
            .whereEqualTo("contentId", id)
            .whereEqualTo("contentType", contentType)
            .orderBy("data_hora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.w("CHECK RESPONSE", "Erro ao obter comentários", exception)
                    return@addSnapshotListener
                }

                val comments = mutableListOf<Comment>()
                snapshot?.forEach { document ->
                    val username = document.getString("username")
                    val comentario = document.getString("comentario")
                    val dataHora = document.get("data_hora")

                    if (username != null && comentario != null && dataHora != null) {
                        comments.add(Comment(username, comentario, dataHora))
                    }
                }
                callback(comments)
            }
    }
}