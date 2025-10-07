package com.bitla.ts.domain.pojo.manage_account_view.update_account_status.request

import com.google.gson.annotations.SerializedName


data class UpdateAccountStatusRequest(

    @SerializedName("api_key")
    val apikey: String,
    @SerializedName("transaction_no")
    val transactionNo: String,
    @SerializedName("Details")
    val details: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)