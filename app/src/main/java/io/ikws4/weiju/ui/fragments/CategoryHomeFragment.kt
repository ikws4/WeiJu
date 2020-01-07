package io.ikws4.weiju.ui.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import io.ikws4.weiju.R
import io.ikws4.weiju.databinding.FragmentCategoryHomeBinding
import io.ikws4.weiju.utilities.LogcatManager
import io.ikws4.weiju.utilities.SPManager
import io.ikws4.weiju.utilities.TEMPLATE_SP
import io.ikws4.weiju.utilities.TEST_DEVICE_ID
import replaceAll

class CategoryHomeFragment : Fragment() {
    // App单独的Hook配置文件
    private lateinit var configSP: SharedPreferences
    private val args: CategoryHomeFragmentArgs by navArgs()
    private lateinit var binding: FragmentCategoryHomeBinding
    private val spManager by lazy {
        SPManager.getInstance(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        configSP = context!!.getSharedPreferences(args.pkgName, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryHomeBinding.inflate(inflater, container, false)
        refreshIconColor()
        setupNavigationDirection()
        setupFab()
        setupAd()
        return binding.root
    }

    private fun setupAd() {
        val adRequest = AdRequest.Builder()
            .addTestDevice(TEST_DEVICE_ID)
            .build()
        with(binding.adView) {
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    binding.isAdLoaded = !isLoading
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()
    }

    /**
     * 用来快速打开应用
     * 当pkgName == io.ikws4.weiju.template(模版)，隐藏
     */
    private fun setupFab() {
        if (args.pkgName != TEMPLATE_SP) {
            // TODO: 添加加载动画
            binding.fab.setOnClickListener {
                try {
                    val pkgIntent = context!!.packageManager.getLaunchIntentForPackage(args.pkgName)
                    pkgIntent!!.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    startActivity(pkgIntent)
                } catch (ex: Exception) {
                    LogcatManager.show(context!!, ex)
                }
            }
        } else {
            binding.fab.hide()
        }
    }

    private fun setupNavigationDirection() {
        with(binding) {
            toStatusBarFragment.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    CategoryHomeFragmentDirections.toCategoryStatusBarFragment(
                        args.pkgName
                    )
                )
            )
            toNavBarFragment.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    CategoryHomeFragmentDirections.toCategoryNavBarFragment(
                        args.pkgName
                    )
                )
            )
            toScreenFragment.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    CategoryHomeFragmentDirections.toCategoryScreenFragment(
                        args.pkgName
                    )
                )
            )
            toTranslationFragment.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    CategoryHomeFragmentDirections.toCategoryTranslationFragment(
                        args.pkgName
                    )
                )
            )
            toVariableFragment.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    CategoryHomeFragmentDirections.toCategoryVariableFragment(
                        args.pkgName
                    )
                )
            )
        }
    }

    private fun refreshIconColor() {
        val isEnableStatusBar = configSP.getBoolean("is_enable_status_bar", false)
        val isEnableNavigationBar = configSP.getBoolean("is_enable_nav_bar", false)
        val isEnableScreen = configSP.getBoolean("is_enable_screen", false)
        val isEnableTranslation = configSP.getBoolean("is_enable_translation", false)
        val isEnableVariable = configSP.getBoolean("is_enable_variable", false)
        binding.apply {
            statusBarItemColor = if (isEnableStatusBar) R.color.red else android.R.color.transparent
            navBarItemColor = if (isEnableNavigationBar) R.color.green else android.R.color.transparent
            screenItemColor = if (isEnableScreen) R.color.amber else android.R.color.transparent
            translationItemColor = if (isEnableTranslation) R.color.google_blue else android.R.color.transparent
            variableItemColor = if (isEnableVariable) R.color.blue else android.R.color.transparent
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (args.pkgName != TEMPLATE_SP) {
            inflater.inflate(R.menu.menu_fragment_category_home, menu)
        } else {
            inflater.inflate(R.menu.menu_fragment_category_home_template, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val templateSP = spManager.templateSP

        return when (item.itemId) {
            R.id.action_restore -> {
                val undoData = configSP.all

                configSP.edit { clear() }
                refreshIconColor()
                Snackbar.make(view!!, R.string.reset_success, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        configSP.replaceAll(undoData)
                        refreshIconColor()
                    }
                    .show()
                true
            }
            R.id.action_save_as_template -> {
                val undoData = templateSP.all

                templateSP.replaceAll(configSP.all)
                Snackbar.make(view!!, R.string.template_change, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        templateSP.replaceAll(undoData)
                    }
                    .show()
                true
            }
            R.id.action_apply_template -> {
                val undoData = configSP.all

                configSP.replaceAll(templateSP.all)
                refreshIconColor()
                Snackbar.make(view!!, R.string.apply_success, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        configSP.replaceAll(undoData)
                        refreshIconColor()
                    }
                    .show()
                true
            }
            R.id.action_force_stop -> {
                // 如果是在VXP环境，那么启动VXP的任务管理器
                try {
                    if (System.getProperty("vxp") != null) {
                        val intent = Intent()
                        val componentName = ComponentName("io.va.exposed", "io.virtualapp.settings.TaskManageActivity")
                        intent.component = componentName
                        startActivity(intent)
                    } else {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.fromParts("package", args.pkgName, null)
                        startActivity(intent)
                    }
                } catch (ex: Exception) {
                    LogcatManager.show(context!!, ex)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}