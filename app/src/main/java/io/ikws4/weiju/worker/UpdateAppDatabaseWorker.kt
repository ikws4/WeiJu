package io.ikws4.weiju.worker

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.utilities.LogcatManager
import kotlinx.coroutines.coroutineScope
import saveToFile

class UpdateAppDatabaseWorker(ctx: Context, workerParameters: WorkerParameters) : CoroutineWorker(ctx, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val appInfoDao = AppDatabase.getInstance(applicationContext).appInfoDao()
            val pm = applicationContext.packageManager
            val data = arrayListOf<AppInfo>()

            pm.getInstalledPackages(0).forEach { packageInfo: PackageInfo ->
                val appInfo = appInfoDao.getByPkgName(packageInfo.packageName)

                if (appInfo == null) {
                    val path = "${applicationContext.filesDir.path}/application_icons/"
                    val fileName = "${packageInfo.packageName}.png"
                    val fullPath = path + fileName

                    data.add(
                        AppInfo(
                            pkgName = packageInfo.packageName,
                            name = packageInfo.applicationInfo.loadLabel(pm).toString(),
                            iconPath = fullPath,
                            isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        )
                    )

                    packageInfo.applicationInfo.loadIcon(pm).saveToFile(fileName, path)

                } else if (appInfoDao.getByPkgName(packageInfo.packageName) != null) {
                    appInfoDao.update(
                        appInfo.copy(
                            isDelete = false
                        )
                    )
                }
            }
            appInfoDao.insert(data)
            Result.success()
        } catch (e: Exception) {
            LogcatManager.saveToFile(applicationContext, e)
            Result.retry()
        }
    }
}