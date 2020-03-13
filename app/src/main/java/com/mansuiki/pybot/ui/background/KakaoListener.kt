package com.mansuiki.pybot.ui.background

import android.app.Notification
import android.app.PendingIntent.CanceledException
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mansuiki.pybot.ui.background.BotManager.Companion.manager
import com.mansuiki.pybot.entity.KakaoData

class KakaoListener : NotificationListenerService() {
    companion object {
        const val TAG = "KakaoListener"
        const val pkgName = "com.kakao.talk"
        private var context: Context? = null

        fun send(room: CharSequence, message: String) {
            var session: Notification.Action? = null
            for ((room1, session1) in BotManager.room.toTypedArray()) {
                if (room1 == room) {
                    session = session1
                    break
                }
            }
            if (session == null) {
                Log.e(TAG, "Can't find [[$room]]")
                return
            }
            val sendIntent = Intent()
            val msg = Bundle()
            for (input in session.remoteInputs) msg.putCharSequence(input.resultKey, message)
            RemoteInput.addResultsToIntent(session.remoteInputs, sendIntent, msg)
            try {
                session.actionIntent.send(context, 0, sendIntent)
                Log.d(TAG, "SendMsg [[ Room : $room message : $message ]]")
            } catch (e: CanceledException) {
                e.printStackTrace()
            }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (sbn.packageName == pkgName) {
            val extender = Notification.WearableExtender(sbn.notification)
            for (act in extender.actions) {
                if (act.remoteInputs != null && act.remoteInputs.isNotEmpty()) {
                    context = applicationContext
                    if (sbn.notification.extras.getCharSequence(Notification.EXTRA_SUB_TEXT) != null) { // 개인톡방 무시
                        val data = KakaoData(
                                sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE)!!,
                                sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)!!,
                                sbn.notification.extras.getCharSequence(Notification.EXTRA_SUB_TEXT)!!,
                                act
                        )
                        manager.addData(data)
                        Log.d(TAG, "ReceiveMsg [[ Room : " + data.room + " Sender : " + data.sender + " message : " + data.message + " ]]")
                    }
                }
            }
        }
    }
}