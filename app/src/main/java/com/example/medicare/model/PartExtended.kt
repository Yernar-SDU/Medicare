package com.example.medicare.model

data class PartExtended(val id: Int, val name: String, val description: String, val service_id: String, val status: String,
                        val value: Int, val part_id: String, val count: Int, val code: String, val price: Int, val limb_id: Int) {
}