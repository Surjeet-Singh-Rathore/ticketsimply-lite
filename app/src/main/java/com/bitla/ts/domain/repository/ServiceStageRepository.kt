package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class ServiceStageRepository (private val apiInterface: ApiInterface){
    suspend fun newLatLongApi(
        resId:String,
        apiKey: String,
    ) = apiInterface.newGetLatLongApi(resId,apiKey)
}