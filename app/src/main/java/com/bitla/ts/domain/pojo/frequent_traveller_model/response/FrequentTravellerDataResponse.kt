package com.bitla.ts.domain.pojo.frequent_traveller_model.response


import com.google.gson.annotations.SerializedName

data class FrequentTravellerDataResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: MutableList<Result>
)