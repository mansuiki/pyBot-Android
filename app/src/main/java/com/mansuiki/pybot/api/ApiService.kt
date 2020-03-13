package com.mansuiki.pybot.api

import com.mansuiki.pybot.entity.retrofit.RequestModel
import com.mansuiki.pybot.entity.retrofit.ResultModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/bot")
    fun upload(@Body reqModel: RequestModel?): Call<ResultModel?>?
}