package io.ikws4.weiju.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.ikws4.weiju.BuildConfig

class SPManager private constructor(context: Context) {
    private val weiJuSP = context.getSharedPreferences(WEIJU_SP, Context.MODE_PRIVATE)
    private val hookListSP = context.getSharedPreferences(HOOK_LIST_SP, Context.MODE_PRIVATE)
    val templateSP: SharedPreferences = context.getSharedPreferences(TEMPLATE_SP, Context.MODE_PRIVATE)

    inner class WeiJuSP {
        val isAutoApplyTemplate = weiJuSP.getBoolean("is_auto_apply_template", false)
        val isBootCompleted = weiJuSP.getBoolean("is_boot_completed", true)
        val isHideSystemApp = weiJuSP.getBoolean("is_hide_system_app", true)
        var apiSogouAppid: String
            get() = weiJuSP.getString("api_sogou_appid", "")!!
            set(value) = weiJuSP.edit { putString("api_sogou_appid", value) }

        var apiSogouKey: String
            get() = weiJuSP.getString("api_sogou_key", "")!!
            set(value) = weiJuSP.edit { putString("api_sogou_key", value) }

        var apiBaiduAppid: String
            get() = weiJuSP.getString("api_baidu_appid", "")!!
            set(value) = weiJuSP.edit { putString("api_baidu_appid", value) }

        var apiBaiduKey: String
            get() = weiJuSP.getString("api_baidu_key", "")!!
            set(value) = weiJuSP.edit { putString("api_baidu_key", value) }

        var apiYoudaoAppid: String
            get() = weiJuSP.getString("api_youdao_appid", "")!!
            set(value) = weiJuSP.edit { putString("api_youdao_appid", value) }

        var apiYoudaoKey: String
            get() = weiJuSP.getString("api_youdao_key", "")!!
            set(value) = weiJuSP.edit { putString("api_youdao_key", value) }

        var freeSogouApiAmount: Int
            get() = weiJuSP.getInt("free_sogou_api_amount", 0)
            set(value) = weiJuSP.edit { putInt("free_sogou_api_amount", value) }

        var isCategoryTranslationFragmentRemindShow: Boolean
            get() = weiJuSP.getBoolean("is_category_translation_fragment_remind_show", true)
            set(value) = weiJuSP.edit { putBoolean("is_category_translation_fragment_remind_show", value) }

        var newVersionDownloadUrl: String
            get() = weiJuSP.getString("download_url", "")!!
            set(value) = weiJuSP.edit { putString("download_url", value) }

        var versionName: String
            get() = weiJuSP.getString("version_name", BuildConfig.VERSION_NAME)!!
            set(value) = weiJuSP.edit { putString("version_name", value) }
    }

    inner class HookListSP {
        /**
         * 标记为true，开启Hook
         * @param pkgName String
         */
        fun add(pkgName: String) {
            hookListSP.edit {
                putBoolean(pkgName, true)
            }
        }

        /**
         * 标记为false，取消Hook
         * @param pkgName String
         */
        fun remove(pkgName: String) {
            hookListSP.edit {
                putBoolean(pkgName, false)
            }
        }
    }

    companion object {
        // For Singleton instantiation
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SPManager? = null

        fun getInstance(ctx: Context): SPManager {
            return instance ?: synchronized(this) {
                instance ?: SPManager(ctx).also { instance = it }
            }
        }
    }
}