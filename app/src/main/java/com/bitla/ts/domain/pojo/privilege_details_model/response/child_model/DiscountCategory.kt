package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class DiscountCategory(
    @SerializedName("category")
    val category: String,
    @SerializedName("discount_from")
    val discountFrom: String,
    @SerializedName("discount_to")
    val discountTo: String,
    @SerializedName("id")
    val id: Int
)