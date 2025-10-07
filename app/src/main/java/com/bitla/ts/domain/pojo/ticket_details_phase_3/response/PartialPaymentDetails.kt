package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import androidx.compose.runtime.MutableState
import com.google.gson.annotations.SerializedName

data class PartialPaymentDetails(
    @SerializedName("paid_amount")
    val paidAmount: String?,
    @SerializedName("remaining_amount")
    val remainingAmount: String?,
    @SerializedName("total_amount")
    val totalAmount: String?
)