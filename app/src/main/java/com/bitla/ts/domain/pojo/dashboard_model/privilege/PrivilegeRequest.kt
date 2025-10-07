package com.bitla.ts.domain.pojo.dashboard_model.privilege

data class PrivilegeRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: PrivilegeReqBody
)