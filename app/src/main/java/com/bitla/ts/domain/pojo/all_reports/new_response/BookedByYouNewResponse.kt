package com.bitla.ts.domain.pojo.all_reports.new_response

import com.bitla.ts.domain.pojo.all_reports.new_response.branch_collection_summary_report_data.TicketData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookedByYouNewResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("result")
    val result: Result,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("header")
    val header: String = "",
    @SerializedName("grand_total_amount")
    val grandTotalAmount: String = "",
    @SerializedName("pdf_url")
    var pdfUrl: String = "",
    @SerializedName("tickets")
    val tickets: ArrayList<TicketData> = arrayListOf(),

    @SerializedName("number_of_pages")
    @Expose
    var number_of_pages: Int? = 0,

    @SerializedName("current_page")
    @Expose
    var current_page: Int? = 0,

    @SerializedName("total_items")
    @Expose
    var total_items: Int? = 0
)