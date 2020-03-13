package com.mansuiki.pybot.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mansuiki.pybot.ui.background.BotManager
import com.mansuiki.pybot.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkPermission()) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            finish()
        }

        binding.masterSwitch.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                Log.d("Main", "onCheckedChanged: Checked")
                BotManager.manager.start()
            } else {
                Log.d("Main", "onCheckedChanged: UnChecked")
                BotManager.manager.stop()
            }
        }

        binding.notiButton.setOnClickListener { _ ->
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }

        BotManager.manager.setContext(this)
    }

    override fun onResume() {
        super.onResume()
        if (BotManager.manager.isRunning) {
            binding.masterSwitch.isChecked = true
        }
    }

    private fun checkPermission(): Boolean {
        val list = Settings.Secure.getString(contentResolver, "enabled_notification_listeners").split(":").toTypedArray()
        val me = "com.mansuiki.pybot/com.mansuiki.pybot.ui.background.KakaoListener"
        for (next in list) {
            if (me == next) return true
        }
        return false
    }
}