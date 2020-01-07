package io.ikws4.weiju.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository private constructor(private val userDao: UserDao) {

    suspend fun increaseFreeSogouApiAmount(amount: Int) = withContext(Dispatchers.IO) {
        userDao.increaseFreeSogouApiAmount(amount)
    }

    fun getUser() = userDao.getUserFormLiveData()

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDao: UserDao): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userDao).also { instance = it }
            }
        }
    }
}