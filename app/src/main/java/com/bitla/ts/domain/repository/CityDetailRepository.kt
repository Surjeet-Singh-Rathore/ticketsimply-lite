package com.bitla.ts.domain.repository

import com.bitla.ts.data.*
import com.bitla.ts.domain.pojo.phone_block_temp_to_permanent_data.request.*
import com.bitla.ts.koin.models.makeApiCall

class CityDetailRepository(private val apiInterface: ApiInterface) {

    suspend fun newCityDetailservice(
        apiKey: String,
        responseFormat: String,
        locale: String,
    ) = makeApiCall { apiInterface.newGetCityList(apiKey, responseFormat, locale) }


    suspend fun newStateDetailService(
        apiKey: String,
        responseFormat: String,
        locale: String,
    ) = makeApiCall { apiInterface.newGetStateList( apiKey, responseFormat,locale) }
    
    suspend fun getMultiStationSeatDataApi(
        apiKey: String,
        reservationId: String,
        seatNumber: String,
        isBima: Boolean,
        locale: String
    ) = makeApiCall {
        apiInterface.getMultiStationSeatDataApi(
            apiKey,
            reservationId,
            seatNumber,
            isBima = isBima,
            locale = locale
        )
    }

    suspend fun getPhoneBlockTempToPermanent(
        phoneBlockTempToPermanent: PhoneBlockTempToPermanentReq,
        ) = makeApiCall {
        apiInterface.getPhoneBlockTempToPermanent(
            phoneBlockTempToPermanent
        )
    }

}