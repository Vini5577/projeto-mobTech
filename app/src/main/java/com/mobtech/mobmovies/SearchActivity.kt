package com.mobtech.mobmovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentSearchAdapter
    private lateinit var icon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager2)

        adapter = FragmentSearchAdapter(this)

        viewPager2.adapter = adapter
        viewPager2.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when(position) {
                0 -> "Filmes"
                1 -> "Séries"
                2 -> "Favoritos"
                else -> null
            }
        }.attach()

        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        val tabIndex = intent.getIntExtra("fragmentIndex", 0)

        tabLayout.getTabAt(tabIndex)?.select()

        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setOnClickListener {
            finish()
        }

        icon = findViewById(R.id.icon_profile_search)

        icon.setOnClickListener({
            val loginScreen = Intent(this, LoginActivity::class.java)
            startActivity(loginScreen)
        })

    }
}