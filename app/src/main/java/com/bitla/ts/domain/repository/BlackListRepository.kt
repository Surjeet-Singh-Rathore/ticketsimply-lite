package com.bitla.ts.domain.repository

import com.bitla.ts.data.ApiInterface

class BlackListRepository (private val apiInterface: ApiInterface) {


    suspend fun newBlackListNumber(
        apiKey: String,
        phoneNumber:String,
        locale:String,
        remarks:String,
        status:String
    ) = apiInterface.newBlackListNumberApi(apiKey,locale,phoneNumber,remarks,status)
    suspend fun newBlockedNumbersList(
        apiKey: String,
        locale: String
    ) = apiInterface.getBlackListNumbersList(apiKey, locale)
}