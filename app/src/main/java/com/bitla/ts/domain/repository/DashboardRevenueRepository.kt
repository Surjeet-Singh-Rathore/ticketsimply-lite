package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.BpDpService.request.BpDpServiceRequest
import com.bitla.ts.domain.pojo.account_info.request.AgentAccountInfoRequest
import com.bitla.ts.koin.models.makeApiCall

class DashboardRevenueRepository(private val apiInterface: ApiInterface) {


    suspend fun getRevenueData(
        apiKey: String,
        from: String,
        to: String,
        routeId: String,
        journeyBy: String,
        pagination: String,
        perPage: String,
        page: String,
        filter: String,
        agentId:String="",
        hubId: String=""
    ) = makeApiCall { apiInterface.getAllRevenueDataApi(apiKey, from,to,routeId,journeyBy,pagination,page,perPage,filter,agentId,hubId) }


    suspend fun getRevenueRouteDetails(
        apiKey: String,
        from: String,
        to: String,
        routeId: String,
        journeyBY:String

    ) = makeApiCall { apiInterface.getRevenueRouteDetailsApi(apiKey, from,to,routeId,journeyBY) }


    suspend fun getRevenueAgentHubDetails(
        apiKey: String,
        from: String,
        to: String,
        agentId:String,
        hubId:String,
        journeyBy:String,

    ) = makeApiCall { apiInterface.getRevenueAgentHubDetails(apiKey, from,to,hubId,agentId,journeyBy) }


    suspend fun hubList(
        apiKey: String,
    ) = makeApiCall {
        apiInterface.hubListApi(
            apiKey
        )
    }



}