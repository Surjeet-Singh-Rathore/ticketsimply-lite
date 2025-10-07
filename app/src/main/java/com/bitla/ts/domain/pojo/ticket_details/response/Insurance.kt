package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Insurance(
    @SerializedName("partner_trans_id")
    val partnerTransId: Boolean = false,
)