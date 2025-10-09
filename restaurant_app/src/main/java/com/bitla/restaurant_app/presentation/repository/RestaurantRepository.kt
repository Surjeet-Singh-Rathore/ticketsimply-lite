package com.bitla.restaurant_app.presentation.repository

import com.bitla.restaurant_app.presentation.network.RetrofitInstance
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctRequest.AllotedDirectRequest

class RestaurantRepository {
    private val retrofitInstance = RetrofitInstance.apiService

    suspend fun getRestaurantListApi(
        apiKey: String
    ) = retrofitInstance.getRestaurantListApi(apiKey)


    suspend fun newAllotedService(
        allotedRequest: AllotedDirectRequest
    ) = retrofitInstance.allotedServiceDirectCall(
        allotedRequest.is_group_by_hubs,
        allotedRequest.hub_id,
        allotedRequest.api_key,
        allotedRequest.travel_date,
        allotedRequest.page,
        allotedRequest.per_page,
        allotedRequest.view_mode,
        allotedRequest.pagination,
        allotedRequest.origin,
        allotedRequest.destination,
        allotedRequest.locale,
        allotedRequest.isCheckingInspector,
        allotedRequest.serviceFilter,
        allotedRequest.res_id
    )


    suspend fun getReportApi(
        apiKey: String,
        respFormat: String,
        isPdfDownload: Boolean,
        fromDate: String,
        toDate: String,
        locale: String,
        page: Int,
        perPage: Int,
        pagination: Boolean,
        restaurantId: String,
        serviceId: String

    ) = retrofitInstance.getRestaurantReportApi(
        apiKey, respFormat, isPdfDownload, fromDate, toDate, locale,
        page, perPage, pagination, restaurantId, serviceId


    )

}