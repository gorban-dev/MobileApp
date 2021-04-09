package ru.gorban.mobileinvestapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.ui.fragment.*

class StockPagerAdapter(fa : Fragment, private val companyProfile: CompanyProfile) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StockChartFragment.getInstance(companyProfile)
            1 ->  StockSummaryFragment.getInstance(companyProfile)
            2 ->  StockNewsFragment.getInstance(companyProfile)
            3 ->  ForecastFragment.getInstance()
            else -> IdeaFragment.getInstance()
        }
    }
}