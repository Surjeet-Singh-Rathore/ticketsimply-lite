package com.bitla.ts.domain.pojo.ticket_details_menu


import com.google.gson.annotations.SerializedName

data class PartialPaymentDetails(
    @SerializedName("paid_amount")
    val paidAmount: Double?,
    @SerializedName("remaining_amount")
    val remainingAmount: Double?,
    @SerializedName("total_amount")
    val totalAmount: Double?
)