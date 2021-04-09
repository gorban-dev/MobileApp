package ru.gorban.mobileinvestapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.gorban.mobileinvestapp.ui.fragment.FavoriteStockFragment
import ru.gorban.mobileinvestapp.ui.fragment.StocksFragment

class ViewPagerAdapter(fa: Fragment): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val stocksFragment = StocksFragment.newInstance()
        val favoriteStockFragment = FavoriteStockFragment.newInstance()
        return if (position == 1) favoriteStockFragment else stocksFragment
    }
}


