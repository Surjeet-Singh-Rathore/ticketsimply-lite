package com.bitla.ts.domain.pojo.user_list.request


data class UserListRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)