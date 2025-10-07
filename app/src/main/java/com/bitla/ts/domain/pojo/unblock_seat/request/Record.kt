package com.bitla.ts.domain.pojo.unblock_seat.request


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("from_date")
    var fromDate: String?,
    @SerializedName("quota_release_hours")
    var quotaReleaseHours: String?,
    @SerializedName("quota_release_mins")
    var quotaReleaseMins: String?,
    @SerializedName("release_hours_before_departure")
    var releaseHoursBeforeDeparture: String?,
    @SerializedName("to_date")
    var toDate: String?,
    @SerializedName("weekly_schedule")
    var weeklySchedule: String?,
    @SerializedName("remarks")
    var remarks: String?
)