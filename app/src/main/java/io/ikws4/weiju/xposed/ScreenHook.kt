package io.ikws4.weiju.xposed

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import de.robv.android.xposed.XSharedPreferences
import io.ikws4.library.xposedktx.*
import java.util.*


class ScreenHook(sp: XSharedPreferences) {
    private val isEnable = sp.getBoolean("is_enable_screen", false)
    private val screenOrientation = sp.getString("screen_orientation", "")
    private val isForceScreenshot = sp.getBoolean("is_enable_force_screenshot", false)
    private val language = sp.getString("language", "")
    private val isCancelDialog = sp.getBoolean("is_cancel_dialog", false)
    private val dpi = sp.getString("custom_dpi", "")

    init {
        if (isEnable) {
            screenOrientation()
            forceScreenshot()
            cancelDialog()
            replaceLanguageAndDpiAndOrientation()
        }
    }

    private fun screenOrientation() {
        if (screenOrientation != "Default") {
            Activity::class.java.hookMethod("onCreate", Bundle::class.java,
                afterHookedMethod = {
                    requestedOrientation = screenOrientation.toInt()
                })
        }
    }

    private fun forceScreenshot() {
        if (isForceScreenshot) {
            Window::class.java.replaceMethod("setFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType,
                replaceHookedMethod = { param ->
                    val flags = param.args[0] as Int
                    val mask = param.args[1] as Int

                    if (!(flags == WindowManager.LayoutParams.FLAG_SECURE && mask == WindowManager.LayoutParams.FLAG_SECURE)) {
                        val attrs = attributes
                        attrs.flags = attrs.flags and mask.inv() or (flags and mask)
                        val mForcedWindowFlags = getIntField("mForcedWindowFlags")
                        setIntField("mForcedWindowFlags", mForcedWindowFlags or mask)
                        invokeMethod(null, "dispatchWindowAttributesChanged", attrs)
                    }
                    return@replaceMethod null
                })
        }
    }

    private fun cancelDialog() {
        if (isCancelDialog) {
            for (methodName in arrayOf("setCancelable", "setCanceledOnTouchOutside")) {
                Dialog::class.java.hookMethod(methodName, Boolean::class.javaPrimitiveType,
                    beforeHookedMethod = { param ->
                        param.args[0] = true
                    })
            }
        }
    }

    private fun replaceLanguageAndDpiAndOrientation() {
        val languageRes = language.split("_")

        if (language != "Default") {
            with(Locale::class.java) {
                replaceMethod("getLanguage") {
                    return@replaceMethod languageRes.first()
                }
                replaceMethod("getCountry") {
                    return@replaceMethod languageRes.last()
                }
            }
        }

        ContextWrapper::class.java.hookMethod("attachBaseContext", Context::class.java, beforeHookedMethod = { param ->
            val ctx = param.args[0] as Context
            val locale = Locale(languageRes.first(), languageRes.last())
            val configuration = Configuration(ctx.resources.configuration)
            if (dpi != "Default") {
                configuration.densityDpi = dpi.toInt()
            }
            if (language != "Default") {
                Locale.setDefault(locale)
                configuration.setLocale(locale)
            }
            param.args[0] = ctx.createConfigurationContext(configuration)
        })
    }
}