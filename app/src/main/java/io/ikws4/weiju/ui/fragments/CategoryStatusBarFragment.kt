package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R

class CategoryStatusBarFragment : PreferenceFragmentCompat() {
    private val args: CategoryStatusBarFragmentArgs by navArgs()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.pkgName
        setPreferencesFromResource(R.xml.status_bar_preference, rootKey)

        with(preferenceManager) {
            val immersive = findPreference<ListPreference>("immersive_status_bar")!!
            val customColor = findPreference<EditTextPreference>("custom_status_bar_color")!!.apply {
                isEnabled = immersive.value == "Custom"
            }
            immersive.setOnPreferenceChangeListener { _, newValue ->
                customColor.isEnabled = newValue.toString() == "Custom"
                true
            }
        }
    }


}