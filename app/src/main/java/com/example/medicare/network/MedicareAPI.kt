package com.example.medicare.network

import com.example.medicare.model.Client
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface MedicareAPI {

    @GET("user")
    fun getUsers(): Call<List<Client>>

    @POST("login")
    fun login()


}