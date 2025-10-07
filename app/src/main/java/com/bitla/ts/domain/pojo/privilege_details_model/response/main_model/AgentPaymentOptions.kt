package com.bitla.ts.domain.pojo.privilege_details_model.response.main_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class AgentPaymentOptions {

    @SerializedName("PAY_FROM_WALLET")
    @Expose
    var payFromWallet: Int? = null

    @SerializedName("PAY_NET_AMOUNT")
    @Expose
    var payNetAmount: Int? = null

    @SerializedName("PAY_FULL_AMOUNT")
    @Expose
    var payFullAmount: Int? = null
}