package com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request

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
    @SerializedName("perticuler_brach_id")
    val perticulerBrachId: Int,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?
)