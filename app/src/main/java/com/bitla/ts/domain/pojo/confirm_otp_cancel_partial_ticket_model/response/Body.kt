package com.bitla.ts.domain.pojo.confirm_otp_cancel_partial_ticket_model.response


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("cancellation_charges")
    val cancellationCharges: Int,
    @SerializedName("refund_amount")
    val refundAmount: Int,
    @SerializedName("seat_numbers")
    val seatNumbers: String,
    @SerializedName("total_fare")
    val totalFare: Int
)