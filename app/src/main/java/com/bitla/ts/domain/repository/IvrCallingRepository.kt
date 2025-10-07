package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.agent_recharge.request.ReqBody
import com.bitla.ts.domain.pojo.ivr_call.IvrCallRequest

class IvrCallingRepository(private val apiInterface: ApiInterface) {

    suspend fun newIvrCall(
//        reservationId:String,
//        apiKey:String,
//        boardingId:String,
//        option:String
        ivrCallRequest: IvrCallRequest
    ) = apiInterface.newIvrCallApi(ivrCallRequest)
}