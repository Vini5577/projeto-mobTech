package com.mobtech.mobmovies

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobtech.mobmovies.databinding.ActivityChangeEmailBinding

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}