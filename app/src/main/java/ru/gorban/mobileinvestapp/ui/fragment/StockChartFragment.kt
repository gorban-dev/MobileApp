package ru.gorban.mobileinvestapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.databinding.FragmentChartBinding

//TODO сделать обновение цены в режиме реального времени. Создать отрисовку графика изменения цены на основе данных из API.
/**
 * StockChartFragment является демонстрационным.
 * Use the [StocksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StockChartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
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
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        val view = binding.root
        setClickListenerSearchChipGroup()
        binding.buyButton.setOnClickListener {
            Toast.makeText(context, "Вы нажали купить", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        setUpdatePrice()
        setButtonText()

    }

    companion object{
        fun getInstance(companyProfile: CompanyProfile) = StockChartFragment().apply {
            arguments = Bundle().apply {
                putParcelable(StocksFragment.COMPANY_KEY, companyProfile)
            }
        }
    }

    //TODO Вынести строковые переменные в константы.
    @SuppressLint("SetTextI18n")
    fun setButtonText(){
        when (companyProfile.currency) {
            "USD" -> {
                binding.buyButton.text = "Buy for $${companyProfile.stocks_price}"
            }
            "CNY" -> {
                binding.buyButton.text = "Buy for ¥${companyProfile.stocks_price}"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun formatPrice(symbol: String){
        val updateCurrentPrice = String.format("%.2f", companyProfile.stocks_price)
        binding.textPrice.text = "$symbol$updateCurrentPrice"
        val diffPrice = companyProfile.stocks_price - companyProfile.close_price
        val diffPricePercent =
            String.format("%.2f", (diffPrice * 100) / companyProfile.stocks_price)
                .toDouble()
        val updateDifferencePrice = String.format("%.2f", diffPrice).toDouble()
        if (diffPrice > 0) {
            binding.textDiffPrice.text =
                "+$symbol$updateDifferencePrice ($diffPricePercent%)"
            binding.textDiffPrice.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.green
                )
            )
        } else {
            val modulPrice = Math.abs(updateDifferencePrice)
            val modulPercent = Math.abs(diffPricePercent)
            binding.textDiffPrice.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.red
                )
            )
            binding.textDiffPrice.text = "-$symbol$modulPrice ($modulPercent%)"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpdatePrice() {
        when (companyProfile.currency) {
            "USD" -> {
                formatPrice("$")
            }
            "CNY" -> {
                formatPrice("¥")
            }
        }
    }

    private fun setClickListenerSearchChipGroup() {
        binding.chartChipGroupe.forEach { child ->
            val chip = child as? Chip
            chip?.setOnClickListener {
                Toast.makeText(context, "Вы нажали ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}