package com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.response


import com.google.gson.annotations.SerializedName

data class PerSeatDiscount(
    @SerializedName("discount_value")
    val discountValue: Double?,
    @SerializedName("seat_no")
    val seatNo: String?
)