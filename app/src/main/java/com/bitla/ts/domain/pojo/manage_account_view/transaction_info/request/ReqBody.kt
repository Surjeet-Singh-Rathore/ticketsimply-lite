package com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apikey: String,
    @SerializedName("transaction_no")
    val transactionNo: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String
)