package com.mansuiki.pybot.entity

import android.app.Notification

data class RoomList (
    val room: CharSequence,
    val session: Notification.Action?
)