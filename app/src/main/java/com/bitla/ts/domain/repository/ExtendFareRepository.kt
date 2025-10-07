package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.extend_fare.ExtendFareRequestModel
import com.bitla.ts.domain.pojo.extend_fare.request.RequestBody
import com.bitla.ts.koin.models.makeApiCall

class ExtendFareRepository(private val apiInterface: ApiInterface) {
    suspend fun newExtendFare(extendFareRequestModel: RequestBody)= makeApiCall { apiInterface.newExtendFare(extendFareRequestModel) }
}