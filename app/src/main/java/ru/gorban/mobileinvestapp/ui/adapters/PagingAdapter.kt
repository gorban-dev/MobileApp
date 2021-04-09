package ru.gorban.mobileinvestapp.ui.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.databinding.StocksCardItemBinding
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain
import ru.gorban.mobileinvestapp.utils.GlideApp

class PagingAdapter(
    private val onCompanyListener: OnCompanyListener,
    private val viewModel: ViewModelMain
) : PagingDataAdapter<CompanyProfile, PagingAdapter.CompanyViewHolder>(CompanyComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val binding =
            StocksCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompanyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            viewModel.initCompanyProfile(currentItem.ticker)
            holder.bind(currentItem, position, onCompanyListener)
        }
    }

    class CompanyViewHolder(private val binding: StocksCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(companyProfile: CompanyProfile, position: Int, onCompanyListener: OnCompanyListener) {
            loadImage(binding, companyProfile.logo)
            setFavoriteStatus(binding, companyProfile.isFavorite)
            binding.apply {
                root.setOnClickListener {
                    onCompanyListener.onCompanyClick(companyProfile, position)
                }
                favoriteIcon.setOnClickListener {
                    onCompanyListener.onCompanyFavoriteClick(companyProfile, position)
                }
                textStockName.text = companyProfile.name
                textTicker.text = companyProfile.ticker

            }
            setUpdatePrice(binding, companyProfile)
            setBackgroundCardView(binding, position)
        }

        @SuppressLint("SetTextI18n")
        private fun formatPrice(symbol: String, itemBinding: StocksCardItemBinding,
                                companyProfile: CompanyProfile){
            val updateCurrentPrice = String.format("%.2f", companyProfile.stocks_price)
            itemBinding.textStockPrice.text = "$symbol$updateCurrentPrice"
            val diffPrice = companyProfile.stocks_price - companyProfile.close_price
            val diffPricePercent =
                String.format("%.2f", (diffPrice * 100) / companyProfile.stocks_price)
                    .toDouble()
            val updateDifferencePrice = String.format("%.2f", diffPrice).toDouble()
            if (diffPrice > 0) {
                itemBinding.textStockChangePrice.text =
                    "+$symbol$updateDifferencePrice ($diffPricePercent%)"
                itemBinding.textStockChangePrice.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.rootStocksView.context,
                        R.color.green
                    )
                )
            } else {
                val modulPrice = Math.abs(updateDifferencePrice)
                val modulPercent = Math.abs(diffPricePercent)
                itemBinding.textStockChangePrice.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.rootStocksView.context,
                        R.color.red
                    )
                )
                itemBinding.textStockChangePrice.text = "-$symbol$modulPrice ($modulPercent%)"
            }
        }

        //TODO вынести валюты в константы
        @SuppressLint("SetTextI18n")
        private fun setUpdatePrice(
            itemBinding: StocksCardItemBinding,
            companyProfile: CompanyProfile
        ) {
            itemBinding.apply {
                loaderGroup.visibility = View.GONE
                viewGroup.visibility = View.VISIBLE
            }
            when (companyProfile.currency) {
                "USD" -> {
                    formatPrice("$",itemBinding,companyProfile)
                }
                "CNY" -> {
                    formatPrice("¥",itemBinding,companyProfile)
                }
            }
        }

        private fun setFavoriteStatus(itemBinding: StocksCardItemBinding, isFavorite: Boolean) =
            if (isFavorite) itemBinding.favoriteIcon.setImageResource(R.drawable.ic_favorite_on)
            else (itemBinding.favoriteIcon.setImageResource(R.drawable.ic_favorite_off))


        //TODO Исправить отображение фона во время поиска. Проработать менее затратный по ресурсам способ установки фона
        @SuppressLint("ResourceAsColor")
        private fun setBackgroundCardView(itemBinding: StocksCardItemBinding, id: Int) {
            if (id % 2 == 0) {
                if (itemBinding.rootStocksView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootStocksView.context,
                        R.color.white
                    )
                    || itemBinding.rootStocksView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootStocksView.context,
                        R.color.background_color_card_stock
                    )
                ) {
                    itemBinding.rootStocksView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootStocksView.context,
                            R.color.background_color_card_stock
                        )
                    )
                } else {
                    itemBinding.rootStocksView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootStocksView.context,
                            R.color.cardview_dark_background
                        )
                    )
                }
            } else {
                if (itemBinding.rootStocksView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootStocksView.context,
                        R.color.white
                    )
                    || itemBinding.rootStocksView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootStocksView.context,
                        R.color.background_color_card_stock
                    )
                ) {
                    itemBinding.rootStocksView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootStocksView.context,
                            R.color.white
                        )
                    )
                } else {
                    itemBinding.rootStocksView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootStocksView.context,
                            R.color.black
                        )
                    )
                }
            }
        }

        private fun loadImage(item: StocksCardItemBinding, urlLogo: String?) {
            GlideApp.with(item.root.context)
                .load(urlLogo)
                .error(R.drawable.no_stock_logo)
                .transform(CenterInside(), RoundedCorners(40))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        item.apply {
                            stocksLogoLoader.visibility = View.GONE
                            stocksLogo.visibility = View.VISIBLE
                        }
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        item.apply {
                            stocksLogoLoader.visibility = View.GONE
                            stocksLogo.visibility = View.VISIBLE
                        }
                        return false
                    }
                })
                .into(item.stocksLogo)
        }
    }


    object CompanyComparator : DiffUtil.ItemCallback<CompanyProfile>() {
        override fun areItemsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile) =
            oldItem.ticker == newItem.ticker

        override fun areContentsTheSame(oldItem: CompanyProfile, newItem: CompanyProfile) =
            oldItem == newItem
    }

    interface OnCompanyListener {
        fun onCompanyClick(companyProfile: CompanyProfile, position: Int)
        fun onCompanyFavoriteClick(companyProfile: CompanyProfile, position: Int)
    }

}


