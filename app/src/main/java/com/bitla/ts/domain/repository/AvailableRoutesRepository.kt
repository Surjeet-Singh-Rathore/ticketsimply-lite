package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.single_block_unblock.single_block_unblock_request.SingleBlockUnblockRequest
import com.bitla.ts.koin.models.makeApiCall


class AvailableRoutesRepository(private val apiInterface: ApiInterface) {


    suspend fun getNewAvailableRoutes(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        showOnlyAvalServices: String,
        locale: String,
        app_bima_enabled: Boolean
    ) = makeApiCall {
        apiInterface.newAvailableRoutes(
            origin_id = originId,
            destination_id = destinationId,
            travel_date = travelDate,
            api_key = apiKey,
            show_injourney_services = showInJourneyServices,
            is_cs_shared = isCsShared,
            operator_api_key = operatorkey,
            response_format = responseFormat,
            show_only_available_services = showOnlyAvalServices,
            locale = locale,
            app_bima_enabled = app_bima_enabled
        )
    }

    suspend fun availableRoutesForAgent(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        showOnlyAvalServices: String,
        locale: String,
        pagination: String? = null,
        per_page: String? = null,
        page: String? = null
    ) = makeApiCall { apiInterface.availableRoutesForAgent (
            originId,
    destinationId,
    travelDate,
    apiKey,
    showInJourneyServices,
    isCsShared,
    operatorkey,
    responseFormat,
    showOnlyAvalServices,
    locale,
    loadAvailableSeats = "true",
    pagination,
    per_page,
    page
    )
}



    suspend fun availableRoutesForAllToAll(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        showOnlyAvalServices: String,
        locale: String,
        pagination: String? = null,
        per_page: String? = null,
        page: String? = null
    ) = makeApiCall { apiInterface.availableRoutesForAgent (
        originId,
        destinationId,
        travelDate,
        apiKey,
        showInJourneyServices,
        isCsShared,
        operatorkey,
        responseFormat,
        showOnlyAvalServices,
        locale,
        loadAvailableSeats = "true",
        pagination,
        per_page,
        page
    )
    }




/*    suspend fun singleBlockUnblock(
        authorization: String,
        apiKey: String,
        singleBlockUnblockRequest: SingleBlockUnblockRequest
    ) = apiInterface.newSingleBlockUnblock(authorization, apiKey, singleBlockUnblockRequest)*/

    suspend fun singleBlockUnblock(
        authorization: String,
        apiKey: String,
        singleBlockUnblockRequest: SingleBlockUnblockRequest,
        authPin: String
    ) = makeApiCall {
        apiInterface.newSingleBlockUnblock(
            apikey = singleBlockUnblockRequest.req_body.api_key,
            locale = singleBlockUnblockRequest.req_body.locale,
            remarks = singleBlockUnblockRequest.req_body.remarks,
            res_id = singleBlockUnblockRequest.req_body.res_id,
            response_format = singleBlockUnblockRequest.req_body.response_format,
            auth_pin = authPin,
            blockingReason = singleBlockUnblockRequest.req_body.blockingReason
        )
    }


    suspend fun getBpDpDetails(
        apiKey: String,originId: String, destinationId: String,resId: String
    ) = makeApiCall {
        apiInterface.getBpDpDetails(
            apiKey,
            originId,
            destinationId,
            resId
        )
    }

    suspend fun getServiceRoutesList(
        originId: String,
        destinationId: String,
        travelDate: String,
        apiKey: String,
        showInJourneyServices: String,
        isCsShared: Boolean,
        operatorkey: String,
        responseFormat: String,
        showOnlyAvalServices: String,
        locale: String
    ) = makeApiCall {
        apiInterface.serviceRoutesList(
            origin_id = originId,
            destination_id = destinationId,
            travel_date = travelDate,
            api_key = apiKey,
            show_injourney_services = showInJourneyServices,
            is_cs_shared = isCsShared,
            operator_api_key = operatorkey,
            response_format = responseFormat,
            show_only_available_services = showOnlyAvalServices,
            locale = locale
        )
    }
}