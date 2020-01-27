package com.mansuiki.pybot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
            finish();
        }

        Switch masterSwitch = findViewById(R.id.masterSwitch);
        masterSwitch.setOnCheckedChangeListener(new switchListener());

        Button notiButton = findViewById(R.id.notiButton);
        notiButton.setOnClickListener(new buttonListener());

        BotManager.getManager().setContext(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (BotManager.getManager().isRunning()) {
            Switch masterSwitch = findViewById(R.id.masterSwitch);
            masterSwitch.setChecked(true);
        }
    }


    private boolean checkPermission() {
        String[] list = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners").split(":");
        String me = "com.mansuiki.pybot/com.mansuiki.pybot.KakaoListener";
        for (String next : list) {
            if (me.equals(next)) return true;
        }
        return false;
    }

    class buttonListener implements CompoundButton.OnClickListener {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.notiButton:
                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                    break;
            }
        }
    }

    class switchListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Log.d("Main", "onCheckedChanged: Checked");
                BotManager.getManager().start();
            } else {
                Log.d("Main", "onCheckedChanged: UnChecked");
                BotManager.getManager().stop();
            }

        }
    }
}
