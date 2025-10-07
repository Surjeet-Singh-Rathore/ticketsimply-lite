package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ReqBody {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("discount_params")
    @Expose
    var discountParams: DiscountParams? = null

    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true

    @SerializedName("locale")
    var locale: String? = ""
}