package com.example.medicare.model

data class ClientProducts(val client_id: Int, val product_id: Int,
                          val name: String, val description: String,
                          val service_id: Int, val status: String,
                          val value: Int, val image: String)