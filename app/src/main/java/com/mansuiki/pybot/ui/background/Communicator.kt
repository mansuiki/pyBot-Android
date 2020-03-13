package com.mansuiki.pybot.ui.background

import android.util.Log
import com.mansuiki.pybot.api.ApiManager
import com.mansuiki.pybot.entity.retrofit.RequestModel
import java.util.*

class Communicator {
    companion object {
        const val TAG = "Communicator"
        private val api = ApiManager.api
    }

    private var thread: CThread? = null

    fun start() {
        thread = CThread()
        thread!!.start()
    }

    fun stop() {
        thread!!.stopc()
        thread = null
    }

    private fun Upload(reqModel: RequestModel) {
        val callSync = api.upload(reqModel)
        try {
            val response = callSync!!.execute()
            val result = response.body()
            KakaoListener.send(reqModel.room, result!!.result)
            Log.d(TAG, "Get Request")
        } catch (e: Exception) {
            Log.e(TAG, "onFailure: Failed....")
        }
        //
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

    private inner class CThread : Thread() {
        private var isRun = true
        private var manager: BotManager? = null
        fun stopc() {
            isRun = false
            manager = null
            Log.d(TAG, "CThread: Stop Thread")
        }

        override fun run() {
            isRun = true
            Log.d(TAG, "CThread : Start!")
            manager!!.resetData()
            try {
                while (isRun) {
                    val temp = transData()
                    for (request in temp.toTypedArray()) {
                        Upload(request)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "run: ", e)
            }
        }

        private fun transData(): ArrayList<RequestModel> {
            val temp = BotManager.savedMsg
            val result = ArrayList<RequestModel>()
            var RM: RequestModel
            for (data in temp.toTypedArray()) {
                RM = RequestModel(data.room, data.sender, data.message)
                result.add(RM)
                manager!!.removeSavedMsg(0, data)
            }
            return result
        }

        init {
            manager = BotManager.manager
        }
    }
}