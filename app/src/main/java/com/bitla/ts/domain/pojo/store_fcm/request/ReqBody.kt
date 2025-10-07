package com.bitla.ts.domain.pojo.store_fcm.request

data class ReqBody(
    val api_key: String,
    val device_id: String,
    val fcm_key: String
)