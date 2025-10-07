package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class WalletPaymentOption(
    @SerializedName("name")
    val name: String,
    @SerializedName("paygay_type")
    val paygayType: Int,
    @SerializedName("type")
    val type: String
)