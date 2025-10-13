package com.bitla.ts.domain.pojo.all_reports.all_report_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Report {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("service_active")
    @Expose
    var serviceActive: List<Int>? = null

    @SerializedName("date")
    @Expose
    var date: String? = null
}