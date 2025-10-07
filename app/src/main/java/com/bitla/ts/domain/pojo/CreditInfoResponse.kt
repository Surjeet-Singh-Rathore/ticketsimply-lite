package com.bitla.ts.domain.pojo

import com.google.gson.annotations.SerializedName

data class CreditInfoResponse(
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("total_credit")
    var totalCredit: String? = "",

    @SerializedName("available_credit")
    var availableCredit: String? = "",

    @SerializedName("available_balance")
    var availableBalance: String? = "",

    @SerializedName("message")
    var message: String? = "",

    @SerializedName("last_updated_on")
    var lastUpdatedOn: String? = "",
)