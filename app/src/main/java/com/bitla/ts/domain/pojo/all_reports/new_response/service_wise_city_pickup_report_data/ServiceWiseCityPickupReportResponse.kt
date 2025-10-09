package com.bitla.ts.domain.pojo.all_reports.new_response.service_wise_city_pickup_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ServiceWiseCityPickupReportResponse(

    @SerializedName("result")
    @Expose
    val result: ArrayList<ServiceWiseCityPickupData>,

    @SerializedName("code")
    @Expose
    val code: Int? = 0,

    @SerializedName("pdf_url")
    @Expose
    var pdfUrl: String = "",

    @SerializedName("number_of_pages")
    @Expose
    var number_of_pages: Int? = 0,

    @SerializedName("message")
    @Expose
    val message: String? = "",

    @SerializedName("total_items")
    @Expose
    val totalItems: Int? = 0,

    @SerializedName("current_page")
    @Expose
    var current_page: Int? = 0
)
