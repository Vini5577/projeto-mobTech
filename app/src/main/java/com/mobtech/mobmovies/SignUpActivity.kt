package com.mobtech.mobmovies

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.mobtech.mobmovies.databinding.ActivityMainBinding
import com.mobtech.mobmovies.databinding.ActivitySignUpBinding
import com.mobtech.mobmovies.databinding.FragmentMovieBinding
import kotlin.math.log

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextUserName : TextInputEditText
    private lateinit var editTextPassword : TextInputEditText
    private lateinit var editTextPasswordRepeat : TextInputEditText
    private lateinit var editTextEmailRepeat : TextInputEditText
    private lateinit var editTextEmail : TextInputEditText
    private lateinit var btnSignUp : Button

    private lateinit var auth: FirebaseAuth

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

            createAccount(email, password, username)

        })

    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                Toast.makeText(this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, MainActivity::class.java);
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Falha ao adicionar username ao perfil do usuário", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Falha ao criar usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}