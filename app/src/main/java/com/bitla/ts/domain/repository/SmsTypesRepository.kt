package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.sms_types.request.SmsTypesRequest
import com.bitla.ts.koin.models.makeApiCall

class SmsTypesRepository(private val apiInterface: ApiInterface) {

    suspend fun newSmsTypes(
        apiKey : String,
        resId : String,
        locale : String,
        responseFormat:String
    ) = makeApiCall { apiInterface.newSmsTypesApi(apiKey, resId, locale,responseFormat) }
}
