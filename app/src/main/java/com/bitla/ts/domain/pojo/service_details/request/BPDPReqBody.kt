package com.bitla.ts.domain.pojo.service_details.request

import com.google.gson.annotations.SerializedName

data class BPDPReqBody(
    @SerializedName("id")
    val id: String,
    @SerializedName("api_key")
    val api_key: String,
    @SerializedName("operator_api_key")
    val operator_api_key: String,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("origin_id")
    val origin_id: String,
    @SerializedName("destination_id")
    val destination_id: String,
    @SerializedName("json_format")
    val json_format: String,
    @SerializedName("boarding_at")
    val boarding_at: String,
    @SerializedName("drop_off")
    val drop_off: String,
    @SerializedName("is_from_middle_tier")
    val is_from_middle_tier: Boolean= true,
    @SerializedName("remarks")
    val remarks: String?= null
)