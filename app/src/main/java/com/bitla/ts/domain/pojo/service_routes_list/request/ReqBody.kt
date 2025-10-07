package com.bitla.ts.domain.pojo.service_routes_list.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val operator_api_key: String,
    val show_injourney_services: String,
    val show_only_available_services: String,
    val travel_date: String,
    val response_format: String,
    val is_cs_shared: Boolean?,
    val locale: String?,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
)