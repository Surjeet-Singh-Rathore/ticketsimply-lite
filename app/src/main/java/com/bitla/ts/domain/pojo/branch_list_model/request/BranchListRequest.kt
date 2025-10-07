package com.bitla.ts.domain.pojo.branch_list_model.request

data class BranchListRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)