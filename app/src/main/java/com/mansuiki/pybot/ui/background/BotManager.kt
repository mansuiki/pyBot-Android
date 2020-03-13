package com.mansuiki.pybot.ui.background

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mansuiki.pybot.entity.KakaoData
import com.mansuiki.pybot.entity.RoomList
import java.util.*

class BotManager {
    companion object {
        private const val TAG = "BotManager"
        @JvmStatic
        val manager = BotManager()
        private var ctx: Context? = null

        @JvmField
        val savedMsg = ArrayList<KakaoData>()
        @JvmField
        val room = ArrayList<RoomList>()
    }

    val isRunning: Boolean
        get() {
            try {
                val activityManager = ctx!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                for (serviceInfo in activityManager.getRunningServices(Int.MAX_VALUE)) {
                    if (CommunicateService::class.java.name == serviceInfo.service.className) {
                        return true
                    }
                }
            } catch (e: Throwable) {
                Log.d(TAG, "isRunning: Error Occurred")
            }
            return false
        }

    fun setContext(context: Context?) {
        ctx = context
    }

    fun start() {
        val intent = Intent(ctx, CommunicateService::class.java)
        ctx!!.startForegroundService(intent)
    }

    fun restart() {
        val intent = Intent(ctx, CommunicateService::class.java)
        ctx!!.stopService(intent)
        ctx!!.startForegroundService(intent)
    }

    fun stop() {
        val intent = Intent(ctx, CommunicateService::class.java)
        ctx!!.stopService(intent)
    }

    fun addData(data: KakaoData) {
        savedMsg.add(data)
        for ((room1) in room.toTypedArray()) {
            if (data.room == room1) {
                // check.session = data.getSession();
                return
            }
        }
        val temp = RoomList(data.room, data.session)
        room.add(temp)
        Log.d(TAG, "Room Added " + "Room : " + temp.room)
    }

    fun resetData() {
        savedMsg.clear()
        Log.d(TAG, "Clear savedData")
    }

    fun removeSavedMsg(index: Int, data: KakaoData) {
        val (_, message, room1) = savedMsg[index]
        if (data.room == room1 && data.message == message)
            savedMsg.removeAt(index)
        else
            Log.e(TAG, "removeSavedMsg: Not same Session")
    }
}