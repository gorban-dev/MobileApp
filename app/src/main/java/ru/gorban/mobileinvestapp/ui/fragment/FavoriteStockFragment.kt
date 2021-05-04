package ru.gorban.mobileinvestapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.filter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory
import ru.gorban.mobileinvestapp.databinding.FragmentFavoriteStockBinding
import ru.gorban.mobileinvestapp.ui.adapters.PagingAdapter
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteStockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteStockFragment : Fragment(), PagingAdapter.OnCompanyListener {

    private var _binding: FragmentFavoriteStockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewModelMain by activityViewModels()
    private lateinit var pagingAdapter: PagingAdapter
    private lateinit var fragment: CollectionStockFragment
    private lateinit var job: Job

    companion object {
        fun newInstance() = FavoriteStockFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteStockBinding.inflate(inflater, container, false)
        val view = binding.root
        fragment = parentFragment as CollectionStockFragment
        return view
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagingAdapter = PagingAdapter(this, viewModel)
        binding.rcFavoriteCompany.rcStocksList.apply {
            adapter = pagingAdapter
        }
    }

    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    override fun onResume() {
        super.onResume()
        setCompanyList("")
        searchStocks()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    private fun searchStocks() {
        fragment.binding.searchView.isEndIconVisible = false
        fragment.binding.searchView.setEndIconOnClickListener {
            closeButtonListener()
        }

        fragment.binding.searchView.setStartIconOnClickListener {
            backButtonListener()
        }

        fragment.binding.editText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                fragment.binding.searchView.isEndIconVisible = true
                job.cancel()
                closeButtonListener()
            } else {
                backButtonListener()
            }
        }

        fragment.binding.editText.setOnKeyListener { _, keyCode, keyEvent ->
            if(keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER){
                val text = fragment.binding.searchView.editText?.text.toString()
                if (text.isNotEmpty() && text.length > 2){
                    val formatRequest = text.lowercase().capitalize(Locale.ROOT)
                    viewModel.addUserRequest(UserSearchHistory(formatRequest))
                }
                job.cancel()
                setCompanyList(text)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        fragment.binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    if (p0.isEmpty()) {
                        job.cancel()
                        val sum = p1 + p2 + p3
                        if (sum == 0){
                            backButtonListener()
                            setCompanyList(it.toString())
                        }else closeButtonListener()
                    } else {
                        job.cancel()
                        setCompanyList(it.toString())
                        fragment.binding.apply {
                            rootSearch.visibility = View.GONE
                            viewPagerStocks.visibility = View.VISIBLE
                            tabLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    @ExperimentalStdlibApi
    @ExperimentalPagingApi
    fun setCompanyList(searchText: String?) {
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFavoriteCompany().collectLatest { company ->
                pagingAdapter.submitData(filterListCompany(company, searchText))
            }
        }
    }

    @ExperimentalStdlibApi
    private fun filterListCompany(
        pagingCompany: PagingData<CompanyProfile>,
        searchText: String?
    ): PagingData<CompanyProfile> {
        return if (searchText.isNullOrEmpty()) {
            pagingCompany
        } else {
            val filterCompany = pagingCompany.filter {
                it.ticker.lowercase().contains(searchText.lowercase()) or it.name.lowercase()
                    .contains(searchText.lowercase())
            }
            filterCompany
        }
    }
    override fun onCompanyClick(companyProfile: CompanyProfile, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(StocksFragment.COMPANY_KEY, companyProfile)
        findNavController().navigate(
            R.id.action_collectionStockFragment_to_detailStockFragment2,
            bundle
        )
    }

    override fun onCompanyFavoriteClick(companyProfile: CompanyProfile, position: Int) {
        companyProfile.isFavorite = !companyProfile.isFavorite
        if (companyProfile.isFavorite) {
            Toast.makeText(
                context,
                "${companyProfile.ticker} ${resources.getString(R.string.add_favorite)}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "${companyProfile.ticker} ${resources.getString(R.string.delete_favorite)}",
                Toast.LENGTH_SHORT
            ).show()
        }
        viewModel.updateFavoriteStatus(companyProfile)
        pagingAdapter.notifyItemChanged(position, companyProfile)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun isNightModeActive() = (activity as MainActivity).isNightModeActive()

    private fun closeButtonListener() {
        fragment.binding.apply {
            editText.hint = ""
            if(isNightModeActive()) searchView.setStartIconDrawable(R.drawable.ic_button_back_night) else searchView.setStartIconDrawable(R.drawable.ic_button_back)
            rootSearch.visibility = View.VISIBLE
            viewPagerStocks.visibility = View.GONE
            tabLayout.visibility = View.GONE
            searchView.editText?.text?.clear()
        }
    }
    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    private fun backButtonListener() {
        hideKeyboard()
        job.cancel()
        setCompanyList("")
        fragment.binding.apply {
            if(isNightModeActive()) searchView.setStartIconDrawable(R.drawable.ic_button_search_night) else searchView.setStartIconDrawable(R.drawable.ic_button_search)
            editText.hint = getString(R.string.search_hint_text)
            searchView.isEndIconVisible = false
            searchView.editText?.text?.clear()
            searchView.clearFocus()
            rootSearch.visibility = View.GONE
            viewPagerStocks.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
        }
    }
}