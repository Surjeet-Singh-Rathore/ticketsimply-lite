package com.bitla.ts.domain.pojo.block_seats.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Record {
    @SerializedName("release_hours_before_departure")
    @Expose
    var release_hours_before_departure: String? = null

    @SerializedName("from_date")
    @Expose
    var from_date: String? = null

    @SerializedName("to_date")
    @Expose
    var to_date: String? = null

    @SerializedName("weekly_schedule")
    @Expose
    var weekly_schedule: String? = null

    @SerializedName("quota_release_hours")
    @Expose
    var quota_release_hours: String? = null

    @SerializedName("quota_release_mins")
    @Expose
    var quota_release_mins: String? = null

    @SerializedName("remarks")
    @Expose
    var remarks: String? = null
}