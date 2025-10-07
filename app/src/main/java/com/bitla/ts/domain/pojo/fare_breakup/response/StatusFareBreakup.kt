package com.bitla.ts.domain.pojo.fare_breakup.response


import com.google.gson.annotations.SerializedName

data class StatusFareBreakup(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)