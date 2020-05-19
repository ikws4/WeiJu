package io.ikws4.weiju

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import io.ikws4.weiju.servers.ApkServer
import io.ikws4.weiju.utilities.WEIJU_SP
import io.ikws4.weiju.worker.CheckUpdateWorker
import io.ikws4.weiju.worker.UpdateAppDatabaseWorker

class WeiJuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, ApkServer::class.java)
        ContextCompat.startForegroundService(this, intent)
        MobileAds.initialize(this)
        startWorkers()
        themeChangeListener()
    }

    private fun startWorkers() {
        val workManager = WorkManager.getInstance(applicationContext)

        val checkUpdateWorkerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val checkUpdateWorker = OneTimeWorkRequestBuilder<CheckUpdateWorker>()
            .setConstraints(checkUpdateWorkerConstraints)
            .build()

        val updateAppDatabaseWorker = OneTimeWorkRequestBuilder<UpdateAppDatabaseWorker>()
            .build()

        workManager.enqueue(updateAppDatabaseWorker)
        workManager.enqueue(checkUpdateWorker)
    }

    private fun themeChangeListener() {
        val sp = getSharedPreferences(WEIJU_SP, Context.MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(sp.getString("theme", "-1")!!.toInt())
        sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "theme") {
                AppCompatDelegate.setDefaultNightMode(sharedPreferences.getString("theme", "-1")!!.toInt())
            }
        }
    }
}