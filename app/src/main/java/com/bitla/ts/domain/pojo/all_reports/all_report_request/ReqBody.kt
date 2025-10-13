package com.bitla.ts.domain.pojo.all_reports.all_report_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReqBody {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("date_range")
    @Expose
    var dateRange: String? = null

    @SerializedName("date_type")
    @Expose
    var dateType: Int? = null

    @SerializedName("date_wise")
    @Expose
    var dateWise: String? = null

    @SerializedName("from_date")
    @Expose
    var fromDate: String? = null

    @SerializedName("is_from_middle_tier")
    @Expose
    var isFromMiddleTier: Boolean? = null

    @SerializedName("is_pdf_download")
    @Expose
    var isPdfDownload: Boolean? = null

    @SerializedName("locale")
    @Expose
    var locale: String? = null

    @SerializedName("occupancy_type")
    @Expose
    var occupancyType: String? = null

    @SerializedName("response_format")
    @Expose
    var responseFormat: String? = null

    @SerializedName("route_id")
    @Expose
    var routeId: String? = null


    @SerializedName("restaurant_id")
    @Expose
    var restaurantId: String? = null

    @SerializedName("to_date")
    @Expose
    var toDate: String? = null

    @SerializedName("report")
    @Expose
    var report: Report? = null

    @SerializedName("is_export_pdf")
    @Expose
    var isExportPdf: String? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("hub_options")
    @Expose
    var hubOptions: String? = null

    @SerializedName("is_starred_report")
    @Expose
    var isStarredReport: Boolean? = null

    @SerializedName("bus_groups")
    @Expose
    var busGroups: String? = null

    @SerializedName("is_report")
    @Expose
    var isReport: Boolean = false

    @SerializedName("is_detailed")
    @Expose
    var isDetailed: Boolean = false

    @SerializedName("branch_id")
    @Expose
    var branchId: String = ""

    @SerializedName("user_id")
    @Expose
    var userId: String = ""

    @SerializedName("page")
    @Expose
    var page: Int? = 0

    @SerializedName("per_page")
    @Expose
    var perPage: Int? = 0

    @SerializedName("pagination")
    @Expose
    var pagination: Boolean?= false

    @SerializedName("payment_type")
    @Expose
    var paymentType: String= ""

}