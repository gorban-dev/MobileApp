package ru.gorban.mobileinvestapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory

@Database(entities = arrayOf(CompanyProfile::class, UserSearchHistory::class), version = 1, exportSchema = false)
abstract class StocksDatabase : RoomDatabase() {

    abstract fun companyProfileDao(): CompanyProfileDao
    abstract fun userSearchDao(): UserSearchDao

    companion object {
        @Volatile
        private var INSTANCE: StocksDatabase? = null
        fun getInstance(context: Context): StocksDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StocksDatabase::class.java,
                        "stock_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}