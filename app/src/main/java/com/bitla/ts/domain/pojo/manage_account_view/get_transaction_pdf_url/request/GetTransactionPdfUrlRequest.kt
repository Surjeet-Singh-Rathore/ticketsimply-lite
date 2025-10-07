package com.bitla.ts.domain.pojo.manage_account_view.get_transaction_pdf_url.request


data class GetTransactionPdfUrlRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)