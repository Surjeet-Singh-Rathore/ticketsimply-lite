package com.example.buscoach.service_details_response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DiscountConfiguration(

    @SerializedName("branch_role_discount_type")
    val branchRoleDiscountType: String,

    @SerializedName("discount_type")
    val discountType: String,

    @SerializedName("discount_value")
    val discountValue: Any?
): Serializable