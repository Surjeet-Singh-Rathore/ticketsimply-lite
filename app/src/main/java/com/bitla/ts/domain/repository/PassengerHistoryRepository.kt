package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.passenger_history.request.PassengerHistoryRequest
import com.bitla.ts.koin.models.makeApiCall


class PassengerHistoryRepository(private val apiInterface: ApiInterface) {

    suspend fun newPassengerHistory(
        apiKey: String,
        passenger_details: String,
        operator_api_key: String,
        locale: String
    ) = makeApiCall { apiInterface.newPassengerHistory(apiKey,passenger_details,operator_api_key,locale) }
}