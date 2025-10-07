package com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.response


import com.google.gson.annotations.SerializedName

data class GetTransactionPdfUrlResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("pdf_url")
    val pdfUrl: String,
    @SerializedName("message")
    val message: String,

)