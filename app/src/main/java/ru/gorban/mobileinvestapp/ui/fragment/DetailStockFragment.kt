package ru.gorban.mobileinvestapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.databinding.FragmentDetailStockBinding
import ru.gorban.mobileinvestapp.ui.adapters.StockPagerAdapter
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [DetailStockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailStockFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var companyProfile: CompanyProfile
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var adapter: StockPagerAdapter
    private val viewModel: ViewModelMain by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<CompanyProfile>(StocksFragment.COMPANY_KEY)?.let {
            companyProfile = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStockBinding.inflate(inflater, container, false)
        binding.favoriteButton.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayoutDetail
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        adapter = StockPagerAdapter(this, companyProfile)
        viewPager = binding.viewPagerStocks
        viewPager.adapter = adapter
        setTabTextView()
        setTabLayoutListener()
        setCompanyInfo()
    }

    private fun setCompanyInfo() {
        binding.apply {
            tvTicker.text = companyProfile.ticker
            tvCompanyName.text = companyProfile.name
            if (companyProfile.isFavorite) favoriteButton.setImageResource(R.drawable.ic_favorite_on)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailStockFragment()
    }


    private fun setTabTextView() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val stocksText = layoutInflater.inflate(R.layout.tab_item_text_active, null) as TextView
            val favoriteText = layoutInflater.inflate(R.layout.tab_item_text_inactive, null) as TextView
            when (position) {
                0 -> {
                    stocksText.text = resources.getString(R.string.table_layout_chart)
                    stocksText.textSize = 28F
                    tab.customView = stocksText
                }
                1 -> {
                    favoriteText.text = resources.getString(R.string.table_layout_summary)
                    favoriteText.textSize = 18F
                    tab.customView = favoriteText
                }
                2 -> {
                    favoriteText.text = resources.getString(R.string.table_layout_news)
                    favoriteText.textSize = 18F
                    tab.customView = favoriteText
                }
                3 -> {
                    favoriteText.text = resources.getString(R.string.table_layout_forecast)
                    favoriteText.textSize = 18F
                    tab.customView = favoriteText
                }
                4 -> {
                    favoriteText.text = resources.getString(R.string.table_layout_idea)
                    favoriteText.textSize = 18F
                    tab.customView = favoriteText
                }
            }
        }.attach()
    }

    private fun setTabLayoutListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab?.customView as TextView
                customView.apply {
                    textSize = 28F
                    if (isNightModeActive()) setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.tab_text_night_color_on
                        )
                    )
                    else setTextColor(ContextCompat.getColor(context, R.color.tab_text_color_on))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView as TextView
                customView.apply {
                    textSize = 18F
                    if (isNightModeActive()) setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.tab_text_night_color_off
                        )
                    )
                    else setTextColor(ContextCompat.getColor(context, R.color.tab_text_color_off))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView as TextView
                customView.apply {
                    textSize = 28F
                    if (isNightModeActive()) setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.tab_text_night_color_on
                        )
                    )
                    else setTextColor(ContextCompat.getColor(context, R.color.tab_text_color_on))
                }
            }
        })
    }

    private fun setFavoriteStatus() {
        companyProfile.isFavorite = !companyProfile.isFavorite
        if (companyProfile.isFavorite) {
            Toast.makeText(
                context,
                "${companyProfile.ticker} ${resources.getString(R.string.add_favorite)}",
                Toast.LENGTH_SHORT
            ).show()
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_on)
        } else {
            Toast.makeText(
                context,
                "${companyProfile.ticker} ${resources.getString(R.string.delete_favorite)}",
                Toast.LENGTH_SHORT
            ).show()
            if (isNightModeActive()) binding.favoriteButton.setImageResource(R.drawable.ic_favorite_off_detail_night)
            else binding.favoriteButton.setImageResource(R.drawable.ic_favorite_off_detail)
        }
        viewModel.updateFavoriteStatus(companyProfile)
    }

    private fun isNightModeActive() = (activity as MainActivity).isNightModeActive()

    override fun onClick(view: View?) {
        view?.let {
            binding.apply {
                when (view) {
                    backButton -> findNavController().popBackStack()
                    favoriteButton -> setFavoriteStatus()
                }
            }
        }
    }
}