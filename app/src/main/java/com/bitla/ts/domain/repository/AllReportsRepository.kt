package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.all_reports.all_report_request.AllReportRequest
import com.bitla.ts.koin.models.makeApiCall


class AllReportsRepository(private val apiInterface: ApiInterface) {
    suspend fun allReports(
        authorization: String,
        apiKey: String,
        allReportsRequest: AllReportRequest
    ) = makeApiCall { apiInterface.allReportsApi(authorization, apiKey, allReportsRequest) }


    suspend fun userCollectionDetailsAPi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.newUserCollectionDetailsApi(
            apikey = apiKey,
            date_range = reqBody.dateRange!!,
            date_wise = reqBody.dateWise!!,
            from_date = reqBody.fromDate!!,
            is_starred_report = reqBody.isStarredReport!!.toString(),
            response_format = reqBody.responseFormat!!,
            route_id = reqBody.routeId!!,
            to_date = reqBody.toDate!!,
            locale = locale,
            is_pdf_download = reqBody.isPdfDownload!!,
            is_detailed = reqBody.isDetailed,
            branchId = reqBody.branchId.toString(),
            userId = reqBody.userId.toString(),
            page = reqBody.page!!,
            per_page = reqBody.perPage!!,
            pagination = reqBody.pagination!!
        )
    }


    suspend fun checkingInspectorReportApi(
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.checkingInspectorReportApi(
            reqBody.apiKey!!,
            reqBody.travelDate!!,
            reqBody.routeId!!,
            reqBody.isStarredReport!!,
            reqBody.isPdfDownload!!
        )
    }

    suspend fun fuelTransactionDetailApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.fuelTransactionDetailApi(
            apiKey, reqBody.fromDate!!, reqBody.isExportPdf!!,
            reqBody.isStarredReport!!, reqBody.isPdfDownload!!, reqBody.toDate!!, locale
        )
    }

    suspend fun occupancyReportApi(
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall { apiInterface.occupancyReportApi(reqBody) }

    suspend fun occupancyReportApiNew(
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall { apiInterface.occupancyReportApiNew(reqBody) }

    suspend fun routeWiseBookingMemoApi(
        apikey: String,
        travel_date: String,
        is_starred_report: Boolean,
        is_pdf_download: Boolean,
        route_id: String,
        locale: String,
    ) = makeApiCall { apiInterface.routeWiseBookingMemoApi(apikey,travel_date,is_starred_report,is_pdf_download,route_id,locale) }

    suspend fun busServiceCollectionApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.busServiceCollectionApi(
            apiKey,
            reqBody.fromDate!!,
            reqBody.dateRange!!,
            reqBody.toDate!!,
            reqBody.busGroups!!,
            reqBody.hubOptions!!,
            reqBody.isStarredReport!!.toString(),
            reqBody.isPdfDownload!!,
            reqBody.dateWise!!,
            reqBody.routeId!!,
            locale,
            reqBody.responseFormat!!,
            reqBody.isDetailed!!
        )
    }

    suspend fun busServiceCollectionNewApi (
        apiKey: String,
        locale: String,
        coachId: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.busServiceCollectionNewApi(
            apiKey,
            reqBody.fromDate ?: "",
            reqBody.dateRange ?: "",
            reqBody.toDate ?: "",
            reqBody.busGroups ?: "",
            reqBody.hubOptions ?: "",
            reqBody.isStarredReport.toString(),
            reqBody.isPdfDownload ?: false,
            coachId,
            reqBody.isDetailed,
            reqBody.dateWise ?: "",
            reqBody.routeId ?: "",
            locale,
            reqBody.responseFormat ?: "hash",
            page = reqBody.page ?: 1,
            per_page = reqBody.perPage ?: 10,
            pagination = reqBody.pagination ?: true
        )
    }

    suspend fun groupByBranchReportApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.groupByBranchReportApi(
            apiKey, reqBody.isStarredReport!!.toString(), reqBody.isPdfDownload!!,
            reqBody.travelDate!!,
            reqBody.routeId!!, locale
        )
    }

    suspend fun groupByBranchNewReportApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.groupByBranchNewReportApi(
            apiKey,
            reqBody.isStarredReport.toString(),
             is_pdf_download = reqBody.isPdfDownload ?: false,
            travel_date = reqBody.travelDate ?: "",
            route_id = reqBody.routeId ?: "",
            locale
        )
    }

    suspend fun cargoBookingReportApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {  apiInterface.cargoBookingReport(
        apiKey, reqBody.fromDate!!,reqBody.toDate!!,reqBody.dateRange!!,reqBody.isPdfDownload!!,
        reqBody.routeId!!,locale
    )}



    suspend fun ticketsBookedByYouApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.ticketsBookedByYouApi(
            apiKey,
            reqBody.isStarredReport.toString(),
            reqBody.responseFormat!!.toString(),
            reqBody.isPdfDownload!!,
            reqBody.fromDate!!,
            reqBody.toDate!!, locale, reqBody.isReport, reqBody.routeId!!

        )
    }




    suspend fun ticketsBookedByYouNewApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.ticketsBookedByYouNewApi(
            api_key = apiKey,
            is_starred_report = reqBody.isStarredReport.toString(),
            resp_format = reqBody.responseFormat!!.toString(),
            is_pdf_download = reqBody.isPdfDownload!!,
            from_date = reqBody.fromDate!!,
            to_date = reqBody.toDate!!,
            locale = locale,
            is_report = reqBody.isReport,
            date_type = reqBody.dateType!!,
            page = reqBody.page ?: 1,
            per_page = reqBody.perPage ?: 10,
            pagination = reqBody.pagination ?: true,
            route_id = reqBody.routeId ?: "",
        )
    }

    suspend fun paymentStatusReportApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.paymentStatusReportApi(
            api_key = apiKey,
            is_starred_report = reqBody.isStarredReport.toString(),
            resp_format = reqBody.responseFormat.toString(),
            is_pdf_download = reqBody.isPdfDownload!!,
            from_date = reqBody.fromDate ?: "",
            to_date = reqBody.toDate ?: "",
            locale = locale,
            is_report = reqBody.isReport,
            date_type = reqBody.dateType ?: 0,
            page = reqBody.page ?: 1,
            per_page = reqBody.perPage ?: 10,
            pagination = reqBody.pagination ?: true,
            route_id = reqBody.routeId ?: "",
            paymentType = reqBody.paymentType

        )
    }

    suspend fun paymentStatusReportDownloadApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall { apiInterface.paymentStatusReportDownloadApi(
        apiKey,
        reqBody.isStarredReport.toString(),
        reqBody.responseFormat!!.toString(),
        reqBody.isPdfDownload!!,
        reqBody.fromDate!!,
        reqBody.toDate!!, locale, reqBody.isReport, reqBody.routeId!!,reqBody.dateType!!,   reqBody.paymentType!!
    )
}


    suspend fun serviceWiseCityPickup(
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall { apiInterface.serviceWiseCityPickup(reqBody)}


    suspend fun serviceWisePickupClosureReportNewApi(
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.serviceWisePickupClosureReportNewApi(
            reqBody
        )
    }

    suspend fun checkingInspectorReportNewApi(
        apiKey: String,
        locale: String,
        reqBody: com.bitla.ts.domain.pojo.all_reports.all_report_request.ReqBody
    ) = makeApiCall {
        apiInterface.checkingInspectorReportNewApi(
            apiKey,
            reqBody.isPdfDownload ?: false,
            reqBody.isStarredReport.toString(),
            reqBody.responseFormat.toString(),
            reqBody.routeId ?: "",
            reqBody.travelDate ?: "",
            reqBody.isFromMiddleTier ?: true,
            locale,
            page = reqBody.page ?: 1,
            perPage = reqBody.perPage ?: 10,
            pagination = reqBody.pagination ?: true
        )
    }

}
