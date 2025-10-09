package com.bitla.restaurant_app.presentation.pojo.reports

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class ReportsResponse:Parcelable {
    @SerializedName("pdf_url")
    @Expose
    var pdfUrl: String? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null

    @SerializedName("total_items")
    @Expose
    var totalItems: Int? = null

    @SerializedName("number_of_pages")
    @Expose
    var numberOfPages: Int? = null

    @SerializedName("current_page")
    @Expose
    var currentPage: Int? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""
}