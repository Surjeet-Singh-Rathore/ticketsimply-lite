package com.bitla.ts.domain.pojo.alloted_services.request

class AllotedServiceRequest(
    val bcc_id: String,
    val method_name: String,
    val format: String,
    val req_body: com.bitla.ts.domain.pojo.alloted_services.request.ReqBody
)

