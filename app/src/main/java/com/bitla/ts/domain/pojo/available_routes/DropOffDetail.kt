package com.bitla.ts.domain.pojo.available_routes

data class DropOffDetail(
    val address: String,
    val id: String,
    val landmark: String,
    val name: String,
    val time: String,
    val distance: String = ""
){
    val default_stage_id: String? =null
}