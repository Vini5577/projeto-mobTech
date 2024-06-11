package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobtech.mobmovies.LoginActivity
import com.mobtech.mobmovies.R


class CommentActivity : AppCompatActivity() {

    private lateinit var commentEditText: TextInputEditText
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var contentId: String
    private lateinit var contentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        contentId = intent.getStringExtra("CONTENT_ID") ?: ""
        contentType = intent.getStringExtra("CONTENT_TYPE") ?: "movie" // Padrão para filme
        commentEditText = comment_edit_text


        send_comment_button.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotBlank()) {
                saveComment(commentText)
                commentEditText.setText("")
            }
        }
    }

    private fun saveComment(commentText: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val commentId = db.collection("comments").document().id
            val comment = hashMapOf(
                "commentId" to commentId,
                "userId" to currentUser.uid,
                "contentId" to contentId,
                "contentType" to contentType,
                "commentText" to commentText,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("comments").document(commentId)
                .set(comment)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("CommentActivity", "Erro ao salvar comentário", e)
                }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}