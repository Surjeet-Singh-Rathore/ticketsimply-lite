package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.koin.models.makeApiCall


class MoveQuotaBlockSeatRepository(private val apiInterface: ApiInterface) {

    suspend fun moveQuotaBlockSeatApi(
        blockingNumber: String,
        oldSeatNumber:String,
        newSeatNumber:String,
        apiKey:String,

    ) = makeApiCall { apiInterface.moveQuotaBlockSeatApi(blockingNumber,oldSeatNumber,newSeatNumber,apiKey) }


}
