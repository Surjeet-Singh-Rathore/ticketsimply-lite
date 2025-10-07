package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class BranchWiseRevenueRepository (private val apiInterface: ApiInterface) {

    suspend fun newBranchWiseRevenueDetails(
        apiKey: String,
        branchId : String,
        fromDate: String,
        toDate : String
    ) = apiInterface.newGetBranchWiseRevenueDetails(apiKey,branchId,fromDate,toDate)
}