package com.mansuiki.pybot.entity

import android.app.Notification

data class KakaoData (
        val sender: CharSequence,
        val message: CharSequence = "",
        val room: CharSequence = "",
        val session: Notification.Action?
)