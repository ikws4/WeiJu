package io.ikws4.weiju.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.ikws4.weiju.R
import io.ikws4.weiju.ui.activitys.SettingActivity
import io.ikws4.weiju.utilities.SPManager

class CategoryTranslationFragment : PreferenceFragmentCompat() {
    private val args: CategoryTranslationFragmentArgs by navArgs()
    private val spManager by lazy { SPManager.getInstance(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = args.pkgName
        setPreferencesFromResource(R.xml.translation_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 提醒设置appid和key的对话框
        if (spManager.WeiJuSP().isCategoryTranslationFragmentRemindShow)
            MaterialAlertDialogBuilder(context!!)
                .setTitle(R.string.warning)
                .setMessage(R.string.set_translation_appid_key_remind)
                .setCancelable(false)
                .setPositiveButton(R.string.go) { _, _ ->
                    findNavController().navigate(CategoryTranslationFragmentDirections.toSettingActivity(SettingActivity.TRANSLATION))
                }
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(getString(R.string.not_remind_again)) { _, _ ->
                    // 禁止再次显示
                    spManager.WeiJuSP().isCategoryTranslationFragmentRemindShow = false
                }.show()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            "translation_data_files" -> {
                findNavController().navigate(
                    CategoryTranslationFragmentDirections
                        .toTranslationEditorActivity(args.pkgName)
                )
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_category_translation, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                findNavController().navigate(CategoryTranslationFragmentDirections.toSettingActivity(SettingActivity.TRANSLATION))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}