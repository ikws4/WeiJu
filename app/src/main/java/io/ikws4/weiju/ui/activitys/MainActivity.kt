package io.ikws4.weiju.ui.activitys

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.ikws4.weiju.R
import io.ikws4.weiju.databinding.ActivityMainBinding
import io.ikws4.weiju.ui.fragments.MainHomeFragmentDirections
import io.ikws4.weiju.ui.viewmodels.UserViewModel
import io.ikws4.weiju.utilities.FREE_SOGOU_API_REWARDED_AD_ID
import io.ikws4.weiju.utilities.InjectorUtils
import io.ikws4.weiju.utilities.SPManager
import io.ikws4.weiju.utilities.TEST_DEVICE_ID

class MainActivity : BasicActivity() {
    private lateinit var navController: NavController
    private lateinit var rewardedAd: RewardedAd
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<UserViewModel> { InjectorUtils.provideUserViewModelFactory(this) }
    private val spManager by lazy { SPManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        rewardedAd = createAndLoadRewardedAd()
        // 更新数据
        viewModel.user.observe(this, Observer { user ->
            spManager.WeiJuSP().freeSogouApiAmount = user?.freeSogouApiAmount ?: 0
        })
        setupToolbar()
        createClickRewardedAdListener()
        // 获取电话权限，用于读取IMEI及IMSI
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun createClickRewardedAdListener() {
        binding.rewardedAdView.setOnClickListener {
            if (rewardedAd.isLoaded) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.rewarded_ad)
                    .setMessage(getString(R.string.rewarded_ad_watch_remind, spManager.WeiJuSP().freeSogouApiAmount.toString()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.watch) { _, _ ->
                        val adCallback = object : RewardedAdCallback() {
                            override fun onUserEarnedReward(reward: RewardItem) {
                                with(reward) {
                                    viewModel.increaseFreeSogouApiAmount(amount)
                                    MaterialAlertDialogBuilder(this@MainActivity)
                                        .setTitle(R.string.reward)
                                        .setMessage(type + amount)
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show()
                                }
                            }

                            override fun onRewardedAdClosed() {
                                this@MainActivity.rewardedAd = createAndLoadRewardedAd()
                            }
                        }
                        rewardedAd.show(this, adCallback)
                    }.show()
            } else {
                rewardedAd = createAndLoadRewardedAd()
            }
        }
    }

    private fun setupToolbar() {
        with(binding) {
            val margin = resources.getDimensionPixelSize(R.dimen.normal)
            toolbar.setPadding(0, 0, margin, 0)
            setSupportActionBar(toolbar)
            toolbarTitle.text = getString(R.string.app_name)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                navController.navigate(
                    MainHomeFragmentDirections
                        .toSettingActivity(SettingActivity.HOME)
                )
            }
        }
        return true
    }

    /**
     * 记载激励广告
     * @return RewardedAd
     */
    private fun createAndLoadRewardedAd(): RewardedAd {
        with(binding) {
            rewardedAdView.visibility = View.GONE
            rewardedAdLoadingView.visibility = View.VISIBLE
        }
        val rewardedAd = RewardedAd(this, FREE_SOGOU_API_REWARDED_AD_ID)
        val adRequest = AdRequest.Builder()
            .addTestDevice(TEST_DEVICE_ID)
            .build()
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
                with(binding) {
                    with(rewardedAdView) {
                        visibility = View.VISIBLE
                        setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_sentiment_satisfied))
                    }
                    rewardedAdLoadingView.visibility = View.GONE
                }
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
                with(binding) {
                    with(rewardedAdView) {
                        visibility = View.VISIBLE
                        setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_sentiment_dissatisfied))
                    }
                    rewardedAdLoadingView.visibility = View.GONE
                }
            }
        }
        rewardedAd.loadAd(adRequest, adLoadCallback)
        return rewardedAd
    }

    companion object {
        const val TAG = "MainActivity"
    }

}
