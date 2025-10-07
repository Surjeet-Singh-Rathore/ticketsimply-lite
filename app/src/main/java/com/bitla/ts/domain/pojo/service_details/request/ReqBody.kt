package com.bitla.ts.domain.pojo.service_details.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
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
    @SerializedName("is_from_middle_tier")
    val is_from_middle_tier: Boolean = true,
    val app_bima_enabled: Boolean?=null
)