package io.ikws4.weiju.worker

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.utilities.LogcatManager
import kotlinx.coroutines.coroutineScope
import saveToFile

class AddAppInfoWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val appInfoDao = AppDatabase.getInstance(applicationContext).appInfoDao()
            val pm = applicationContext.packageManager
            val pkgName = inputData.getString("pkgName")!!
            val applicationInfo = pm.getApplicationInfo(pkgName, 0)
            val path = "${applicationContext.filesDir.path}/application_icons/"
            val fileName = "${applicationInfo.packageName}.png"
            val fullPath = path + fileName


            appInfoDao.insert(
                AppInfo(
                    pkgName = applicationInfo.packageName,
                    name = applicationInfo.loadLabel(pm).toString(),
                    iconPath = fullPath,
                    isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            )
            applicationInfo.loadIcon(pm).saveToFile(fileName, path)
            Result.success()
        } catch (ex: Exception) {
            LogcatManager.saveToFile(applicationContext, ex)
            Result.failure()
        }
    }

    companion object {
        const val TAG = "AddAppInfoWorker"
    }

}