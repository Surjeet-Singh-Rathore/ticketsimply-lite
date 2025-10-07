package com.bitla.ts.domain.pojo.extend_fare.response

import com.google.gson.annotations.SerializedName

data class ExtendFareResponse(
    @SerializedName("code")
    var code: Int?,
    @SerializedName("message")
    var message: String?
)