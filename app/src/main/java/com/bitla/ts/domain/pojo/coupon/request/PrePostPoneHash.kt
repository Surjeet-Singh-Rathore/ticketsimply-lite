package com.bitla.ts.domain.pojo.coupon.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PrePostPoneHash {
    @SerializedName("pnr_number")
    @Expose
    var pnrNumber: String? = null

    @SerializedName("agent_type")
    @Expose
    var agentType: String? = null

    @SerializedName("is_bima_service")
    @Expose
    var isBimaService: String? = null

    @SerializedName("reservation_id")
    @Expose
    var reservationId: String? = null


    @SerializedName("journey_date")
    @Expose
    var journeyDate: String = ""

    @SerializedName("origin_id")
    @Expose
    var originId: String? = null

    @SerializedName("destination_id")
    @Expose
    var destinationId: String? = null

    @SerializedName("no_of_seats")
    @Expose
    var noOfSeats: String? = null

    @SerializedName("corp_company_id")
    @Expose
    var corpCompanyId: String? = null

    @SerializedName("allow_pre_post_pone_other_branch")
    @Expose
    var allowPrePostPoneOtherBranch: String? = null
}