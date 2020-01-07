package io.ikws4.weiju.worker

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.data.User
import io.ikws4.weiju.utilities.LogcatManager
import kotlinx.coroutines.coroutineScope
import saveToFile

/**
 * 创建Database时，执行该任务
 * @constructor
 */
class SeedAppDatabaseWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val data = arrayListOf<AppInfo>()
            val pm = applicationContext.packageManager
            val appDatabase = AppDatabase.getInstance(applicationContext)
            // 初始化用户数据
            appDatabase.userDao().insert(User((0)))

            pm.getInstalledPackages(0).forEach {
                val path = "${applicationContext.filesDir.path}/application_icons/"
                val fileName = "${it.packageName}.png"
                val fullPath = path + fileName

                data.add(
                    AppInfo(
                        pkgName = it.packageName,
                        name = it.applicationInfo.loadLabel(pm).toString(),
                        iconPath = fullPath,
                        isSystemApp = (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    )
                )
                // 把图标保存到 内置的files/application_icon文件夹下
                it.applicationInfo.loadIcon(pm).saveToFile(fileName, path)
            }
            appDatabase.appInfoDao().insert(data)
            Result.success()
        } catch (ex: Exception) {
            LogcatManager.saveToFile(applicationContext, ex)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SeedAppDatabaseWorker"
    }
}