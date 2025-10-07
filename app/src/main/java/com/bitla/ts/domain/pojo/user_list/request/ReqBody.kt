package com.bitla.ts.domain.pojo.user_list.request

data class ReqBody(
    val api_key: String,
    val user_type: Int,
    val branch_id: Int? = 0,
    val locale: String?
) {
    var city_id: String = ""
}