package com.bitla.ts.domain.pojo.extend_fare.request

import com.google.gson.annotations.SerializedName

class RequestBodyExtendFarePojo {

    @SerializedName("route_id")
    var route_id: Int? = null

    @SerializedName("start_date")
    var start_date: String? = null

    @SerializedName("end_date")
    var end_date: String? = null

    @SerializedName("apply_date")
    var apply_date: String? = null

    @SerializedName("pick_from_date")
    var pick_from_date: String? = null

    @SerializedName("weekly_schedule_copy")
    var weekly_schedule_copy: String? = null

    @SerializedName("copy_type")
    var copy_type: String? = null

    @SerializedName("multiple_dates")
    var multipleDates: ArrayList<String> = arrayListOf()
}