package io.ikws4.weiju.utilities

import android.content.Context
import android.os.Environment
import de.robv.android.xposed.XSharedPreferences
import io.ikws4.weiju.BuildConfig
import java.io.File

/**
 * 由于 [XSharedPreferences] 是直接通过 [Environment.getDataDirectory]
 * 来获取 Data path 的，所以无法在 virtual xposed 这样的沙盒应用中运行
 * 所以使用 [Context] 来获取 Data path
 */
internal object XSharedPreferencesUtil {
    private const val PACKAGE_NAME = BuildConfig.APPLICATION_ID

    private fun getUserDataPathFromContext(context: Context): String {
        return context.filesDir.parentFile!!.parent!!
    }


    fun get(
        context: Context, packageName: String, prefFileName: String
    ): XSharedPreferences {
        val file =
            File(getUserDataPathFromContext(context), "$packageName/shared_prefs/$prefFileName.xml")

        return XSharedPreferences(file)
    }

    fun get(
        context: Context, prefFileName: String
    ): XSharedPreferences {
        return get(context, PACKAGE_NAME, prefFileName)
    }

    fun getHookList(context: Context): XSharedPreferences {
        return get(context, PACKAGE_NAME, HOOK_LIST_SP)
    }

    fun getAppConfig(context: Context): XSharedPreferences {
        return get(context, PACKAGE_NAME, PACKAGE_NAME + "_preferences")
    }
}