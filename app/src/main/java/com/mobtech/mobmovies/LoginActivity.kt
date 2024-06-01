package com.mobtech.mobmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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
import com.google.firebase.auth.FirebaseUser
import com.mobtech.mobmovies.databinding.ActivityLoginBinding

class LoginActivity() : AppCompatActivity() {

    private lateinit var editTextEmail : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var btnSignup: Button
    private lateinit var btnLogin: Button
    private lateinit var forgotPassword: TextView
    private lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java);
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        btnSignup = binding.btnCadastro
        editTextEmail = binding.email
        editTextPassword = binding.password
        btnLogin = binding.btnLogin

        forgotPassword = binding.passwordForgot

        btnSignup.setOnClickListener({
            val signUpScreen = Intent(this, SignUpActivity::class.java)
            startActivity(signUpScreen);
        })

        btnLogin.setOnClickListener({
            val email: String = editTextEmail.text.toString()
            val password: String = editTextPassword.text.toString()

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Todos os campos são obrigatorios",
                    Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent = Intent(applicationContext, MainActivity::class.java);
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "E-mail ou senha está incorreto",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        })

        forgotPassword.setOnClickListener({
            val intent = Intent(applicationContext, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        })

        backButton = binding.backButton

        backButton.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}