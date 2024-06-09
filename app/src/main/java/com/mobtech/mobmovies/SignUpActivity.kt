package com.mobtech.mobmovies

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.ActivitySignUpBinding
import com.mobtech.mobmovies.databinding.FragmentMovieBinding
import kotlin.math.log

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextUserName : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var editTextPasswordRepeat : EditText
    private lateinit var editTextEmailRepeat : EditText
    private lateinit var editTextEmail : EditText
    private lateinit var btnSignUp : TextView
    private lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var binding: ActivitySignUpBinding

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, LoginActivity::class.java);
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore =FirebaseFirestore.getInstance()
        setContentView(binding.root)


        editTextUserName = binding.username
        editTextEmail = binding.email
        editTextEmailRepeat = binding.emailRepeat
        editTextPassword = binding.password
        editTextPasswordRepeat = binding.passwordRepeat
        btnSignUp = binding.btnRegister

        btnSignUp.setOnClickListener({
            val username: String = editTextUserName.text.toString()
            val email: String = editTextEmail.text.toString()
            val emailRepeat: String = editTextEmailRepeat.text.toString()
            val password: String = editTextPassword.text.toString()
            val passwordRepeat: String = editTextPasswordRepeat.text.toString()

            if(TextUtils.isEmpty(username) &&
                TextUtils.isEmpty(email) && TextUtils.isEmpty(emailRepeat) &&
                TextUtils.isEmpty(password) && TextUtils.isEmpty(passwordRepeat)
                ) {
                Toast.makeText(this, "Todos os campos são obrigatorios",
                    Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            if (!email.equals(emailRepeat) || !password.equals(passwordRepeat)) {
                Toast.makeText(this, "Os campos de email e senha devem ser iguais", Toast.LENGTH_SHORT).show()
                Log.d("SignUpActivity", "Email: $email, Email Repeat: $emailRepeat, Password: $password, Password Repeat: $passwordRepeat")
                return@setOnClickListener
            }

            checkIfUserExists(email, password, username)
        })

        backButton = binding.backButton

        backButton.setOnClickListener({
            val intent = Intent(applicationContext, LoginActivity::class.java);
            startActivity(intent)
            finish()
        })
    }

    private fun checkIfUserExists(email: String, password: String, username: String) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    createAccount(email, password, username)
                } else {
                    Toast.makeText(this,
                        "Usuario ou e-mail já registrado.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    "Falha ao verificar email: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                Log.e("SignUpActivity", "Erro ao verificar email no Firestore", e)
            }
    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userMap = hashMapOf(
                            "uid" to it.uid,
                            "username" to username,
                            "email" to email
                        )

                        firestore.collection("users").document(it.uid).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Falha ao adicionar usuário no Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("SignUpActivity", "Erro ao adicionar usuário no Firestore", e)
                            }
                    }
                } else {
                    Toast.makeText(this, "Falha ao criar usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", "Erro ao criar usuário", task.exception)
                }
            }
    }
}
