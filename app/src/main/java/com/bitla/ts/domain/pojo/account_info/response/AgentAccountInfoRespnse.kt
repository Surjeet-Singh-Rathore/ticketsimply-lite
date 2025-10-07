package com.bitla.ts.domain.pojo.account_info.response

import com.google.gson.annotations.SerializedName

data class AgentAccountInfoRespnse(
    @SerializedName("available_balance")
    val available_balance: String = "",
    @SerializedName("code")
    val code: Int,
    @SerializedName("credit_limit")
    val credit_limit: String= "",
    @SerializedName("last_recharge_amount")
    val last_recharge_amount: String = "",
    @SerializedName("last_recharged_on")
    val last_recharged_on: String = "",
    @SerializedName("commission_balance")
    val commission_balance: String = "",
    @SerializedName("notify_limit")
    val notify_limit: String = "",
    @SerializedName("balance_amount")
    val balance_amount: String = "",
    val result: Result
)
data class Result(
    val message: String?=null
)