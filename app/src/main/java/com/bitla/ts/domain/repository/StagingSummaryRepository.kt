package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class StagingSummaryRepository (private val apiInterface: ApiInterface) {
        suspend fun newStagingSummaryDetailsService(apiKey: String, resId: String) = apiInterface.newGetStagingSummary(apiKey,resId)

}