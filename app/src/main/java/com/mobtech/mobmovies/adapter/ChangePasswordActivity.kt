package com.mobtech.mobmovies.adapter

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mobtech.mobmovies.LoginActivity
import com.mobtech.mobmovies.R
import com.mobtech.mobmovies.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val backButton = binding.backButton
        val changePassword = binding.btnChangePassword

        changePassword.setOnClickListener({
            val currentPassword = binding.currentPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val repeatPassword = binding.repeatPassword.text.toString()

            if(TextUtils.isEmpty(currentPassword) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(repeatPassword) ) {
                Toast.makeText(this, "Todos os campos são obrigatorios",
                    Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            if(!newPassword.equals(repeatPassword.toString()))  {
                Toast.makeText(this, "As senhas devem ser iguais!",
                    Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            val user = auth.currentUser


            if(user != null && user.email.toString() != null) {
                val credential = EmailAuthProvider.getCredential(user.email.toString(), currentPassword)

                user?.reauthenticate(credential)?.addOnCompleteListener({
                    if(it.isSuccessful) {

                        user!!.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Senha alterada com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                    auth.signOut()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }
                            }
                    } else {
                        Toast.makeText(this, "Senha atual icorreta!",
                            Toast.LENGTH_SHORT).show();
                    }
                })
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        })

        backButton.setOnClickListener({
            onBackPressed()
        })
    }
}