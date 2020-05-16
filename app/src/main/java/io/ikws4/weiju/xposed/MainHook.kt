package io.ikws4.weiju.xposed

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.ikws4.library.xposedktx.hookMethod
import io.ikws4.weiju.BuildConfig
import io.ikws4.weiju.utilities.HOOK_LIST_SP
import io.ikws4.weiju.utilities.WEIJU_PKG_NAME
import io.ikws4.weiju.utilities.WEIJU_SP
import io.ikws4.weiju.utilities.XSPUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Keep
@ExperimentalCoroutinesApi
class MainHook : IXposedHookLoadPackage {
    private var isRunning = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val pkgName = lpparam.packageName

        Instrumentation::class.java.hookMethod("callApplicationOnCreate", Application::class.java) { param ->
            val application = param.args[0] as Application
            // 刷新模块状态
            setModuleState(application, lpparam)
            // Hook在Hook_List_SP名单中的应用
            val hookListSP = XSPUtils(application, HOOK_LIST_SP)

            if (hookListSP.getBoolean(pkgName)) {
                val sp = XSPUtils(application, pkgName)
                StatusBarHook(sp)
                NavBarHook(sp)
                ScreenHook(sp)
                TranslationHook(application, pkgName)
                VariableHook(sp)
            }
            isRunning = true
        }
    }

    /**
     * 刷新模块状态（UPDATE ACTIVE WARNING）
     * @param context Context
     * @param lpparam LoadPackageParam
     */
    private fun setModuleState(context: Context, lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == WEIJU_PKG_NAME) {
            val className = "$WEIJU_PKG_NAME.ui.fragments.MainHomeFragment"
            className.hookMethod(lpparam.classLoader, "refreshModuleState", String::class.java,
                beforeHookedMethod = {
                    val weiJuSP = XSPUtils(context, WEIJU_SP)
                    if (BuildConfig.VERSION_NAME < weiJuSP.getString("version_name"))
                        it.args[0] = "UPDATE"
                    else
                        it.args[0] = "ACTIVE"
                })
        }
    }


    companion object {
        fun log(text: String) {
            if (BuildConfig.DEBUG) {
                XposedBridge.log("WeiJu-> $text")
            }
        }

        fun log(t: Throwable) {
            if (BuildConfig.DEBUG) {
                XposedBridge.log("WeiJu-> ${Log.getStackTraceString(t)}")
            }
        }
    }
}