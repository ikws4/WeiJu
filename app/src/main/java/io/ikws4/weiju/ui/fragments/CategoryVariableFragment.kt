package io.ikws4.weiju.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R
import io.ikws4.weiju.ui.preferences.EditTextPreference
import io.ikws4.weiju.utilities.LogcatManager

class CategoryVariableFragment : PreferenceFragmentCompat() {
    private val args: CategoryVariableFragmentArgs by navArgs()

    @SuppressLint("HardwareIds")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.pkgName
        setPreferencesFromResource(R.xml.variable_preference, rootKey)

        // TODO: 逻辑待优化
        val telephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var imei = "error: not permission"
        var imsi = "error: not permission"
        try {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei ?: "error: failure"
                } else {
                    telephonyManager.deviceId ?: "error: failure"
                }
                imsi = telephonyManager.subscriberId ?: "error: failure"
            }
        } catch (ex: Exception) {
            LogcatManager.saveToFile(context!!, ex, true)
        }

        findPreference<EditTextPreference>("variable_model")!!.apply {
            if (text == null) text = Build.MODEL
        }
        findPreference<EditTextPreference>("variable_brand")!!.apply {
            if (text == null) text = Build.BRAND
        }
        findPreference<EditTextPreference>("variable_device")!!.apply {
            if (text == null) text = Build.DEVICE
        }
        findPreference<EditTextPreference>("variable_product_name")!!.apply {
            if (text == null) text = Build.PRODUCT
        }
        findPreference<EditTextPreference>("variable_release")!!.apply {
            if (text == null) text = Build.VERSION.RELEASE
        }
        findPreference<EditTextPreference>("variable_longitude")!!.apply {
            if (text == null) text = ""
        }
        findPreference<EditTextPreference>("variable_latitude")!!.apply {
            if (text == null) text = ""
        }
        findPreference<EditTextPreference>("variable_imei")!!.apply {
            if (text == null) text = imei
        }
        findPreference<EditTextPreference>("variable_imsi")!!.apply {
            if (text == null) text = imsi
        }
    }
}