package com.example.medicare.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {
    companion object{
        val BASE_URL : String = "http://10.0.2.2:8000/api/"
        val gson : Gson = GsonBuilder().setLenient().create();
        val okHttpClient : OkHttpClient = OkHttpClient()
        val retrofit : Retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(BASE_URL).build()
        val medicareAPI : MedicareAPI = retrofit.create(MedicareAPI::class.java)

    }


}