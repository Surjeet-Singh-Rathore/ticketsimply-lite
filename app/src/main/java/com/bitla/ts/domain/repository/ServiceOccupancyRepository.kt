package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class ServiceOccupancyRepository (private val apiInterface: ApiInterface) {
    suspend fun newServiceOccupancyDetails(
        apiKey: String,
        routeId : String,
        fromDate: String,
        toDate : String
    ) = apiInterface.newGetServiceOccupancyDetails(apiKey,routeId,fromDate,toDate)
}