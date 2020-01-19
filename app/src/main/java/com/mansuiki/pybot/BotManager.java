package com.mansuiki.pybot;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class BotManager {
    private static final String TAG = "BotManager";
    private static final BotManager manager = new BotManager();
    public static Context ctx;
    private static ArrayList<KakaoData> savedMsg = new ArrayList<>();
    private static ArrayList<RoomList> roomList = new ArrayList<>();

    public static BotManager getManager() {
        return manager;
    }

    public void setContext(Context context) {
        ctx = context;
    }


    public void start() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.startForegroundService(intent);
    }

    public void restart() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.stopService(intent);
        ctx.startForegroundService(intent);
    }

    public void stop() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.stopService(intent);
    }

    public boolean isRunning() {

        try {
            ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (CommunicateService.class.getName().equals(serviceInfo.service.getClassName())) {
                    return true;
                }
            }

        } catch (Throwable e) {
            Log.d(TAG, "isRunning: Error Occurred");
        }
        return false;
    }


    public void addData(KakaoData data) {
        savedMsg.add(data);

        for (RoomList check : roomList.toArray(new RoomList[0])) {
            if (data.room.equals(check.room)) {
                check.session = data.session;
                return;
            }
        }

        RoomList temp = new RoomList();
        temp.room = data.room;
        temp.session = data.session;

        roomList.add(temp);

        Log.d(TAG, "Room Added " + "Room : " + temp.room);
    }

    public void resetData() {
        savedMsg.clear();
        Log.d(TAG, "Clear savedData");
    }


    public ArrayList<RoomList> getRoom() {
        return roomList;
    }

    public ArrayList<KakaoData> getSavedMsg() {
        return savedMsg;
    }

    public void removeSavedMsg(int index, KakaoData data) {
        KakaoData temp = savedMsg.get(index);
        if ((data.room == temp.room) && (data.message == temp.message))
            savedMsg.remove(index);
        else
            Log.e(TAG, "removeSavedMsg: Not same Session");
    }


    public static class KakaoData {
        String sender = "";
        CharSequence message = "";
        CharSequence room = "";

        Notification.Action session;
    }

    public static class RoomList {
        CharSequence room = "";
        Notification.Action session;
    }
}
