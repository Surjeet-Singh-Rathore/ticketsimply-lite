package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.edit_chart.request.EditChartRequest
import com.bitla.ts.koin.models.makeApiCall

class EditChartRepository(private val apiInterface: ApiInterface) {


    suspend fun newEditChart(
        editChartRequest: com.bitla.ts.domain.pojo.edit_chart.request.ReqBody
    ) = makeApiCall { apiInterface.newEditChart(editChartRequest) }
}