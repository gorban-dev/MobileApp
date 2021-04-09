package ru.gorban.mobileinvestapp.ui.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.gorban.mobileinvestapp.R
import ru.gorban.mobileinvestapp.data.entity.News
import ru.gorban.mobileinvestapp.databinding.NewsCardItemBinding
import ru.gorban.mobileinvestapp.databinding.StocksCardItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NewsListAdapter(
    private val onNewsListener: OnNewsListener
) :
    RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    private var _itemList = emptyList<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemBinding =
            NewsCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(itemBinding)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = _itemList[position]
        holder.bind(currentItem, position, onNewsListener)
    }

    override fun getItemCount(): Int = _itemList.size

    fun setNewsData(stocksList: List<News>) {
           _itemList = stocksList
           notifyDataSetChanged()
    }

    class NewsViewHolder(private val itemBinding: NewsCardItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: News, position: Int, onNewsListener: OnNewsListener) {
            itemBinding.root.setOnClickListener { onNewsListener.onNewsClick(model) }
            itemBinding.apply {
                textHeadline.text = model.headline
                textSourceNews.text =  model.source
                val dateFromModel = model.datetime * 1000L
                textDatePublic.text =  Date(dateFromModel).toString("dd-MM-yyyy")
            }
            loadImage(itemBinding, model.image)
            setBackgroundCardView(itemBinding, position)
        }

        @SuppressLint("ResourceAsColor")
        private fun setBackgroundCardView(itemBinding: NewsCardItemBinding, id: Int) {
            if (id % 2 == 0) {
                if (itemBinding.rootNewsView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootNewsView.context,
                        R.color.white
                    )
                    || itemBinding.rootNewsView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootNewsView.context,
                        R.color.background_color_card_stock
                    )
                ) {
                    itemBinding.rootNewsView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootNewsView.context,
                            R.color.background_color_card_stock
                        )
                    )
                } else {
                    itemBinding.rootNewsView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootNewsView.context,
                            R.color.cardview_dark_background
                        )
                    )
                }
            } else {
                if (itemBinding.rootNewsView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootNewsView.context,
                        R.color.white
                    )
                    || itemBinding.rootNewsView.cardBackgroundColor
                    == ContextCompat.getColorStateList(
                        itemBinding.rootNewsView.context,
                        R.color.background_color_card_stock
                    )
                ) {
                    itemBinding.rootNewsView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootNewsView.context,
                            R.color.white
                        )
                    )
                } else {
                    itemBinding.rootNewsView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.rootNewsView.context,
                            R.color.black
                        )
                    )
                }
            }
        }

        private fun loadImage(item: NewsCardItemBinding, urlLogo: String?) {
                Glide.with(item.root.context)
                        .load(urlLogo)
                        .error(R.drawable.no_stock_logo)
                        .transform(CenterCrop(), RoundedCorners(15))
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                item.imageNewsLoader.visibility = View.GONE
                                item.imageNews.visibility = View.VISIBLE
                                return false
                            }

                            override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                item.imageNewsLoader.visibility = View.GONE
                                item.imageNews.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(item.imageNews)
        }

        private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }
    }

    interface OnNewsListener {
        fun onNewsClick(news: News)
    }

}
