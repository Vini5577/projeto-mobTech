package com.mobtech.mobmovies

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobtech.mobmovies.fragments.FavoritosFragment
import com.mobtech.mobmovies.fragments.MovieFragment
import com.mobtech.mobmovies.fragments.SeriesFragment

class FragmentPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MovieFragment()
            1 -> SeriesFragment()
            2 -> FavoritosFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}