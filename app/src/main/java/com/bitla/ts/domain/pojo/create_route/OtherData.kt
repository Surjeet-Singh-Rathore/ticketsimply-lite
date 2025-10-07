package com.bitla.ts.domain.pojo.create_route

import com.bitla.ts.domain.pojo.stage_for_city.StageListData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OtherData {

    @SerializedName("allow_cancellation")
    @Expose
    var allowCancellation: String = ""

    @SerializedName("is_rapid_booking")
    @Expose
    var isRapidBooking: String = ""

    @SerializedName("allow_gents_next_to_ladies")
    @Expose
    var allowGentsNextToLadies: String = ""
}