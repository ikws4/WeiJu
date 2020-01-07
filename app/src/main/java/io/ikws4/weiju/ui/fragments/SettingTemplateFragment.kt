package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R
import io.ikws4.weiju.utilities.TEMPLATE_SP

class SettingTemplateFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_template_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            "config_template" -> {
                val title = preference.title.toString()
                findNavController().navigate(SettingTemplateFragmentDirections.toCategoryActivity(title, TEMPLATE_SP))
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }
}