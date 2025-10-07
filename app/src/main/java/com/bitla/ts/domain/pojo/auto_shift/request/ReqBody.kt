package com.bitla.ts.domain.pojo.auto_shift.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val auto_macth_by: String,
    val new_res_id: String,
    val old_res_id: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)