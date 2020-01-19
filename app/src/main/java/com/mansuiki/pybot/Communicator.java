package com.mansuiki.pybot;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class Communicator {
    public static final String TAG = "Communicator";
    private static final String BASE_URL = "http://mansuiki.com:37280";
    private static Retrofit retrofit = null;
    private static RetrofitInterface service = null;
    private CThread thread = null;


    public Communicator() {

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(RetrofitInterface.class);
    }

    public void start() {

        thread = new CThread();
        thread.start();
    }

    public void stop() {

        thread.stopc();
        thread = null;
    }

    private void Upload(final RequestModel reqModel) {

        Call<ResultModel> callSync = service.upload(reqModel);
        try {
            Response<ResultModel> response = callSync.execute();
            ResultModel result = response.body();
            KakaoListener.send(reqModel.getRoom(), result.getResult());
            Log.d(TAG, "Get Request");
        } catch (Exception e) {
            Log.e(TAG, "onFailure: Failed....");
        }

/*
        service.upload(reqModel).enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                ResultModel result = response.body();
                KakaoListener.send(reqModel.getRoom(), result.getResult());
                Log.d(TAG, "Get Request");
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed....");
            }
        });*/
    }


    public interface RetrofitInterface {

        @POST("/bot")
        Call<ResultModel> upload(@Body RequestModel reqModel);
    }


    private class CThread extends Thread {

        private boolean isRun;
        private BotManager manager = null;

        public CThread() {

            isRun = true;
            manager = BotManager.getManager();
        }

        public void stopc() {

            isRun = false;
            manager = null;
            Log.d(TAG, "CThread: Stop Thread");
        }

        public void run() {

            isRun = true;
            Log.d(TAG, "CThread : Start!");
            manager.resetData();
            try {
                while (isRun) {
                    ArrayList<RequestModel> temp = transData();
                    for (RequestModel request : temp.toArray(new RequestModel[0])) {
                        Upload(request);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "run: ", e);
            }
        }

        private ArrayList<RequestModel> transData() {
            ArrayList<BotManager.KakaoData> temp = manager.getSavedMsg();
            ArrayList<RequestModel> result = new ArrayList<>();
            RequestModel RM;
            for (BotManager.KakaoData data : temp.toArray(new BotManager.KakaoData[0])) {
                RM = new RequestModel(data.room, data.sender, data.message);
                result.add(RM);
                manager.removeSavedMsg(0, data);
            }

            return result;
        }
    }


    public class RequestModel {

        private CharSequence Room;
        private CharSequence Sender;
        private CharSequence Message;

        public RequestModel(CharSequence Room, CharSequence Sender, CharSequence Message) {
            this.Room = Room;
            this.Sender = Sender;
            this.Message = Message;
        }

        public CharSequence getRoom() {

            return Room;
        }

        public CharSequence getSender() {

            return Sender;
        }

        public CharSequence getMessage() {

            return Message;
        }
    }

    public class ResultModel {

        private String Result;

        public ResultModel(String Result, String Checker) {
            this.Result = Result;
        }

        public String getResult() {

            return Result;
        }

    }
}
