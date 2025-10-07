package com.bitla.ts.domain.pojo.campaigns_and_promotions_discount.request


import com.google.gson.annotations.SerializedName
import retrofit2.http.Path
import retrofit2.http.Query

data class CampaignsAndPromotionsDiscountRequest(
    val reservationId: String?,
    val api_key: String?,
    val operator_api_key: String?,
    val locale: String?,
    val origin_id: String?,
    val destination_id: String?,
    val boardingAt: String?,
    val dropOff: String?,
    val reqBody: ReqBody?
)