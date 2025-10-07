package com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request

import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    val apikey: String,
    @SerializedName("list_type")
    val listType: String,
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("branch_id")
    val branchId: String,
    @SerializedName("from_date")
    val fromDate: String,
    @SerializedName("to_date")
    val toDate: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("page")
    val pageNo: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("pagination")
    val pagination: Boolean,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)