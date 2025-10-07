package com.bitla.ts.domain.pojo.city_details.response

import com.google.gson.annotations.SerializedName

data class CityDetailsResponseModel(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: MutableList<Result>,
    @SerializedName("message")
    val message: String?=null,
)