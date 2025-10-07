package com.bitla.ts.domain.pojo.getCouponDiscount.Response

data class GetCouponDetailResponse(
    val code: Int,
    val per_booking_coupons: ArrayList<PerBookingCoupon>,
    val per_seat_coupons: ArrayList<PerSeatCoupon>,
    val result: Result?=null
)
data class Result(
    val message: String?=null
)