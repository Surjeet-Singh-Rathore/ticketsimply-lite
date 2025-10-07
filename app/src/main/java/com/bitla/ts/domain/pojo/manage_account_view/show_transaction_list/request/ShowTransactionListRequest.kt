package com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.request

data class ShowTransactionListRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)