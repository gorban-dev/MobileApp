package ru.gorban.mobileinvestapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory

@Dao
interface UserSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userRequest: UserSearchHistory)

    @Delete
    fun delete(userRequest: UserSearchHistory)

    @Query("SELECT * FROM user_search_history")
    fun getAllRequest() : Flow<List<UserSearchHistory>>

    @Query("DELETE FROM user_search_history")
    fun deleteAllRequest()
}