package com.bitla.ts.domain.pojo.agent_recharge

import com.google.gson.annotations.SerializedName

data class Result(
    val message: String?,
    @SerializedName("transaction_number")
    val transactionNumber: String,
    @SerializedName("key")
    val key: String,

    )