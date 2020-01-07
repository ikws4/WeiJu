package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R

class SettingHomeFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_home_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "general_screen" -> {
                findNavController().navigate(SettingHomeFragmentDirections.toSettingGeneralFragment())
            }
            "template_screen" -> {
                findNavController().navigate(SettingHomeFragmentDirections.toSettingTemplateFragment())
            }
            "translation_screen" -> {
                findNavController().navigate(SettingHomeFragmentDirections.toSettingTranslationFragment())
            }
            "about_screen" -> {
                findNavController().navigate(SettingHomeFragmentDirections.toSettingAboutFragment())
            }
        }
        return true
    }
}
