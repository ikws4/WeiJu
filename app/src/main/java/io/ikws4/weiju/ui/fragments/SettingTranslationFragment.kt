package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import io.ikws4.weiju.R
import io.ikws4.weiju.R.xml
import io.ikws4.weiju.utilities.SPManager

class SettingTranslationFragment : PreferenceFragmentCompat() {
    private val spManager by lazy { SPManager.getInstance(context!!) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xml.setting_translation_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "api_sogou" -> {
                apiConfigBottomSheet(R.string.so_gou,
                    setHint = { inputKey, inputAppId ->
                        inputKey.setText(apiSogouKey)
                        inputAppId.setText(apiSogouAppid)

                    }, onPositiveButtonClick = { key, appId ->
                        apiSogouKey = key
                        apiSogouAppid = appId
                    })
            }
            "api_baidu" -> {
                apiConfigBottomSheet(R.string.bai_du,
                    setHint = { inputKey, inputAppId ->
                        inputKey.setText(apiBaiduKey)
                        inputAppId.setText(apiBaiduAppid)

                    }, onPositiveButtonClick = { key, appId ->
                        apiBaiduKey = key
                        apiBaiduAppid = appId
                    })
            }
            "api_youdao" -> {
                apiConfigBottomSheet(R.string.you_dao
                    , setHint = { inputKey, inputAppId ->
                        inputKey.setText(apiYoudaoKey)
                        inputAppId.setText(apiYoudaoAppid)

                    }, onPositiveButtonClick = { key, appId ->
                        apiYoudaoKey = key
                        apiYoudaoAppid = appId
                    })
            }
        }
        return true
    }

    private fun apiConfigBottomSheet(
        @StringRes titleRes: Int, setHint: SPManager.WeiJuSP. (inputKey: TextInputEditText, inputAppId: TextInputEditText) -> Unit,
        onPositiveButtonClick: SPManager.WeiJuSP.(key: String, appId: String) -> Unit
    ) {
        val dialog = MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(titleRes)
            customView(R.layout.bottom_sheet_api_config, horizontalPadding = true)
            positiveButton(android.R.string.ok) { dialog ->
                with(spManager.WeiJuSP()) {
                    val inputKey = dialog.getCustomView().findViewById<TextInputEditText>(R.id.input_key)
                    val inputAppId = dialog.getCustomView().findViewById<TextInputEditText>(R.id.input_app_id)
                    onPositiveButtonClick(this, inputKey.text.toString(), inputAppId.text.toString())
                }
            }
            negativeButton(android.R.string.cancel)
        }
        val customView = dialog.getCustomView()
        val inputKey = customView.findViewById<TextInputEditText>(R.id.input_key)
        val inputAppId = customView.findViewById<TextInputEditText>(R.id.input_app_id)
        setHint(spManager.WeiJuSP(), inputKey, inputAppId)
    }
}