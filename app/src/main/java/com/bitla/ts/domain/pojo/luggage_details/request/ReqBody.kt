package com.bitla.ts.domain.pojo.luggage_details.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    var apiKey: String?,
    @SerializedName("locale")
    var locale: String?,
    @SerializedName("pnr_number")
    var pnrNumber: String?,
    @SerializedName("luggage_detail")
    var luggageDetail: String?
)
