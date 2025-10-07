package com.bitla.ts.domain.pojo.cancellation_details_model.response


import com.google.gson.annotations.SerializedName

data class CancellationDetailsResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("body")
    val result: Result

)