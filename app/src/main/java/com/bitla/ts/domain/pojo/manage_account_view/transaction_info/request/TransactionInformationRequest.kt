package com.bitla.ts.domain.pojo.manage_account_view.transaction_info.request


data class TransactionInformationRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)