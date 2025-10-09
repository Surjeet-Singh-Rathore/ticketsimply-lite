package com.bitla.ts.domain.pojo.all_reports.new_response.bus_service_collection_summary_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BusServiceCollectionReportResponse(

    @SerializedName("result")
    @Expose
    val result: ArrayList<BusServiceCollectionData>,

    @SerializedName("pdf_url")
    @Expose
    val pdfUrl: String = "",

    @SerializedName("total_seats")
    @Expose
    val totalSeats: String = "",

    @SerializedName("coach_number")
    @Expose
    val coachNumber: Int? = null,

    @SerializedName("code")
    @Expose
    val code: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("number_of_pages")
    @Expose
    var number_of_pages: Int? = 0,

    @SerializedName("total_items")
    @Expose
    val totalItems: Int? = 0,

    @SerializedName("current_page")
    @Expose
    var current_page: Int? = 0

)
