package com.example.medicare.model

data class Part(val id: Int, val name: String, val code: String, val description: String,
                val image: String,val status: String, val value: String, val price: Int, val categories_id: Int,
                val sub_categories_id: Int, val limb_id: Int, var count: Int){

}
