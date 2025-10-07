package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model

import com.google.gson.annotations.SerializedName

data class RechargeTypes (
    @SerializedName("credit_card")
    var creditCard: Boolean = false,
    @SerializedName("bank_deposit")
    var bankDeposit: Boolean = false,
    @SerializedName("fund_transfer")
    var fundTransfer: Boolean = false,
    @SerializedName("instant_recharge")
    var instantRecharge: Boolean = false,

)