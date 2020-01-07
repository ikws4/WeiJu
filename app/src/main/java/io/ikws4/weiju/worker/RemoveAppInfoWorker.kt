package io.ikws4.weiju.worker

import android.content.Context
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.utilities.LogcatManager
import kotlinx.coroutines.coroutineScope

class RemoveAppInfoWorker(
    context: Context,
    workerParameters: WorkerParameters
) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val pkgName = inputData.getString("pkgName")!!
            val appInfoDao = AppDatabase.getInstance(applicationContext).appInfoDao()
            appInfoDao.remove(pkgName)
            val sharedPreferences = applicationContext.getSharedPreferences(pkgName, Context.MODE_PRIVATE)
            sharedPreferences.edit {
                clear()
            }

            Result.success()
        } catch (ex: Exception) {
            LogcatManager.saveToFile(applicationContext, ex)
            Result.failure()
        }
    }

    companion object {
        const val TAG = "RemoveAppInfoWorker"
    }

}