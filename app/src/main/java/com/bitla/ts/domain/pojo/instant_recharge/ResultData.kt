package com.bitla.ts.domain.pojo.instant_recharge

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResultData (
    @SerializedName("transaction_charge")
    val transactionCharge: Double? = null,
    @SerializedName("net_amount")
    val netAmount: Double? = null,

)