package com.example.medicare.model

data class Client(val id: Int, val name: String, val email : String,
                  val phoneNumber: String, val status: String, val avatar: String, val address: String,
                  val IIN: String, val email_verified_at: String, val password: String, val remember_token: String,
                  val created_at: String, val updated_at: String)
