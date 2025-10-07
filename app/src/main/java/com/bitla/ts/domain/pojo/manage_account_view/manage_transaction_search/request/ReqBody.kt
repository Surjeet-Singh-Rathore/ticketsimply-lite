package com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apikey: String,
    @SerializedName("search")
    val search: String,
    @SerializedName("list_type")
    val listType: String,
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("branch_id")
    val branchId: String,
    @SerializedName("from_date")
    val category: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)