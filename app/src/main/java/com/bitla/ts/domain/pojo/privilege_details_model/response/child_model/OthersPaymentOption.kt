package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class OthersPaymentOption(
    @SerializedName("id")
    val id: Int,
    @SerializedName("label")
    val label: String
)