package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.boarding_stage_seats.request.BoardingStageSeatsRequest
import com.bitla.ts.domain.pojo.reservation_stages.request.ReservationStagesRequest
import com.bitla.ts.koin.models.makeApiCall

class CoachLayoutReportingRepository(private val apiInterface: ApiInterface) {
    suspend fun getReservationStagesApi(
        reservationStagesRequest: ReservationStagesRequest
    ) = makeApiCall {
        apiInterface.getReservationStagesApi(
            reservationId = reservationStagesRequest.reservationId,
            apiKey = reservationStagesRequest.apiKey,
            operatorApiKey = reservationStagesRequest.operatorApiKey,
            locale = reservationStagesRequest.locale
        )
    }

    suspend fun getBoardingStageSeatsApi(
        boardingStageSeatsRequest: BoardingStageSeatsRequest
    ) = makeApiCall {
        apiInterface.getBoardingStageSeatsApi(
            reservationId = boardingStageSeatsRequest.reservationId,
            originId = boardingStageSeatsRequest.originId,
            destinationId = boardingStageSeatsRequest.destinationId,
            apiKey = boardingStageSeatsRequest.apiKey,
            operatorApiKey = boardingStageSeatsRequest.operatorApiKey,
            locale = boardingStageSeatsRequest.locale,
            appBimaEnabled = boardingStageSeatsRequest.appBimaEnabled,
            boardingId = boardingStageSeatsRequest.boardingId
        )
    }
}