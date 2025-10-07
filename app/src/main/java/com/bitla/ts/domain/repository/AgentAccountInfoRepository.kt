package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.BpDpService.request.BpDpServiceRequest
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.koin.models.makeApiCall

class AgentAccountInfoRepository(private val apiInterface: ApiInterface) {

    suspend fun getAgentAccountBalanceInfo(
        agentAccountInfoRequest: AgentAccountInfoRequest,
        agentId : String,
        branchId: String
    ) = makeApiCall {
        apiInterface.getAgentAccountBalanceInfo(
            agentAccountInfoRequest.req_body.api_key,
            agentAccountInfoRequest.req_body.locale,
            agentId, branchId
        )
    }


 suspend fun newBpDpService(
        reservation_id: String,
        apiKey: String
    ) = makeApiCall { apiInterface.newBpDpService(reservation_id, apiKey)}

}