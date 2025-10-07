package com.bitla.ts.domain.pojo.getCouponDiscount.Response

data class PerBookingCoupon(
    val coupon_name: String,
    val coupon_type: String,
    val date: String,
    val destination: String,
    val destination_pair_str: String,
    val discount: Double,
    val discount_type: Int,
    val is_mot_coupon: Boolean,
    val is_roundtrip: Boolean,
    val is_rut_discount: Boolean,
    val number_of_seats: Int,
    val origin: String
)