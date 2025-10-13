package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.starred_reports.request.StarredReportsRequest
import com.bitla.ts.koin.models.makeApiCall


class StarredReportsRepository(private val apiInterface: ApiInterface) {

    suspend fun newStarredReports(
       apiKey:String,
       recentData : Boolean,
       locale : String
    ) = makeApiCall {  apiInterface.newStarredReport(apiKey,recentData,locale) }
}
