package io.ikws4.weiju.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.ikws4.weiju.worker.AddAppInfoWorker
import io.ikws4.weiju.worker.RemoveAppInfoWorker


class ApkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pkgName = intent.data!!.schemeSpecificPart
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val data = Data.Builder()
                    .putString("pkgName", pkgName)
                    .build()
                val result = OneTimeWorkRequestBuilder<AddAppInfoWorker>()
                    .setInputData(data)
                    .build()
                WorkManager.getInstance(context).enqueue(result)
            }

            Intent.ACTION_PACKAGE_FULLY_REMOVED, Intent.ACTION_PACKAGE_REMOVED -> {
                val data = Data.Builder()
                    .putString("pkgName", pkgName)
                    .build()
                val result = OneTimeWorkRequestBuilder<RemoveAppInfoWorker>()
                    .setInputData(data)
                    .build()
                WorkManager.getInstance(context).enqueue(result)
            }
        }
    }
}
