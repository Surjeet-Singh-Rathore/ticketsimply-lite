package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.request.AddBpDpToServiceRequest
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.MergeBusSeatMappingRequest
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request.MergeBusShiftPassengerRequest
import com.bitla.ts.domain.pojo.merge_service_details.request.MergeServiceDetailsRequest
import com.bitla.ts.domain.pojo.recommended_seats.request.RecommendedSeatsRequest

class MergeBusShiftRepository(private val apiInterface: ApiInterface) {

    suspend fun newGetServiceDetailMergeBus(
        reservationId: String,
        origin:String,
        destinationId:String,
        apiKey: String,
        operator_api_key:String,
        locale:String,
        excludePassengerDetails : Boolean
    ) = apiInterface.newGetServiceDetailMergeBus(
        reservationId,
        origin,
        destinationId,
        apiKey,
        operator_api_key,
        excludePassengerDetails,
        locale)

    suspend fun getShiftToServices(
        originId: String, destinationId: String,travelDate: String,apiKey: String, responseFormat: String,
        locale: String,oldResId:String
    ) = apiInterface.getMergeBusShiftToServices(
        originId,
        destinationId,
        travelDate,
        apiKey,
        responseFormat,
        locale,
        oldResId
    )

    suspend fun getMergeBusRecommendedSeats(
        recommendedSeatsRequest: RecommendedSeatsRequest
    ) = apiInterface.getMergeBusRecommendedSeats(
        apiKey = recommendedSeatsRequest.apiKey,
        resId = recommendedSeatsRequest.resId,
        pnrNumber = recommendedSeatsRequest.pnr,
        originId = recommendedSeatsRequest.originId,
        destinationId = recommendedSeatsRequest.destinationId,
        excludePassengerDetails = recommendedSeatsRequest.excludePassengerDetails,
        locale = recommendedSeatsRequest.locale

    )
    suspend fun getMergeServiceDetails(
        mergeServiceDetailsRequest: MergeServiceDetailsRequest
    ) = apiInterface.getMergeServiceDetails(
        apiKey = mergeServiceDetailsRequest.apiKey,
        resId = mergeServiceDetailsRequest.resId,
        originId = mergeServiceDetailsRequest.originId,
        destinationId = mergeServiceDetailsRequest.destinationId,
        excludePassengerDetails = mergeServiceDetailsRequest.excludePassengerDetails,
        locale = mergeServiceDetailsRequest.locale
    )

    suspend fun mergeBusShiftPassenger(
        mergeBusShiftPassengerRequest: MergeBusShiftPassengerRequest
    ) = apiInterface.mergeBusShiftPassenger(
        apiKey = mergeBusShiftPassengerRequest.apiKey,
        mergeBusShiftPassengerRequest = mergeBusShiftPassengerRequest
    )

    suspend fun mergeBusSeatMapping(
        mergeBusSeatMappingRequest: MergeBusSeatMappingRequest
    ) = apiInterface.mergeBusSeatMapping(
        apiKey = mergeBusSeatMappingRequest.apiKey,
        mergeBusSeatMappingRequest = mergeBusSeatMappingRequest
    )
    suspend fun addBpDpToService(
        addBpDpToServiceRequest: AddBpDpToServiceRequest
    ) = apiInterface.addBpDpToService(addBpDpToServiceRequest
    )

    suspend fun getMultiStationSeatDataApi(
        apiKey: String,
        reservationId: String,
        seatNumber: String,
        isBima: Boolean,
    ) = apiInterface.getMultiStationSeatDataApiMergeBus(
        apiKey,
        reservationId,
        seatNumber,
        isBima = isBima
    )
}