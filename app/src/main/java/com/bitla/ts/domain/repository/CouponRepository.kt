package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.coupon.request.CouponRequest
import com.bitla.ts.domain.pojo.smart_miles_otp.request.ReqBody
import com.bitla.ts.domain.pojo.smart_miles_otp.request.SmartMilesOtpRequest
import com.bitla.ts.koin.models.makeApiCall

class CouponRepository(private val apiInterface: ApiInterface) {

    suspend fun newValidateCoupon(
        couponRequest: com.bitla.ts.domain.pojo.coupon.request.ReqBody
    ) = makeApiCall { apiInterface.newValidateCoupons(couponRequest) }


    suspend fun newSmartMilesOtp(
        smartMilesOtpRequest: ReqBody
    ) = makeApiCall { apiInterface.newSmartMilesOtp( smartMilesOtpRequest) }
}