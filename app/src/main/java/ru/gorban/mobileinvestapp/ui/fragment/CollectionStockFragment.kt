package ru.gorban.mobileinvestapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.databinding.FragmentRootStocksBinding
import ru.gorban.mobileinvestapp.ui.adapters.ViewPagerAdapter
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain

class CollectionStockFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRootStocksBinding? = null
    val binding get() = _binding!!
    private val viewModel: ViewModelMain by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootStocksBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayout
        adapter = ViewPagerAdapter(this)
        viewPager = binding.viewPagerStocks
        viewPager.id = binding.viewPagerStocks.id
        viewPager.adapter = adapter
        binding.clearSearchHistory.setOnClickListener(this)
        setTabTextView()
        setTabLayoutListener()
        createUserSearchGroup()
        setClickListenerPopularChipGroup()
    }

    private fun setTabTextView(){
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val stocksText = layoutInflater.inflate(R.layout.tab_item_text_active, null) as TextView
            val favoriteText = layoutInflater.inflate(R.layout.tab_item_text_inactive, null) as TextView
            if (position == 1){
                favoriteText.text = resources.getString(R.string.table_layout_favorite)
                favoriteText.textSize = 18F
                tab.customView = favoriteText
            } else{
                stocksText.text = resources.getString(R.string.table_layout_stocks)
                stocksText.textSize = 28F
                tab.customView = stocksText
            }
        }.attach()
    }


    private fun setTabLayoutListener(){
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab?.customView as TextView
                customView.apply {
                    textSize = 28F
                    if (isNightModeActive()) setTextColor(ContextCompat.getColor(context, R.color.tab_text_night_color_on))
                    else setTextColor(ContextCompat.getColor(context, R.color.tab_text_color_on))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView as TextView
                customView.apply {
                    textSize = 18F
                    if (isNightModeActive()) setTextColor(ContextCompat.getColor(context, R.color.tab_text_night_color_off))
                    else setTextColor(ContextCompat.getColor(context, R.color.tab_text_color_off))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isNightModeActive() = (activity as MainActivity).isNightModeActive()

    companion object {
        fun newInstance() = CollectionStockFragment()
    }

    @SuppressLint("ResourceType")
    private fun createUserSearchGroup(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserSearchRequest().collectLatest { requestList ->
                clearChipGroup(binding.searchedGroup1,binding.searchedGroup2)
                val revList = requestList.reversed()
                if (revList.size > 16) viewModel.deleteOldUserRequest(revList[requestList.size-1])
                for ((i, value) in revList.withIndex()){
                    if (i < 8){
                        val chip = Chip(context)
                        val chipDrawable = ChipDrawable.createFromAttributes(requireContext(),null,0, R.style.Chip)
                        chip.setChipDrawable(chipDrawable)
                        chip.text = value.request
                      binding.searchedGroup1.addView(chip)
                    }else{
                        val chip = Chip(context)
                        val chipDrawable = ChipDrawable.createFromAttributes(requireContext(),null,0, R.style.Chip)
                        chip.setChipDrawable(chipDrawable)
                        chip.text = value.request
                       binding.searchedGroup2.addView(chip)
                    }
                }
                setClickListenerSearchChipGroup()
            }
        }
    }

    private fun setClickListenerSearchChipGroup(){
       binding.searchedGroup1.forEach { child ->
            val chip = child as? Chip
            chip?.setOnClickListener {
                binding.editText.apply {
                    val text = chip.text
                    setText(text)
                    setSelection(text.length)
                }
            }
        }

        binding.searchedGroup2.forEach { child ->
            val chip = child as? Chip
            chip?.setOnClickListener {
                binding.editText.apply {
                    val text = chip.text
                    setText(text)
                    setSelection(text.length)
                }
            }
        }
    }

    private fun setClickListenerPopularChipGroup(){
        binding.popularGroup1.forEach { child ->
            val chip = child as? Chip
            chip?.setOnClickListener {
                binding.editText.apply {
                    val text = chip.text
                    setText(text)
                    setSelection(text.length)
                }
            }
        }
        binding.popularGroup2.forEach { child ->
            val chip = child as? Chip
            chip?.setOnClickListener {
                binding.editText.apply {
                    val text = chip.text
                    setText(text)
                    setSelection(text.length)
                }
            }
        }
    }

    private fun clearChipGroup(vararg chipGroup: ChipGroup){
        chipGroup.forEach {
            it.removeAllViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        viewModel.deleteSearchHistory()
        clearChipGroup(binding.searchedGroup1, binding.searchedGroup2)
    }
}