package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.multiple_services_manage_fare.request.ReqBody

class MultipleServicesManageFareRepository(private val apiInterface: ApiInterface) {

    suspend fun availableServiceList(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        pagination: Boolean,
        page: Int?,
        perPage: Int?
    ) = apiInterface.availableServiceList(
        originId,
        destinationId,
        travelDate,
        apiKey,
        pagination,
        page,
        perPage
    )

    suspend fun getSeatTypes(
        apiKey: String,
        routeIds: String,
        originId: String?,
        destinationId: String?,
        travelDate: String?
    ) = apiInterface.getSeatTypes(
        apiKey,
        routeIds,
        originId,
        destinationId,
        travelDate
    )

    suspend fun multipleServicesManageFares(
        reqBody: ReqBody
    ) = apiInterface.multipleServicesManageFares(
        reqBody
    )
}