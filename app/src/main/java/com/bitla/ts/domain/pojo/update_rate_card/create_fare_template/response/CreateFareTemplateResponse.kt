package com.bitla.ts.domain.pojo.update_rate_card.create_fare_template.response


import com.google.gson.annotations.SerializedName

data class CreateFareTemplateResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?
)