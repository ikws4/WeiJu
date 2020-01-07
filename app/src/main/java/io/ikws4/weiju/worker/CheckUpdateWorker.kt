package io.ikws4.weiju.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ikws4.weiju.BuildConfig
import io.ikws4.weiju.utilities.LogcatManager
import io.ikws4.weiju.utilities.SPManager
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * 在后台检查软件更新
 * @constructor
 */
class CheckUpdateWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.github.com/repos/ikws4/WeiJu/releases/latest")
                .build()
            val response = client.newCall(request).execute()
            val result = JSONObject(response.body!!.string())

            val versionName = result.getString("tag_name")
            val spManager = SPManager.getInstance(applicationContext)
            if (BuildConfig.VERSION_NAME < versionName) {
                val assets = result.getJSONArray("assets")
                val downloadUrl = assets.getJSONObject(0).getString("browser_download_url")
                with(spManager.WeiJuSP()) {
                    newVersionDownloadUrl = downloadUrl
                    this.versionName = versionName
                }
                spManager.WeiJuSP().newVersionDownloadUrl = downloadUrl
            } else {
                spManager.WeiJuSP().versionName = BuildConfig.VERSION_NAME
            }

            Result.success()
        } catch (ex: Exception) {
            LogcatManager.saveToFile(applicationContext, ex)
            Result.failure()
        }
    }

    companion object {
        const val TAG = "CheckUpdateWorker"
    }
}