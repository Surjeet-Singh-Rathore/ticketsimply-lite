package com.bitla.ts.domain.pojo.agent_recharge.request


import com.google.gson.annotations.SerializedName

data class AgentRechargeRequest(
    @SerializedName("bcc_id")
    val bccId: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("method_name")
    val methodName: String,
    @SerializedName("req_body")
    val agentReqBody: AgentReqBody
)