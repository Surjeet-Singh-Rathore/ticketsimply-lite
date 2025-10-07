package com.bitla.ts.domain.pojo

import com.google.gson.annotations.SerializedName

data class PhonePeV2StatusResponse(
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("order_id")
    var orderId: String = "",

    @SerializedName("status")
    var status: String = "",
)