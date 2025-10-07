package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.move_to_extra_seat.request.ReqBody
import com.bitla.ts.domain.pojo.move_to_normal_seats.MoveToNormalSeatRequest
import com.bitla.ts.koin.models.makeApiCall
import com.google.gson.JsonObject


class MoveToExtraSeatRepository(private val apiInterface: ApiInterface) {

    suspend fun newMoveToExtraSeatApi(
        moveToExtraSeatRequest: ReqBody
    ) = makeApiCall { apiInterface.newMoveToExtraSeatApi(moveToExtraSeatRequest) }



    suspend fun newMoveToNormalSeatApi(
        moveToNormalSeatRequest: MoveToNormalSeatRequest
    ) = makeApiCall { apiInterface.newMoveToNormalSeatApi(moveToNormalSeatRequest) }



}
