package com.bitla.ts.domain.pojo.service_details_response

import com.google.gson.annotations.SerializedName

data class DiscountConfiguration(

    @SerializedName("branch_role_discount_type")
    val branchRoleDiscountType: String,

    @SerializedName("discount_type")
    val discountType: String,

    @SerializedName("discount_value")
    val discountValue: Any?
)