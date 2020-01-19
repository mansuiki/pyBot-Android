package com.mansuiki.pybot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.mansuiki.pybot.BotManager.KakaoData;
import com.mansuiki.pybot.BotManager.RoomList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KakaoListener extends NotificationListenerService {

    private final static String TAG = "KakaoListener";
    private final static String pkgName = "com.kakao.talk";

    private static Context context;

    public KakaoListener() {
        // Nothing ....
    }

    public static void send(CharSequence room, CharSequence message) {
        Notification.Action session = null;

        for (RoomList temp : BotManager.getManager().getRoom().toArray(new RoomList[0])) {
            if (temp.room.equals(room)) {
                session = temp.session;
                break;
            }
        }

        if (session == null) {
            Log.e(TAG, "Can't find [[" + room + "]]");
            return;
        }

        Intent sendIntent = new Intent();
        Bundle msg = new Bundle();
        for (RemoteInput input : session.getRemoteInputs())
            msg.putCharSequence(input.getResultKey(), message);
        RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);

        try {
            session.actionIntent.send(context, 0, sendIntent);
            Log.d(TAG, "SendMsg [[ Room : " + room + " message : " + message + " ]]");
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if (sbn.getPackageName().equals(pkgName)) {
            Notification.WearableExtender extender = new Notification.WearableExtender(sbn.getNotification());

            for (Notification.Action act : extender.getActions()) {
                if ((act.getRemoteInputs() != null) && act.getRemoteInputs().length > 0) {
                    context = getApplicationContext();
                    if (sbn.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT) != null) { // 개인톡방 무시
                        KakaoData data = new KakaoData();

                        data.sender = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
                        data.message = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
                        data.room = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
                        data.session = act;


                        BotManager.getManager().addData(data);

                        Log.d(TAG, "ReceiveMsg [[ Room : " + data.room + " Sender : " + data.sender + " message : " + data.message + " ]]");
                    }
                }
            }
        }
    }
}

