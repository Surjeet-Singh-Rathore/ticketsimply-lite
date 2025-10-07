package com.bitla.ts.domain.pojo.service_routes_list.request

import com.bitla.ts.domain.pojo.available_routes.request.ReqBody

data class ServiceRoutesListRequest(
    val bcc_id: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)
