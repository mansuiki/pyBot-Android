package com.mansuiki.pybot;

import android.util.Log;

import com.mansuiki.pybot.BotManager.KakaoData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class Communicator implements Observer {
    private static final String TAG = "Communicator";
    private static final String BASE_URL = "http://mansuiki.com:37280";
    private static Retrofit retrofit = null;
    private static RetrofitInterface service = null;
    private boolean isRun = false;


    Communicator() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(RetrofitInterface.class);
    }

    void start() {
        BotManager.getManager().addObserver(this);
        BotManager.getManager().resetData();
        isRun = true;
    }

    void stop() {
        BotManager.getManager().delObserver(this);
        isRun = false;
    }

    private void Upload(final KakaoData reqModel) {
        service.upload(reqModel).enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                ResultModel result = response.body();
                if (result.getResult().equals("BOTONPYBOT")) {
                    isRun = true;
                    KakaoListener.send(reqModel.room, "BOTON");
                } else if (result.getResult().equals("BOTOFFPYBOT")) {
                    isRun = false;
                    KakaoListener.send(reqModel.room, "BOTOFF");
                } else if (isRun) {
                    KakaoListener.send(reqModel.room, result.getResult());
                }
                Log.d(TAG, "Get Request");
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed....", t);
            }
        });
    }

    @Override
    public void update() {
        for (KakaoData data : BotManager.getManager().getSavedMsg().toArray(new KakaoData[0])) {
            BotManager.getManager().removeSavedMsg(0, data);
            if (data.message.charAt(0) == '/')
                Upload(data);
        }
    }


    interface RetrofitInterface {

        @POST("/bot")
        Call<ResultModel> upload(@Body KakaoData kakaoData);
    }

    class ResultModel {

        String Result;

        ResultModel(String Result) {
            this.Result = Result;
        }

        String getResult() {

            return Result;
        }

    }
}
