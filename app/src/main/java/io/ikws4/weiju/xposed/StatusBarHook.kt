package io.ikws4.weiju.xposed

import android.R
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import io.ikws4.library.xposedktx.hookMethod
import io.ikws4.weiju.utilities.XSPUtils

class StatusBarHook(sp: XSPUtils) {
    private val isEnable = sp.getBoolean("is_enable_status_bar")
    private val isHide = sp.getBoolean("is_hide_status_bar")
    private val immersive = sp.getString("immersive_status_bar")
    private val customColor = sp.getString("custom_status_bar_color")
    private val iconColor = sp.getString("status_bar_icon_color")

    init {
        if (isEnable) {
            Activity::class.java.hookMethod("onCreate", Bundle::class.java, afterHookedMethod = {
                with(window) {
                    if (immersive != "Default") {
                        // 沉浸
                        val typedValue = TypedValue()
                        when (immersive) {
                            "ColorPrimary" -> {
                                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                            }
                            "ColorPrimaryDark" -> {
                                theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
                            }
                            "ColorAccent" -> {
                                theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
                            }
                            "Custom" -> {
                                try {
                                    val color = Color.parseColor(customColor)
                                    statusBarColor = color
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    Toast.makeText(this@hookMethod, "WeiJu: ${ex.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        statusBarColor = typedValue.data
                    }


                    // 显隐状态栏
                    if (isHide) {
                        addFlags(LayoutParams.FLAG_FULLSCREEN)
                    } else {
                        clearFlags(LayoutParams.FLAG_FULLSCREEN)
                    }

                    // 图标颜色
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val flag = decorView.systemUiVisibility

                        when (iconColor) {
                            "Grey" -> {
                                decorView.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                            "White" -> {
                                decorView.systemUiVisibility = flag xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                        }
                    }
                }
            })
        }
    }
}