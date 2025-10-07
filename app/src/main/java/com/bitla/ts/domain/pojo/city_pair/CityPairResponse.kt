package com.bitla.ts.domain.pojo.city_pair

import com.google.gson.annotations.SerializedName

data class CityPairResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: MutableList<Result>
)