package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class RestaurantRepository(private val apiInterface: ApiInterface) {


    suspend fun getRestaurantListApi(
        apiKey: String
    ) = apiInterface.getRestaurantListApi(apiKey)




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

    ) = apiInterface.getRestaurantReportApi(
        apiKey, respFormat, isPdfDownload, fromDate, toDate, locale,
        page, perPage, pagination, restaurantId, serviceId


    )

}