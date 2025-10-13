package com.bitla.ts.domain.pojo.all_reports.all_report_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllReportRequest {
    @SerializedName("bcc_id")
    @Expose
    var bccId: String? = null

    @SerializedName("format")
    @Expose
    var format: String? = null

    @SerializedName("method_name")
    @Expose
    var methodName: String? = null

    @SerializedName("req_body")
    @Expose
    var reqBody: ReqBody? = null

}