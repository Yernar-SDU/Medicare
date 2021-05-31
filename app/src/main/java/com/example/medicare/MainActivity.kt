package com.example.medicare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.medicare.model.Client
import com.example.medicare.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val medicareAPI = NetworkService().medicareAPI

        medicareAPI.getUsers().enqueue(object: Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                Log.i("retrofit", "onResponse: " + response.message())
                response.body()
            }

            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                Log.i("retrofit", "onFailure: " + t.message)

            }

        })

    }
//    fun initializeSSLContext(mContext: Context) {
//        try {
//            SSLContext.getInstance("TLSv1.2")
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        }
//
//        try {
//            ProviderInstaller.installIfNeeded(mContext.applicationContext)
//        } catch (e: GooglePlayServicesRepairableException) {
//            e.printStackTrace()
//        } catch (e: GooglePlayServicesNotAvailableException) {
//            e.printStackTrace()
//        }
//
//    }
}