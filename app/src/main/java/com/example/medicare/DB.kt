package com.example.medicare

import android.content.SharedPreferences
import com.example.medicare.model.User
import com.google.gson.Gson
import kotlin.math.log

class DB {
    companion object {
        var sharedPreferences: SharedPreferences? = null
        var loggedIn: Boolean = false
        var user: User? = null

        fun get_LoggedIn(): Boolean{
            return sharedPreferences!!.getBoolean(KEYS.LOGGED_IN,false)
        }
        fun set_LoggedIn(loggedIn: Boolean){
            sharedPreferences!!.edit().putBoolean(KEYS.LOGGED_IN,loggedIn).apply()
        }

        fun get_user(): User {
            return Gson().fromJson(sharedPreferences!!.getString(KEYS.USER,null),User::class.java)
        }
        fun set_user(user: User){
            sharedPreferences!!.edit().putString(KEYS.USER,Gson().toJson(user)).apply()
        }

    }

}