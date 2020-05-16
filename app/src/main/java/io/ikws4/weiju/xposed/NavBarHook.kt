package io.ikws4.weiju.xposed

import android.R
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import de.robv.android.xposed.XSharedPreferences
import io.ikws4.library.xposedktx.hookMethod

class NavBarHook(sp: XSharedPreferences) {
    private val isEnable = sp.getBoolean("is_enable_nav_bar", false)
    private val isHide = sp.getBoolean("is_hide_nav_bar", false)
    private val immersive = sp.getString("immersive_nav_bar", "")
    private val customColor = sp.getString("custom_nav_bar_color", "")
    private val iconColor = sp.getString("nav_bar_icon_color", "")

    init {
        if (isEnable) {
            Activity::class.java.hookMethod("onCreate", Bundle::class.java,
                afterHookedMethod = {
                    with(window) {
                        val flag = decorView.systemUiVisibility

                        // 沉浸
                        if (immersive != "Default") {
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
                                    if (customColor != "Default") {
                                        try {
                                            val color = Color.parseColor(customColor)
                                            navigationBarColor = color
                                        } catch (ex: Exception) {
                                            ex.printStackTrace()
                                            Toast.makeText(this@hookMethod, "WeiJU: ${ex.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                            navigationBarColor = typedValue.data
                        } else {
                            navigationBarColor = Color.parseColor(customColor)
                        }

                        // 显隐状态栏
                        if (isHide) {
                            decorView.systemUiVisibility =
                                flag or (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                        }

                        // 图标颜色
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            when (iconColor) {
                                "Grey" -> {
                                    decorView.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                                }
                                "White" -> {
                                    decorView.systemUiVisibility = flag xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                                }
                            }
                        }
                    }
                })
        }
    }
}