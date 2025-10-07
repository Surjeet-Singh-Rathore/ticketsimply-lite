package com.bitla.ts.domain.pojo.update_rate_card.fetch_fare_template.response

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
)