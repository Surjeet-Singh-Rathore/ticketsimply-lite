package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.BpDpService.request.BpDpServiceRequest
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.koin.models.makeApiCall

class DashboardRevenueRepository(private val apiInterface: ApiInterface) {


    suspend fun hubList(
        apiKey: String,
    ) = makeApiCall {
        apiInterface.hubListApi(
            apiKey
        )
    }



}