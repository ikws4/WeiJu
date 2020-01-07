package io.ikws4.weiju.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslationInfoRepository(private val translationDao: TranslationDao) {

    fun getByPkgName(pkgName: String) = translationDao.getByPkgName(pkgName)

    suspend fun update(info: TranslationInfo) = withContext(Dispatchers.IO) {
        translationDao.update(info)
    }
}