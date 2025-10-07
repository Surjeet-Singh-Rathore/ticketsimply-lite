package com.bitla.ts.domain.pojo.ticket_details_phase_3.response


import com.google.gson.annotations.SerializedName

data class InsuranceTransDetails(
    @SerializedName("details")
    val details: List<Detail?>?,
    @SerializedName("partner_trans_id")
    val partnerTransId: String?
)