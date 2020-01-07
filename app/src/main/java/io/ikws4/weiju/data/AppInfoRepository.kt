package io.ikws4.weiju.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用于对数据库的读写
 * @property appInfoDao AppInfoDao
 * @constructor
 */
class AppInfoRepository private constructor(private val appInfoDao: AppInfoDao) {

    suspend fun insert(appInfo: AppInfo) = withContext(Dispatchers.IO) {
        appInfoDao.insert(appInfo)
    }

    suspend fun update(appInfo: AppInfo) = withContext(Dispatchers.IO) {
        appInfoDao.update(appInfo)
    }

    fun getAll() = appInfoDao.getAll()

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppInfoRepository? = null

        fun getInstance(appInfoDao: AppInfoDao): AppInfoRepository {
            return instance ?: synchronized(this) {
                instance ?: AppInfoRepository(appInfoDao).also {
                    instance = it
                }
            }
        }
    }
}