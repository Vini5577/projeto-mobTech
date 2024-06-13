package com.mobtech.mobmovies

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentActivity : AppCompatActivity() {

    private lateinit var commentEditText: TextInputEditText

    private var contentId: Int = 0
    private lateinit var contentType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val sendCommentButton: Button = findViewById(R.id.commentButton)
        commentEditText = findViewById(R.id.commentTextBox)

        val backButton: ImageView = findViewById(R.id.backButton)

        backButton.setOnClickListener({
            onBackPressed()
        })

        contentId = intent.getStringExtra("CONTENT_ID")?.toIntOrNull() ?: 0
        contentType = intent.getStringExtra("CONTENT_TYPE") ?: "movie"

        Log.d(TAG, "Content ID: $contentId, Content Type: $contentType")

        if (contentId == 0) {
            // Handle error case where content ID is not provided
            Log.e(TAG, "Content ID is not provided in the Intent")
            finish() // End the activity if no valid ID is provided
        }

        sendCommentButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            if (commentText.isNotBlank()) {
                saveComment(commentText)
                commentEditText.setText("")
            }
        }
    }

    private fun saveComment(commentText: String) {
        val currentUser = Firebase.auth.currentUser
        val userEmail = currentUser?.email
        val userId = currentUser?.uid

        if (userId == null || userEmail == null) {
            Log.e(TAG, "User not logged in or missing email/uid")
            return
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = sdf.format(Date())

        Firebase.firestore.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val username = document.getString("username")
                    if (username != null) {
                        val comentariosRef = Firebase.firestore.collection("comentarios")
                        val comentarioData: Map<String, Any> = mapOf(
                            "username" to username as String,
                            "comentario" to commentText as String,
                            "data_hora" to formattedDate as String,
                            "contentId" to contentId.toInt(),
                            "contentType" to contentType as String,
                        )

                        comentariosRef.add(comentarioData)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "Comentário adicionado com ID: ${documentReference.id}")
                                // Update the comment with its ID
                                comentariosRef.document(documentReference.id)
                                    .update("commentId", documentReference.id)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Erro ao adicionar comentário", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao recuperar usuário", e)
            }
    }
}
