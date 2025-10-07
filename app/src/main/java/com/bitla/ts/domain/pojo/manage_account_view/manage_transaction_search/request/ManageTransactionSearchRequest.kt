package com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.request

data class ManageTransactionSearchRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)
