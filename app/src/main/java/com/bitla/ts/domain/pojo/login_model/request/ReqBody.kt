package com.bitla.ts.domain.pojo.login_model.request

data class ReqBody(
    val login: String,
    val password: String,
    val api_key: String,
    var locale: String?,
    var device_id: String = ""
)