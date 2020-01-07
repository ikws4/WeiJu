package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R

class SettingAboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_about_preference, rootKey)
    }
}