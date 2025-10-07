package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.notify_passengers.request.NotifyPassengersRequest
import com.bitla.ts.koin.models.makeApiCall


class AllPassengersRepository(private val apiInterface: ApiInterface) {

    suspend fun newNotifyPassengers(
        notifyPassengersRequest: com.bitla.ts.domain.pojo.notify_passengers.request.ReqBody
    ) = makeApiCall { apiInterface.newNotifyPassengersApi(notifyPassengersRequest) }
}
