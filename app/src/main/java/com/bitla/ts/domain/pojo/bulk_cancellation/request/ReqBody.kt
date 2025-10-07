package com.bitla.ts.domain.pojo.bulk_cancellation.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    var auth_pin: String,
    val bulk_cancel_params: List<BulkCancelParam>,
    val cancel_percent: Float,
    val is_sms_send: Boolean,
    val remarks: String,
    val res_id: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)