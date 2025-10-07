package com.bitla.ts.domain.pojo.agent_recharge.request

data class BranchRechargeRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)