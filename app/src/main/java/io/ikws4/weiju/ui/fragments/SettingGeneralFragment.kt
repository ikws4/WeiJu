package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R
import io.ikws4.weiju.utilities.LogcatManager

class SettingGeneralFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_general_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
       return when (preference.key) {
            "report_bug" -> {
                val message = LogcatManager.getSavedLog(context!!)
                LogcatManager.show(context!!, message,true)
                true
            }
           else -> super.onPreferenceTreeClick(preference)
        }
    }
}