package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class RefundType(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("label")
    val label: String?
)