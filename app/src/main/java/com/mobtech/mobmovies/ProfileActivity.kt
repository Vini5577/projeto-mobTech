package com.mobtech.mobmovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.mobtech.mobmovies.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnFavorite: TextView
    private lateinit var btnChangePassword: TextView
    private lateinit var btnExit: TextView
    private lateinit var btnBackButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java);
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val currenteUser = auth.currentUser

        if(currenteUser != null) {
            val uid = currenteUser.uid
            val db = FirebaseFirestore.getInstance()

            if(uid != null) {
                val userRef = db.collection("users").document(uid)
                userRef.get().addOnSuccessListener { document ->
                    if(document.exists()) {
                        val username = document.getString("username")
                        val email = document.getString("email")

                        binding.nameProfile.text = username
                        binding.emailProfile.text = email
                    }
                }
            }
        }

        btnBackButton = binding.backButton
        btnFavorite = binding.btnFavorite
        btnChangePassword = binding.btnChangePassword
        btnExit = binding.btnExit

        btnBackButton.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnFavorite.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("favoriteScreen", 2)
            startActivity(intent)
            finish()
        })

        btnChangePassword.setOnClickListener({
            val intent = Intent(applicationContext, ChangePasswordActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnExit.setOnClickListener({
            auth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

    }
}