package com.thethirdeye.esport.assessment

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ResumeApi {

    @GET("resume")
    fun getResume(
        @Query("name") name: String
    ): Call<ResumeModel>
}