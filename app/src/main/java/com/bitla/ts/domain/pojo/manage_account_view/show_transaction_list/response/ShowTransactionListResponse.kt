package com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response


import com.google.gson.annotations.SerializedName

data class ShowTransactionListResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("result")
    val result: MutableList<Result>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_records")
    val totalRecords: Int,
    @SerializedName("message")
    val message: String
)