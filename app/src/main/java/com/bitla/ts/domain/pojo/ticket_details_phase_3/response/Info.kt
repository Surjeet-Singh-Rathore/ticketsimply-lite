package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class Info(
    @SerializedName("booking_code")
    val bookingCode: String?,
    @SerializedName("policy_number")
    val policyNumber: String?
)