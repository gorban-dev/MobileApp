package ru.gorban.mobileinvestapp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.filter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory
import ru.gorban.mobileinvestapp.databinding.FragmentStocksBinding
import ru.gorban.mobileinvestapp.ui.adapters.PagingAdapter
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [StocksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StocksFragment : Fragment(), PagingAdapter.OnCompanyListener {

    private val viewModel: ViewModelMain by activityViewModels()
    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagingAdapter: PagingAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var fragment: CollectionStockFragment
    private lateinit var job: Job

    companion object {
        fun newInstance() = StocksFragment()
        const val COMPANY_KEY = "company"
    }

    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        val view = binding.root
        pagingAdapter = PagingAdapter(this, viewModel)
        binding.rcContainer.rcStocksList.apply {
            adapter = pagingAdapter
        }
        fragment = parentFragment as CollectionStockFragment
        showLoadProgress()
        return view
    }

    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCompanyList("")
        searchStocks()
    }

    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    override fun onResume() {
        super.onResume()
        showSnackbar(checkInternetConnection())
    }

    private fun showLoadProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collectLatest {
                binding.loadingStocks.visibility = View.VISIBLE
                if (it.refresh.endOfPaginationReached) binding.loadingStocks.visibility = View.GONE
            }
        }
    }

    @ExperimentalPagingApi
    @ExperimentalStdlibApi
    private fun showSnackbar(isOnline: Boolean) {
        snackbar = Snackbar.make(
            requireView(),
            resources.getString(R.string.no_internet_connection),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(resources.getString(R.string.snackbar_text_action)) {
            if (checkInternetConnection()) {
                connectSocket()
                setCompanyList("")
                binding.loadingStocks.visibility = View.GONE
            }
            showSnackbar(checkInternetConnection())
        }
        if (isOnline) {
            snackbar.dismiss()
        } else {
            binding.loadingStocks.visibility = View.VISIBLE
            snackbar.show()
        }
    }

    private fun isNightModeActive() = (activity as MainActivity).isNightModeActive()

    private fun checkInternetConnection() = (activity as MainActivity).isOnline()

    override fun onPause() {
        super.onPause()
        if (this::snackbar.isInitialized) snackbar.dismiss()
    }

    private fun connectSocket() {
        Log.i("MySocket", "startSocketConnection from StocksFragment")
        viewModel.subscribeToSocketEvents()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    //TODO протестировать на отработку всех слушателей
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

        fragment.binding.editText.addTextChangedListener(object : TextWatcher{
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
            viewModel.getCompanyList().collectLatest { company ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCompanyClick(companyProfile: CompanyProfile, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable(COMPANY_KEY, companyProfile)
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