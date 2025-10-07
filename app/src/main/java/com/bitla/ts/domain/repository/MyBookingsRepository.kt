package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.my_bookings.request.MyBookingsRequest
import com.bitla.ts.koin.models.makeApiCall

class MyBookingsRepository(private val apiInterface: ApiInterface) {


    suspend fun myNewBookings(
        apiKey: String,
        responseFormat : String,
        from_date: String,
        to_date: String,
        dateType: Int,
        locale: String
    ) = makeApiCall {  apiInterface.myNewBookingsApi(apiKey,responseFormat,from_date,to_date,dateType,locale)}

}