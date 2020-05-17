package io.ikws4.weiju.xposed

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.telephony.TelephonyManager
import io.ikws4.library.xposedktx.hookMethod
import io.ikws4.library.xposedktx.replaceMethod
import io.ikws4.library.xposedktx.setStaticObjectField
import io.ikws4.weiju.utilities.XSPUtils

class VariableHook(sp: XSPUtils, private val classLoader: ClassLoader) {
    private val isEnable = sp.getBoolean("is_enable_variable")
    private val device = sp.getString("variable_device")
    private val productName = sp.getString("variable_product_name")
    private val model = sp.getString("variable_model")
    private val brand = sp.getString("variable_brand")
    private val release = sp.getString("variable_release")
    private val longitude = sp.getString("variable_longitude").toDouble()
    private val latitude = sp.getString("variable_latitude").toDouble()
    private val imei = sp.getString("variable_imei")
    private val imsi = sp.getString("variable_imsi")

    init {
        if (isEnable) {
            replaceBuildConfig()
            replaceLocationInfo()
            replaceImei()
            replaceImsi()
        }
    }

    private fun replaceBuildConfig() {

        with(Build::class.java) {
            // 设备代号
            // ro.product.device=mido
            setStaticObjectField("DEVICE", device)

            // ro.product.name=aosp_mido
            setStaticObjectField("PRODUCT", productName)

            // 机型
            // ro.product.model=Redmi Note 4
            setStaticObjectField("MODEL", model)

            // 厂商
            // ro.product.brand=xiaomi
            // ro.product.manufacturer=xiaomi
            setStaticObjectField("BRAND", brand)
            setStaticObjectField("MANUFACTURER", brand)
        }
        // 安卓版本
        // ro.build.version.release=9
        Build.VERSION::class.java.setStaticObjectField("RELEASE", release)
    }

    private fun replaceLocationInfo() {
        Location::class.java.hookMethod("getLongitude") { param ->
            param.result = this@VariableHook.longitude
        }
        Location::class.java.hookMethod("getLatitude") { param ->
            param.result = this@VariableHook.latitude
        }

        // 百度
        val bdLocation = "com.baidu.location.BDLocation"
        bdLocation.hookMethod(classLoader, "getLongitude") { param ->
            param.result = this@VariableHook.longitude
        }
        bdLocation.hookMethod(classLoader, "getLatitude") { param ->
            param.result = this@VariableHook.latitude
        }
    }

    @SuppressLint("MissingPermission")
    private fun replaceImei() {
        if (imei == "error: not permission") return

        with(TelephonyManager::class.java) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                replaceMethod("getImei") {
                    return@replaceMethod this@VariableHook.imei
                }
            } else {
                replaceMethod("getDeviceId") {
                    return@replaceMethod this@VariableHook.imei
                }
            }
        }
    }

    private fun replaceImsi() {
        if (imsi == "error: not permission") return

        TelephonyManager::class.java.replaceMethod("getSubscriberId") {
            return@replaceMethod imsi
        }
    }
}
