package com.mansuiki.pybot;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class BotManager implements Observable {
    private static final String TAG = "BotManager";
    private static final BotManager manager = new BotManager();
    private static Context ctx;
    private static ArrayList<KakaoData> savedMsg = new ArrayList<>();
    private static ArrayList<RoomList> roomList = new ArrayList<>();

    private Observer observer = null;

    static BotManager getManager() {
        return manager;
    }

    void setContext(Context context) {
        ctx = context;
    }


    void start() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.startForegroundService(intent);
    }

    void restart() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.stopService(intent);
        ctx.startForegroundService(intent);
    }

    void stop() {
        Intent intent = new Intent(ctx, CommunicateService.class);
        ctx.stopService(intent);
    }

    boolean isRunning() {

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


    void addData(KakaoData data) {
        savedMsg.add(data);

        for (RoomList check : roomList.toArray(new RoomList[0])) {
            if (data.room.equals(check.room)) {
                check.session = data.session;
                if (observer != null)
                    observer.update();
                return;
            }
        }

        RoomList temp = new RoomList();
        temp.room = data.room;
        temp.session = data.session;

        roomList.add(temp);
        if (observer != null)
            observer.update();
        Log.d(TAG, "Room Added " + "Room : " + temp.room);
    }

    void resetData() {
        savedMsg.clear();
        Log.d(TAG, "Clear savedData");
    }


    ArrayList<RoomList> getRoom() {
        return roomList;
    }

    ArrayList<KakaoData> getSavedMsg() {
        return savedMsg;
    }

    void removeSavedMsg(int index, KakaoData data) {
        KakaoData temp = savedMsg.get(index);
        if ((data.room == temp.room) && (data.message == temp.message))
            savedMsg.remove(index);
        else
            Log.e(TAG, "removeSavedMsg: Not same Session");
    }

    @Override
    public void addObserver(Observer o) {
        observer = o;
    }

    @Override
    public void delObserver(Observer o) {
        observer = o;
    }

    static class KakaoData {
        String sender = "";
        CharSequence message = "";
        CharSequence room = "";

        Notification.Action session;
    }

    static class RoomList {
        CharSequence room = "";
        Notification.Action session;
    }
}
