package com.bitla.ts.domain.pojo.rutDiscountDetails.response

data class RutDiscountResponse(
    val code: Int,
    val per_booking_coupons: List<PerBookingCoupon>,
    val per_seat_coupons: List<PerSeatCoupon>,
    val result: Result?= null
)

data class Result(
    val message: String?=null
)