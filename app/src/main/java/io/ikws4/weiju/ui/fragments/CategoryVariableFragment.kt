package io.ikws4.weiju.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ikws4.weiju.R
import io.ikws4.weiju.data.VariableModel
import io.ikws4.weiju.ui.preferences.EditTextPreference
import io.ikws4.weiju.utilities.LogcatManager


class CategoryVariableFragment : PreferenceFragmentCompat() {
    private val args: CategoryVariableFragmentArgs by navArgs()
    private var variableModel: VariableModel? = null


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


        val model = findPreference<EditTextPreference>("variable_model")!!.text
        val brand = findPreference<EditTextPreference>("variable_brand")!!.text
        val device = findPreference<EditTextPreference>("variable_device")!!.text
        val productName = findPreference<EditTextPreference>("variable_product_name")!!.text
        val androidRelease = findPreference<EditTextPreference>("variable_release")!!.text
        val longitude = findPreference<EditTextPreference>("variable_longitude")!!.text
        val latitude = findPreference<EditTextPreference>("variable_latitude")!!.text
        val imeiStr = findPreference<EditTextPreference>("variable_imei")!!.text
        val imsiStr = findPreference<EditTextPreference>("variable_imsi")!!.text

        variableModel = VariableModel(model, brand, device, productName, androidRelease, longitude, latitude, imeiStr, imsiStr)

        setHasOptionsMenu(true)
    }

    private fun exportConfig() {
        val configStr = Gson().toJson(variableModel)
        MaterialDialog(requireContext()).show {
            title(R.string.export_config)
            message(text = configStr)
            positiveButton(android.R.string.copy) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("variable config", configStr)
                clipboard.primaryClip = clip
                Toast.makeText(requireContext(), R.string.copy_success, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun importConfig() {
        MaterialDialog(requireContext()).show {
            title(R.string.import_config)
            input()
            positiveButton(android.R.string.ok) {
                val configStr = it.getInputField().text.toString()

                try {
                    variableModel = getVariableModelFromJsonString(configStr).apply {
                        findPreference<EditTextPreference>("variable_model")!!.text = model
                        findPreference<EditTextPreference>("variable_brand")!!.text = brand
                        findPreference<EditTextPreference>("variable_device")!!.text = device
                        findPreference<EditTextPreference>("variable_product_name")!!.text = productName
                        findPreference<EditTextPreference>("variable_release")!!.text = androidRelease
                        findPreference<EditTextPreference>("variable_longitude")!!.text = longitude
                        findPreference<EditTextPreference>("variable_latitude")!!.text = latitude
                        findPreference<EditTextPreference>("variable_imei")!!.text = imei
                        findPreference<EditTextPreference>("variable_imsi")!!.text = imsi
                    }
                }catch (e:Exception){
                    Toast.makeText(requireContext(),R.string.import_import_error,Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_variable_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_config ->
                exportConfig()

            R.id.import_config -> importConfig()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getVariableModelFromJsonString(str: String): VariableModel {
        return Gson().fromJson(
            str, object : TypeToken<VariableModel>() {}.type
        )
    }
}