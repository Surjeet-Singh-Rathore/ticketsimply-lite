package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface
import com.bitla.ts.domain.pojo.block_seats.request.ReqBody__1
import com.bitla.ts.domain.pojo.unblock_seat.request.ReqBody
import com.bitla.ts.koin.models.makeApiCall


class BlockRepository(private val apiInterface: ApiInterface) {


    suspend fun newBlockConfig(
        apiKey: String,
        locale: String
    ) = makeApiCall { apiInterface.newBlockConfigApi(apiKey, locale) }


    suspend fun newUserList(
        apiKey: String,
        cityId: String,
        userType: String,
        branch_id: String,
        locale: String
    ) = makeApiCall {
        apiInterface.newUserListApi(
            apiKey,
            cityId,
            userType,
            branch_id,
            locale
        )
    }



    suspend fun newBranchList(
        apiKey: String,
        locale: String
    ) = makeApiCall { apiInterface.newBranchListApi( apiKey, locale) }

    suspend fun newBlockSeats(
        blockSeatRequest: ReqBody__1
    ) = makeApiCall { apiInterface.newBlockSeatApi(blockSeatRequest) }


    suspend fun newUnBlockSeats(
        unblockSeatRequest: ReqBody
    ) = makeApiCall { apiInterface.unblockSeatApi(unblockSeatRequest) }

    suspend fun quotaBlockingTooltipInfo(
        apiKey: String,
        resId: String,
        seatNumber: String,
        locale: String
    ) = makeApiCall {
        apiInterface.quotaBlockingTooltipInfoApi(
            apikey = apiKey,
            resId = resId,
            seatNumber = seatNumber,
            locale = locale
        )
    }

}
