package com.bitla.ts.domain.pojo.destination_list

import com.bitla.ts.domain.pojo.available_routes.DropOffDetail

data class City(
    val dropping_point: List<DropOffDetail>,
    val id: String,
    val name: String
){
    var isSelectedCity : Boolean = false
}