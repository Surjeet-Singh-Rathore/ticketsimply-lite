package com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.response


import com.google.gson.annotations.SerializedName

data class CampaignsAndPromotionsDiscountResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("discount_value")
    val discountValue: Double?,
    @SerializedName("per_seat_discount")
    val perSeatDiscount: List<PerSeatDiscount?>?,
    @SerializedName("message")
    val message: String?
)