package com.mobtech.mobmovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.mobtech.mobmovies.databinding.ActivityLoginBinding

import com.mobtech.mobmovies.databinding.ActivityProfileBinding
import kotlin.system.exitProcess

class ProfileActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private lateinit var icon: ImageView
    private lateinit var btnFavorite: TextView
    private lateinit var btnChangeEmail: TextView
    private lateinit var btnChangePassword: TextView
    private lateinit var btnExit: TextView
    private lateinit var btnBackButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding

    override fun onStart() {
        super.onStart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        btnBackButton = binding.backButton
        btnFavorite = binding.btnFavorite
        btnChangeEmail = binding.btnChangeEmail
        btnChangePassword = binding.btnChangePassword
        btnExit = binding.btnExit

        btnBackButton.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnFavorite.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnChangeEmail.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        btnChangePassword.setOnClickListener({
            val intent = Intent(applicationContext, MainActivity::class.java)
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