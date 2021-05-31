package com.example.medicare.network

import com.example.medicare.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MedicareAPI {

    @POST("login")
    fun login(@Body user: UserCredential): Call<AuthUser>


    @GET("user")
    fun getUsers(): Call<List<User>>

    @GET("client")
    fun getClients(): Call<List<Client>>

    @GET("client/{id}")
    fun getClientsById(@Path("id") id: Int): Call<Client>


    @GET("userClients/{id}")
    fun getUserClientsById(@Path("id") id: Int): Call<List<UserClients>>


}