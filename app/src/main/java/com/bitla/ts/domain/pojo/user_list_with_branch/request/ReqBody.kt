package com.bitla.ts.domain.pojo.user_list_with_branch.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    val api_key: String,
    val user_type: Int,
    val branch_id: Int,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)