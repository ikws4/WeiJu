package io.ikws4.weiju.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import io.ikws4.weiju.servers.ApkServer
import io.ikws4.weiju.utilities.SPManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 启动前台服务
        // 监听应用的安装与卸载，用于更新数据库的数据
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val weiJuSP = SPManager.getInstance(context).WeiJuSP()
            if (weiJuSP.isBootCompleted) {
                val server = Intent(context, ApkServer::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(server)
                } else {
                    context.startService(server)
                }
            }
        }
    }
}
