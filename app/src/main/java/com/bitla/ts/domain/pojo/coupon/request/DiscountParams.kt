package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DiscountParams {
    @SerializedName("coupon_code_hash")
    @Expose
    var couponCodeHash: CouponCodeHash? = null

    @SerializedName("promotion_coupon_code_hash")
    @Expose
    var promotionCouponCodeHash: PromotionCouponHash? = null


    @SerializedName("previous_pnr_hash")
    @Expose
    var previousPnrHash: PreviousPnrHash? = null

    @SerializedName("pre_post_pone_hash")
    @Expose
    var prePostPoneHash: PrePostPoneHash? = null


    @SerializedName("privilege_card_hash")
    @Expose
    var privilegeCardHash: PrivilegeCardHash? = null

    @SerializedName("smart_miles_hash")
    @Expose
    var smartMilesHash: SmartMilesHash? = null

    @SerializedName("fare_hash")
    @Expose
    var fareHash: MutableList<FareHash> = arrayListOf()
}