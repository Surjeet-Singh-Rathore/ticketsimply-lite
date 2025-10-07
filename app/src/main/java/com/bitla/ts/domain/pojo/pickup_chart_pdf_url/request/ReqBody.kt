package com.bitla.ts.domain.pojo.pickup_chart_pdf_url.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val res_id: String,
    val travel_date: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    val locale: String?,
    val audit_type: String
)