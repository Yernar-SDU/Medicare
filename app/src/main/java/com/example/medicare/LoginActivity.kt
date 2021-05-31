package com.example.medicare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.medicare.model.AuthUser
import com.example.medicare.model.UserCredential
import com.example.medicare.network.MedicareAPI
import com.example.medicare.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var authUser : AuthUser
    private lateinit var medicareAPI: MedicareAPI
    lateinit var email: EditText
    lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        email = findViewById<EditText>(R.id.email)
        password = findViewById<EditText>(R.id.password)
        DB.sharedPreferences = getPreferences(MODE_PRIVATE)
        Log.i("TAG", "onCreate: ${DB.get_LoggedIn()}")
        if (DB.get_LoggedIn()){
//            val intent: Intent = Intent(this@LoginActivity,MainActivity::class.java)
//            intent.putExtra(KEYS.USER,DB.get_user())
//            startActivity(intent)
        }
    }


    private fun getCredential():UserCredential{
        return UserCredential(email.text.toString().lowercase().trim(), password.text.toString().trim())
    }

    fun signIn(view: View) {
        medicareAPI= NetworkService.medicareAPI
        medicareAPI.login(getCredential()).enqueue(
            object : Callback<AuthUser> {
                override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {
                    Log.i("retrofit", "onResponse: ${response.body()}")
                    Log.i("retrofit", "onResponse: " + response.message())

                    if(response.isSuccessful){
                        authUser = response.body()!!
                        if (authUser.success){
                            val intent: Intent = Intent(this@LoginActivity,MainActivity::class.java)
                            intent.putExtra(KEYS.USER,authUser.user)
                            DB.set_user(authUser.user)
                            startActivity(intent)
                        }
                    }
                }

                override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                    Log.i("retrofit", "onFailure: " + t.message)
                }
            })
    }


}

