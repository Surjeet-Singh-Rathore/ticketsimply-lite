package com.bitla.ts.domain.pojo.service_routes_list.response


import com.google.gson.annotations.SerializedName

data class ServiceRoutesListResponse(
    @SerializedName("code")
    val code: Int,
    val message: String,
    val error: String,
    @SerializedName("result")
    val result: MutableList<Result>
)