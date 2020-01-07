package io.ikws4.weiju.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.content.edit

class SharedPreferencesProvider : ContentProvider() {

    /*******************************这些方法不用管******************************/

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun onCreate(): Boolean = false

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

    /*******************************这些方法不用管******************************/

    companion object {
        const val EXTRA_KEY = "key"
        const val EXTRA_VALUE = "value"

        const val METHOD_PUT_STRING = "put_string"
        const val METHOD_GET_STRING = "get_string"

        const val METHOD_PUT_INT = "put_int"
        const val METHOD_GET_INT = "get_int"

        const val METHOD_PUT_BOOLEAN = "put_boolean"
        const val METHOD_GET_BOOLEAN = "get_boolean"

        // 如果还需要别的类型，在继续加
    }

    /**
     * 我们只需要这个call方法
     * @param method String 需要调用的方法
     * @param arg String? SharedPreferences的文件名称
     * @param extras Bundle? 通过Bundle传输数据
     * @return Bundle?
     */
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {

        val bundle = Bundle()
        val sharedPreferences = context!!.getSharedPreferences(arg, Context.MODE_PRIVATE)

        val key = extras!!.getString(EXTRA_KEY)

        when (method) {
            METHOD_PUT_STRING -> {
                sharedPreferences.edit {
                    val value = extras.getString(EXTRA_VALUE)
                    putString(key, value)
                }
            }
            METHOD_GET_STRING -> {
                bundle.putString(EXTRA_VALUE, sharedPreferences.getString(key, ""))
            }
            METHOD_PUT_INT -> {
                sharedPreferences.edit {
                    val value = extras.getInt(EXTRA_VALUE)
                    putInt(key, value)
                }
            }
            METHOD_GET_INT -> {
                bundle.putInt(EXTRA_VALUE, sharedPreferences.getInt(key, 0))
            }
            METHOD_PUT_BOOLEAN -> {
                sharedPreferences.edit {
                    val value = extras.getBoolean(EXTRA_VALUE)
                    putBoolean(key, value)
                }
            }
            METHOD_GET_BOOLEAN -> {
                bundle.putBoolean(EXTRA_VALUE, sharedPreferences.getBoolean(key, false))
            }
        }

        return if (bundle.isEmpty) {
            super.call(method, arg, extras)
        } else {
            bundle
        }
    }
}
