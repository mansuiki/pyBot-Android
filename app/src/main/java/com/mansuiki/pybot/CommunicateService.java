package com.mansuiki.pybot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class CommunicateService extends Service {
    public static final String CHANNEL_ID = "pyBotCommunicateService";

    private static boolean isStart = false;
    private static Communicator communicator;

    public static Communicator getCommunicator() {
        return communicator;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //TODO 작성하자 !!!!!
        communicator = new Communicator();
        communicator.start();
        isStart = true;


        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_MIN);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("pyBot")
                .setContentText("pyBot is Running")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isStart = false;
        communicator.stop();
        communicator = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Nothing
        return null;
    }

}
