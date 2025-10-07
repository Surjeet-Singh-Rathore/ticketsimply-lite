package com.bitla.ts.domain.pojo.available_routes

data class BoardingPointDetail(
    val address: String,
    val id: String,
    val landmark: String,
    val name: String,
    val time: String,
    var distance: String = ""
){
    val boarding_stage_id: String?= null
    val boarding_stage_name: String?= null
    val origin: Int?= null
}