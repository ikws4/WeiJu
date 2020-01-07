package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import io.ikws4.weiju.R
import io.ikws4.weiju.ui.preferences.EditTextPreference

class CategoryNavBarFragment : PreferenceFragmentCompat() {
    private val args: CategoryNavBarFragmentArgs by navArgs()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.pkgName
        setPreferencesFromResource(R.xml.navigation_bar_preference, rootKey)

        with(preferenceManager) {
            val immersive = findPreference<ListPreference>("immersive_nav_bar")!!
            val customColor = findPreference<EditTextPreference>("custom_nav_bar_color")!!.apply {
                isEnabled = immersive.value == "Custom"
            }
            immersive.setOnPreferenceChangeListener { _, newValue ->
                customColor.isEnabled = newValue.toString() == "Custom"
                true
            }

        }
    }
}