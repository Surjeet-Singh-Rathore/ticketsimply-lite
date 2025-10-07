package com.bitla.ts.domain.pojo.manage_account_view.transaction_info.response


import com.google.gson.annotations.SerializedName

data class TransactionInformationResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("pdf_url")
    val pdfUrl: String,
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("total_records")
    val totalRecords: Int,
    @SerializedName("message")
    val message: String
)