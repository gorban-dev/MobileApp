package ru.gorban.mobileinvestapp.data.repository

import kotlinx.coroutines.flow.Flow
import ru.gorban.mobileinvestapp.data.db.UserSearchDao
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory

class UserSearchRepository(private val userSearchDao: UserSearchDao) {

    fun insert(userRequest: UserSearchHistory) = userSearchDao.insert(userRequest)

    fun delete(userRequest: UserSearchHistory) =
        userSearchDao.delete(userRequest)

    fun getAllRequest() : Flow<List<UserSearchHistory>> = userSearchDao.getAllRequest()

    fun deleteAllRequest() = userSearchDao.deleteAllRequest()
}