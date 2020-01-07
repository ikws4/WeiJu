package io.ikws4.weiju.utilities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import io.ikws4.weiju.provider.SharedPreferencesProvider

/**
 * 对SharePreferencesProvider的一个封装
 *
 * 用法:
 * val spHelper = XSPUtils(context,"Your sharePreference file name")
 * val value = spHelper.getString("there is a key")
 *
 * @property context Context
 * @property prefName String SharePreferences件名称
 * @property uri (android.net.Uri..android.net.Uri?)
 * @constructor
 */
class XSPUtils(private val context: Context, private val prefName: String) {

    private val uri = Uri.parse("content://io.ikws4.weiju.provider.SharedPreferencesProvider")

    /**
     * 更新SP文件中的某个String
     * @param key String
     * @param value String
     */
    fun putString(key: String, value: String) {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)
        data.putString(SharedPreferencesProvider.EXTRA_VALUE, value)

        context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_PUT_STRING,
            prefName,
            data
        )
    }

    /**
     * 传入你的key，然后获取相应的value
     * @param key String
     * @return String
     */
    fun getString(key: String): String {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)

        val bundle = context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_GET_STRING,
            prefName,
            data
        )!!

        return bundle.getString(SharedPreferencesProvider.EXTRA_VALUE, "")
    }

    fun putInt(key: String, value: Int) {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)
        data.putInt(SharedPreferencesProvider.EXTRA_VALUE, value)

        context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_PUT_INT,
            prefName,
            data
        )
    }

    fun getInt(key: String): Int {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)

        val bundle = context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_GET_INT,
            prefName,
            data
        )!!

        return bundle.getInt(SharedPreferencesProvider.EXTRA_VALUE, 0)
    }

    fun putBoolean(key: String, value: Boolean) {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)
        data.putBoolean(SharedPreferencesProvider.EXTRA_VALUE, value)

        context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_PUT_BOOLEAN,
            prefName,
            data
        )
    }

    fun getBoolean(key: String): Boolean {
        val data = Bundle()
        data.putString(SharedPreferencesProvider.EXTRA_KEY, key)

        val bundle = context.contentResolver.call(
            uri,
            SharedPreferencesProvider.METHOD_GET_BOOLEAN,
            prefName,
            data
        )!!

        return bundle.getBoolean(SharedPreferencesProvider.EXTRA_VALUE, false)
    }

    fun getContext() = context
}