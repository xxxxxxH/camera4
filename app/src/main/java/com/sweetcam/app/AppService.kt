package com.sweetcam.app

import okhttp3.ResponseBody
import retrofit2.http.*

interface AppService {

    @POST("config")
    suspend fun getConfig(): ResponseBody?

    @POST
    suspend fun uploadFbData(
        @Url url: String,
        @Body body: Map<String,String>,
    ): com.sweetcam.app.pojo.ResultPojo?
}