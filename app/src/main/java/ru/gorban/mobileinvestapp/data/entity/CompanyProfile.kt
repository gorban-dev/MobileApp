package ru.gorban.mobileinvestapp.data.entity


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stocks_data_table")
data class CompanyProfile(
    @PrimaryKey
    @ColumnInfo(name = "stocks_ticker")
    var ticker: String = "",
    @ColumnInfo(name = "stocks_name")
    var name: String = "",
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false,
    @ColumnInfo(name = "stocks_price")
    var stocks_price: Double = 0.00,
    @ColumnInfo(name = "close_price")
    var close_price: Double = 0.00,
    @ColumnInfo(name = "web_url_logo")
    var logo: String? = "",
    @ColumnInfo(name = "phone")
    var phone: String = "",
    @ColumnInfo(name = "time_update")
    var time: Long = 0L,
    @ColumnInfo(name = "currency")
    var currency: String = "",
    @ColumnInfo(name = "date_ipo")
    var ipo: String = "",
    @ColumnInfo(name = "web_url")
    var weburl: String = "",
    @ColumnInfo(name = "finnhubIndustry")
    var finnhubIndustry: String = ""
): Parcelable


