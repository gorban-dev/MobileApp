package ru.gorban.mobileinvestapp.ui.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.databinding.FragmentSummaryBinding
import ru.gorban.mobileinvestapp.utils.GlideApp

class StockSummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var companyProfile: CompanyProfile

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
    ): View? {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCompanyData()
    }


    private fun setCompanyData() {
        loadImage()
        setTextSummary()

    }

    companion object {
        fun getInstance(companyProfile: CompanyProfile) = StockSummaryFragment().apply {
            arguments = Bundle().apply {
                putParcelable(StocksFragment.COMPANY_KEY, companyProfile)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextSummary() {
        binding.apply {
            textTicker.text = "${textTicker.text} ${companyProfile.ticker}"
            textName.text = "${textName.text} ${companyProfile.name}"
            textCurrency.text = "${textCurrency.text} ${companyProfile.currency}"
            textIndustry.text =
                if (companyProfile.finnhubIndustry.isNotEmpty()) "${textIndustry.text} ${companyProfile.finnhubIndustry}" else "${textIndustry.text} ${resources.getString(R.string.data_not_found)}"
            textPhone.text =
                if (companyProfile.phone.isNotEmpty()) "${textPhone.text} ${companyProfile.phone}" else "${textPhone.text} ${resources.getString(R.string.data_not_found)}"
            textIpo.text =
                if (companyProfile.ipo.isNotEmpty()) "${textIpo.text} ${companyProfile.ipo}" else "${textIpo.text} ${resources.getString(R.string.data_not_found)}"
            textWeburl.text =
                if (companyProfile.weburl.isNotEmpty()) "${textWeburl.text} ${companyProfile.weburl}" else "${textWeburl.text} ${resources.getString(R.string.data_not_found)}"
        }
    }

    private fun loadImage() {
        GlideApp.with(this)
            .load(companyProfile.logo)
            .error(R.drawable.no_stock_logo)
            .transform(CenterInside(), RoundedCorners(40))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.logoLoader.visibility = View.GONE
                    binding.companyLogo.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.logoLoader.visibility = View.GONE
                    binding.companyLogo.visibility = View.VISIBLE
                    return false
                }
            })
            .into(binding.companyLogo)
    }
}