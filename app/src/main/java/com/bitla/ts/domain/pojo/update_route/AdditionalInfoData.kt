package com.bitla.ts.domain.pojo.update_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdditionalInfoData {

    @SerializedName("gst")
    @Expose
    var gst: Boolean = false

    @SerializedName("is_branch_booking")
    @Expose
    var branchBooking: Boolean = false

    @SerializedName("is_api_allowed")
    @Expose
    var apiBooking: Boolean = false

    @SerializedName("offline_booking")
    @Expose
    var offlineBooking: Boolean = false

    @SerializedName("allow_ladies_next_to_gents")
    @Expose
    var allowLadiesNextToGents: Boolean = false

    @SerializedName("allow_gents_next_to_ladies")
    @Expose
    var allowGentsNextToLadies: Boolean = false

    @SerializedName("remark")
    @Expose
    var remark: Boolean = false
}