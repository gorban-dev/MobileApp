package ru.gorban.mobileinvestapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.News
import ru.gorban.mobileinvestapp.databinding.FragmentNewsBinding
import ru.gorban.mobileinvestapp.ui.adapters.NewsListAdapter
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain
import java.text.SimpleDateFormat
import java.util.*

class StockNewsFragment : Fragment(),
    NewsListAdapter.OnNewsListener {

    private var _binding: FragmentNewsBinding? = null
    val binding get() = _binding!!
    private val viewModel: ViewModelMain by activityViewModels()
    private lateinit var newsAdapter: NewsListAdapter
    private lateinit var snackbar: Snackbar
    private val WEEK_OF_MILLIS = 604800000L
    private val DATE_FORMAT = "yyyy-MM-dd"
    private lateinit var companyProfile: CompanyProfile

    companion object {
        fun getInstance(companyProfile: CompanyProfile) = StockNewsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(StocksFragment.COMPANY_KEY, companyProfile)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<CompanyProfile>(StocksFragment.COMPANY_KEY)?.let {
            companyProfile = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root
        getNewsOfWeek()
        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webNews.apply {
            webViewClient = MyWebViewClient(binding)
            settings.javaScriptEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        setNewsList()
        showSnackbar(checkInternetConnection())
    }


    //TODO Сделать обработку нажатия "Назад" для корректного возврата к списку новостей
    override fun onPause() {
        super.onPause()
        binding.apply {
            rcContainerNews.rcStocksList.visibility = View.VISIBLE
            textNoNews.visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            webNews.loadUrl("about:blank")
            webNews.visibility = View.GONE
        }
        if(this::snackbar.isInitialized) snackbar.dismiss()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getNewsOfWeek() {
        val currentTime = getCurrentDateTime()
        val lastWeek = getCurrentDateTime() - WEEK_OF_MILLIS
        viewModel.getNews(
            companyProfile.ticker,
            Date(lastWeek).toString(DATE_FORMAT),
            Date(currentTime).toString(DATE_FORMAT)
        )
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Long {
        return Calendar.getInstance().timeInMillis
    }

    private fun setNewsList() {
        newsAdapter = NewsListAdapter(this)
        binding.rcContainerNews.rcStocksList.apply {
            adapter = newsAdapter
        }
        viewModel.companyNews.observe(viewLifecycleOwner, Observer {
            binding.loadingNews.visibility = View.VISIBLE
            binding.rcContainerNews.rcStocksList.visibility = View.GONE
          if(it.isNotEmpty()) {
              newsAdapter.setNewsData(it)
              binding.rcContainerNews.rcStocksList.visibility = View.VISIBLE
              binding.loadingNews.visibility = View.GONE
        }else{
              binding.textNoNews.visibility = View.VISIBLE
              binding.rcContainerNews.rcStocksList.visibility = View.GONE
              binding.loadingNews.visibility = View.GONE
          }
        })
    }

    private fun showSnackbar(isOnline: Boolean){
        snackbar = Snackbar.make(requireView(), resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(resources.getString(R.string.snackbar_text_action)) {
            if (checkInternetConnection()){
                getNewsOfWeek()
            }
            showSnackbar(checkInternetConnection())
        }
        if (isOnline) snackbar.dismiss() else snackbar.show()
    }

    private fun checkInternetConnection() = (activity as MainActivity).isOnline()


    override fun onNewsClick(news: News) {
        binding.apply {
            rcContainerNews.rcStocksList.visibility = View.GONE
            textNoNews.visibility = View.GONE
            text.visibility = View.GONE
            webNews.visibility = View.VISIBLE
        }
        binding.webNews.apply {
            loadUrl(news.url)
            binding.loadingNews.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.companyNews.value = emptyList()
    }

     class MyWebViewClient(private val binding: FragmentNewsBinding) : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (view != null && request != null) {
                view.loadUrl(request.url.toString())
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            view?.clearHistory()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.loadingNews.visibility = View.GONE
            view?.clearHistory()
        }
    }
}