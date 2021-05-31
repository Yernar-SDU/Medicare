package com.example.medicare.model

import java.io.Serializable

data class User(val id: Int, val name: String, val email: String, val phone: String,
                val status: String, val avatar: String, val updated_at: String, val created_at: String,
                val address: String, val IIN: String, val token: String) : Serializable{
}
