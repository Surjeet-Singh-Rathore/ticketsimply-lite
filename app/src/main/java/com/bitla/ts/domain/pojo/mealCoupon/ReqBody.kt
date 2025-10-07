package com.bitla.ts.domain.pojo.mealCoupon

data class ReqBody(
    val api_key: String,
    val seat_number: String,
    val pnr_number: String,
    val coupon_code: String
)
