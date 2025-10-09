package com.bitla.ts.domain.pojo.all_reports.new_response.occupany_report_data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OccupancyNewResponse {

    @SerializedName("result")
    @Expose
    var result: ArrayList<Result>? = arrayListOf()

    @SerializedName("pdf_url")
    @Expose
    var pdfUrl: String? = ""

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("number_of_pages")
    @Expose
    var number_of_pages: Int? = 0

    @SerializedName("current_page")
    @Expose
    var current_page: Int? = 0

    @SerializedName("total_items")
    @Expose
    var total_items: Int? = 0



}