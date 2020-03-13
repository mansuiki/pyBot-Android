package com.mansuiki.pybot.ui.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mansuiki.pybot.ui.MainActivity

class CommunicateService : Service() {
    companion object {
        const val CHANNEL_ID = "pyBotCommunicateService"
        private var isStart = false
        private var communicator: Communicator? = null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // TODO 작성하자 !!!!!
        communicator = Communicator()
        communicator!!.start()
        isStart = true
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_MIN)
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("pyBot")
                .setContentText("pyBot is Running")
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        isStart = false
        communicator!!.stop()
        communicator = null
    }

    override fun onBind(intent: Intent): IBinder? { //Nothing
        return null
    }
}