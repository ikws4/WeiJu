package io.ikws4.weiju.ui.activitys

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.google.android.material.appbar.MaterialToolbar
import io.ikws4.weiju.R
import io.ikws4.weiju.databinding.ActivitySettingBinding

class SettingActivity : BasicActivity() {
    private val args: SettingActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = setContentView<ActivitySettingBinding>(this, R.layout.activity_setting)
        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar = binding.actionBar.toolbar
        setupToolbarWithNav(navController, toolbar)
        navigation(navController)
    }

    /**
     * 用来导航到目标Fragment，方便切换
     * @param navController NavController
     */
    private fun navigation(navController: NavController) {
        val graph = navController.graph.apply {
            startDestination = when (args.pageName) {
                HOME -> R.id.settingHomeFragment
                GENERAL -> R.id.settingGeneralFragment
                TEMPLATE -> R.id.settingTemplateFragment
                TRANSLATION -> R.id.settingTranslationFragment
                else -> R.id.settingHomeFragment
            }
        }
        navController.graph = graph
    }

    /**
     * 设置Toolbar
     * 把destination的label属性作为toolbar的子标题
     * @param navController NavController
     * @param toolbar MaterialToolbar
     */
    private fun setupToolbarWithNav(navController: NavController, toolbar: MaterialToolbar) {
        setSupportActionBar(toolbar)
        with(navController) {
            addOnDestinationChangedListener { _, destination, _ ->
                toolbar.subtitle = destination.label
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val HOME = "/"
        const val GENERAL = "/general"
        const val TEMPLATE = "/template"
        const val TRANSLATION = "/translation"
        const val ABOUT = "/about"
    }
}
