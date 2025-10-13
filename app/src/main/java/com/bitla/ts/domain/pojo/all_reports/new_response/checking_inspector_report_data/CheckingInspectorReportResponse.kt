package com.bitla.ts.domain.pojo.all_reports.new_response.checking_inspector_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CheckingInspectorReportResponse(
    @SerializedName("pdf_url")
    @Expose
    val pdfUrl: String?,

    @SerializedName("result")
    @Expose
    val result: List<InspectionResult>,

    @SerializedName("code")
    @Expose
    val code: Int? = 0,

    @SerializedName("number_of_pages")
    @Expose
    val number_of_pages: Int? = 0,

    @SerializedName("page")
    @Expose
    val page: Int? = 0,

    @SerializedName("message")
    @Expose
    val message: String = "",

    @SerializedName("total_items")
    @Expose
    val totalItems: Int? = 0
)
