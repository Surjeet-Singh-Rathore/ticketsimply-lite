package com.bitla.ts.domain.pojo.dashboard_model.response


import com.google.gson.annotations.SerializedName

data class Collections(
    @SerializedName("agent_collection")
    val agentCollection: Double?,
    @SerializedName("bima")
    val bima: Double?,
    @SerializedName("card")
    val card: Double?,
    @SerializedName("cash")
    val cash: Double?,
    @SerializedName("others")
    val others: Double?,
    @SerializedName("wallet")
    val wallet: Double?
)