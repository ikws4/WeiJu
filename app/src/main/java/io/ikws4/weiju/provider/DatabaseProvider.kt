package io.ikws4.weiju.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.data.TranslationInfo


class DatabaseProvider : ContentProvider() {
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    companion object {
        const val authority = "io.ikws4.weiju.provider.DatabaseProvider"

        const val TRANSLATION_INFO_URI = "content://$authority/translation_info"
        const val TRANSLATION_INFO_QUERY_RESULT = 0 //通过query获取result
        const val TRANSLATION_INFO_INSERT = 1 //插入一条翻译数据

        const val USER_URI = "content://$authority/user"
        const val USER_QUERY_FREE_SOUGOU_API_AMOUNT = 2 // 获取免费搜狗API字符数量
        const val USER_UPDATE_FREE_SOUGOU_API_AMOUNT = 3 // 设置免费搜狗API字符数量
    }


    private fun initializeUriMatching() {
        uriMatcher.addURI(authority, "translation_info/insert/", TRANSLATION_INFO_INSERT)
        // 举个例子: content://io.ikws4.weiju.provider.DatabaseProvider/translation_info/query/result/
        // 那么应该返回result（你好）
        uriMatcher.addURI(authority, "translation_info/query/result/*", TRANSLATION_INFO_QUERY_RESULT)
        uriMatcher.addURI(authority, "user/query/freeSogouApiAmount", USER_QUERY_FREE_SOUGOU_API_AMOUNT)
        // * 为更新值
        uriMatcher.addURI(authority, "user/update/freeSogouApiAmount", USER_UPDATE_FREE_SOUGOU_API_AMOUNT)
    }


    override fun onCreate(): Boolean {
        initializeUriMatching()
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            TRANSLATION_INFO_INSERT -> {
                val context = context ?: return null
                val translationDao = AppDatabase.getInstance(context).translationInfoDao()
                val id = translationDao.insert(
                    TranslationInfo.fromContentValues(
                        values ?: throw IllegalAccessException("Values is not null")
                    )
                )
                return ContentUris.withAppendedId(uri, id)
            }
            else -> throw IllegalAccessException("Unknown URI: $uri")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>, sortOrder: String?): Cursor? {
        when (uriMatcher.match(uri)) {
            TRANSLATION_INFO_QUERY_RESULT -> {
                val query = uri.lastPathSegment ?: ""
                val context = context ?: return null
                val translationDao = AppDatabase.getInstance(context).translationInfoDao()
                val cursor = translationDao.get(
                    query = query,
                    pkgName = selectionArgs[0],
                    from = selectionArgs[1],
                    to = selectionArgs[2]
                )
                cursor.setNotificationUri(context.contentResolver, uri)
                return cursor
            }
            USER_QUERY_FREE_SOUGOU_API_AMOUNT -> {
                val userDao = AppDatabase.getInstance(context!!).userDao()
                return userDao.getFreeSogouApiAmount()
            }
            else -> throw IllegalAccessException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return when (uriMatcher.match(uri)) {
            USER_UPDATE_FREE_SOUGOU_API_AMOUNT -> {
                val userDao = AppDatabase.getInstance(context!!).userDao()
                val amount = values?.getAsInteger("freeSogouApiAmount") ?: throw IllegalAccessException("freeSogouApiAmount can't be null")
                val user = userDao.getUser()
                if (user.freeSogouApiAmount >= 0) {
                    userDao.decreaseFreeSogouApiAmount(amount)
                } else {
                    userDao.setFreeSogouApiAmount(0)
                }
                0 //表示更新成功
            }
            else -> throw IllegalAccessException("Unknown URI: $uri") //表示更新失败
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}