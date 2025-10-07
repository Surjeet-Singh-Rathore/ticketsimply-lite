package com.bitla.ts.domain.pojo.cancellation_details_model.response


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("cancel_percent")
    val cancelPercent: Double,
    @SerializedName("cancelled_fare")
    val cancelledFare: Double,
    @SerializedName("refund_amount")
    val refundAmount: Double
)