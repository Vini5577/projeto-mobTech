package com.mobtech.mobmovies

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobtech.mobmovies.fragments.SearchMovieFragment
import com.mobtech.mobmovies.fragments.FavoritosFragment
import com.mobtech.mobmovies.fragments.SearchSeriesFragment

class FragmentSearchAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchMovieFragment()
            1 -> SearchSeriesFragment()
            2 -> FavoritosFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}