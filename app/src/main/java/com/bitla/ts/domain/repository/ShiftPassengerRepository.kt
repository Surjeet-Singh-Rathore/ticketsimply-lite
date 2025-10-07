package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.auto_shift.request.AutoShiftRequest
import com.bitla.ts.domain.pojo.multiple_shift_passenger.request.MultiShiftPassengerRequest
import com.bitla.ts.domain.pojo.singleShiftPassenger.request.ReqBody
import com.bitla.ts.domain.pojo.singleShiftPassenger.request.SingleShiftPassengerRequest
import com.bitla.ts.koin.models.makeApiCall
import retrofit2.http.Query


class ShiftPassengerRepository (private val apiInterface: ApiInterface) {

    suspend fun newSingleShiftPassenger(
       singleShiftPassengerRequest: ReqBody
    ) = makeApiCall { apiInterface.newSingleShiftPassengerApi(singleShiftPassengerRequest) }



    suspend fun newMultiShiftPassenger(
        multiShiftPassengerRequest: com.bitla.ts.domain.pojo.multiple_shift_passenger.request.ReqBody
    ) = makeApiCall { apiInterface.newMultipleShiftPassengerApi(multiShiftPassengerRequest) }



    suspend fun newAutoShiftPassenger(
        reqBody: com.bitla.ts.domain.pojo.auto_shift.request.ReqBody
    ) = makeApiCall { apiInterface.newAutoShiftApi(reqBody.api_key,reqBody.auto_macth_by,reqBody.locale!!,reqBody.new_res_id,reqBody.old_res_id) }
}