package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import io.ikws4.weiju.R

class CategoryScreenFragment : PreferenceFragmentCompat() {
    private val args: CategoryScreenFragmentArgs by navArgs()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.pkgName
        setPreferencesFromResource(R.xml.screen_preference, rootKey)
    }
}