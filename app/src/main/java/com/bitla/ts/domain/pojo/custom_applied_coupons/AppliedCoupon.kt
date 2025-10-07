package com.bitla.ts.domain.pojo.custom_applied_coupons

import com.bitla.ts.utils.ResourceProvider
import java.io.Serializable

data class AppliedCoupon(
    val coupon_code: String,
    var coupon_type: String = "",
    var previous_pnr_phone: String = "",
    var couponTypeResource: ResourceProvider.TextResource? = null
) : Serializable