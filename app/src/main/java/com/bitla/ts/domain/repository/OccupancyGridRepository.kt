package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.ActivateDeactivateServiceRequest
import com.bitla.ts.domain.pojo.active_inactive_services.request.ActiveInactiveServicesRequest
import com.bitla.ts.domain.pojo.occupancy_datewise.request.OccupancyDateWiseRequest
import com.bitla.ts.koin.models.makeApiCall

class OccupancyGridRepository(private val apiInterface: ApiInterface) {

    suspend fun getOccupancyDateWise(occupancyDateWiseRequest: OccupancyDateWiseRequest) = makeApiCall {
        apiInterface.getOccupancyDateWise(
            apikey = occupancyDateWiseRequest.apiKey ?: "",
            date = occupancyDateWiseRequest.date ?: "",
            routeId = occupancyDateWiseRequest.routeId ?: "-1"
        )
    }

    suspend fun getActiveInactiveServices(activeInactiveServicesRequest: ActiveInactiveServicesRequest) =
        apiInterface.getActiveInactiveServices(
            apikey = activeInactiveServicesRequest.apiKey ?: "",
            from = activeInactiveServicesRequest.from ?: "",
            to = activeInactiveServicesRequest.to ?: ""
        )

    suspend fun activateDeactivateServices(apikey: String?, activateDeactivateServiceRequest: ActivateDeactivateServiceRequest) = makeApiCall {
        apiInterface.activateDeactivateServices(
            apikey = apikey ?: "",
            activateDeactivateServiceRequest = activateDeactivateServiceRequest
        )
    }
}