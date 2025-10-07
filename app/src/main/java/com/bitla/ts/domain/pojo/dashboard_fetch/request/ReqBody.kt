package com.bitla.ts.domain.pojo.dashboard_fetch.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("is_all_service")
    val isAllService: Boolean,
    @SerializedName("order_by")
    val orderBy: OrderBy,
    @SerializedName("service_id")
    val serviceId: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean,
    @SerializedName("locale")
    val locale: String
)