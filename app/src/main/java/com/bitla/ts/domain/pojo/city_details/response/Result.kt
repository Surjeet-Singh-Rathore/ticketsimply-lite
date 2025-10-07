package com.bitla.ts.domain.pojo.city_details.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)