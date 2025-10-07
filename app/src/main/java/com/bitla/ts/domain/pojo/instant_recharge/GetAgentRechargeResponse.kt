package com.bitla.ts.domain.pojo.instant_recharge

import com.google.gson.annotations.SerializedName

data class GetAgentRechargeResponse(
    @SerializedName("result")
    val result: ResultData? = null,
    @SerializedName("code")
    val code: Int? = null,

)