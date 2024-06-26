package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobtech.mobmovies.databinding.ActivityLoginBinding
import com.mobtech.mobmovies.databinding.ActivityPasswordRecoveryBinding

class PasswordRecoveryActivity : AppCompatActivity() {

    lateinit var email: EditText;
    lateinit var username: EditText;
    lateinit var button: TextView;
    private lateinit var backButton: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    lateinit var binding: ActivityPasswordRecoveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setContentView(binding.root)

        email = binding.email
        username = binding.username
        button = binding.btn

        button.setOnClickListener({
            val emailText: String = email.text.toString().trim()
            val usernameText: String = username.text.toString().trim()

            if(TextUtils.isEmpty(emailText) && TextUtils.isEmpty(usernameText)){
                Toast.makeText(this, "Todos os campos são obrigatorios",
                    Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            validateUsernamAndEmail(usernameText, emailText)
        })

        backButton = binding.backButton

        backButton.setOnClickListener({
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun validateUsernamAndEmail (username: String, email: String) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty) {
                    Toast.makeText(this,
                        "Usuário ou E-mail estão incorretos",
                        Toast.LENGTH_SHORT).show()
                } else {
                    ResetPassword(email)
                }
            }
            .addOnFailureListener({
                Toast.makeText(this,
                    "Usuário ou E-mail não cadastrado",
                    Toast.LENGTH_SHORT).show()
            })
    }

    private fun ResetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            Toast.makeText(this,
                "Senha de redifinação de senha foi mandado para o Email",
                Toast.LENGTH_SHORT).show();
            finish()
        }.addOnFailureListener({
            Toast.makeText(this,
                "Usuario ou E-mail estão incorretos",
                Toast.LENGTH_SHORT).show();
        })
    }
}