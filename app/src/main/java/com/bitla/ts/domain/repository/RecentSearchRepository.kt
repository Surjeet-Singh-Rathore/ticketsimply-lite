package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.koin.models.makeApiCall

class RecentSearchRepository(private val apiInterface: ApiInterface) {

    suspend fun getNewDestinationPair(
        apiKey: String,
        operatorKey: String,
        responseFormat: String,
        appBimaEnable: Boolean,
        locale: String
    ) = makeApiCall {
        apiInterface.getNewDestinationPairs(
            apiKey,
            operatorKey,
            responseFormat,
            appBimaEnable,
            locale
        )
    }

    suspend fun destinationListWithOrigin(
        apiKey: String,
        originId: String,resId:String?= null) = makeApiCall {  apiInterface.destinationListWithOrigin(apiKey, originId,resId) }


    suspend fun getDestinationList(
        apiKey: String,
        originId: String,resId:String?= null) = apiInterface.getDestinationListWithOrigin(apiKey, originId)

}
