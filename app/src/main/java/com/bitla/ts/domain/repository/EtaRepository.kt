package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.eta.Request.EtaRequest
import com.bitla.ts.domain.pojo.eta.Request.ReqBody
import com.bitla.ts.koin.models.makeApiCall


class EtaRepository(private val apiInterface: ApiInterface) {


    suspend fun newEta(
        reqBody: ReqBody
    ) = makeApiCall {  apiInterface.newEtaApi(reqBody.api_key,reqBody.locale!!,reqBody.route_id,reqBody.travel_date) }
}
