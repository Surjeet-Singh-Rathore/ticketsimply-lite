package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.update_coach_type.request.UpdateCoachTypeRequest
import com.bitla.ts.koin.models.makeApiCall

class UpdateCoachTypeRepository(private val apiInterface: ApiInterface) {
    suspend fun updateCoachTypeApi(
        updateCoachTypeRequest: UpdateCoachTypeRequest
    )
    = makeApiCall {
        apiInterface.updateCoachTypeApi(updateCoachTypeRequest)
    }
}