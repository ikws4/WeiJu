package io.ikws4.weiju.servers

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import io.ikws4.weiju.R
import io.ikws4.weiju.broadcasts.ApkReceiver
import io.ikws4.weiju.ui.activitys.MainActivity


class ApkServer : Service() {
    companion object {
        const val APK_SERVER_NOTIFICATION_ID = 1
        const val CHANNEL_ID = "apk foreground notification"
        const val CHANNEL_NAME = "apk listener"
    }

    private lateinit var apkReceiver: ApkReceiver

    override fun onCreate() {
        apkReceiver = ApkReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        registerReceiver(apkReceiver, intentFilter)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // TODO: 待优化

        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, mainIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_face)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_face))
                .setContentText(getString(R.string.foreground_notification))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notification = builder.build()
            startForeground(APK_SERVER_NOTIFICATION_ID, notification)
        } else {
            val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_face)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_face))
                .setContentText(getString(R.string.foreground_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notification = builder.build()
            startForeground(APK_SERVER_NOTIFICATION_ID, notification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        stopForeground(true)
        unregisterReceiver(apkReceiver)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                lightColor = getColor(R.color.colorAccent)

            }
        val server = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        server.createNotificationChannel(channel)
    }

}
