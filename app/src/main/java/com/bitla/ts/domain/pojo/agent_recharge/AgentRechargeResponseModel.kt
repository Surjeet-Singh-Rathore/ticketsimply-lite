package com.bitla.ts.domain.pojo.agent_recharge


import com.google.gson.annotations.SerializedName

data class AgentRechargeResponseModel(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val agentRechargeResult: List<AgentRechargeResultBody>
)