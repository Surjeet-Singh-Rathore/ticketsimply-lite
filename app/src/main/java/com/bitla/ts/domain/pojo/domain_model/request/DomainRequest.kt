package com.bitla.ts.domain.pojo.domain_model.request

data class DomainRequest(
    val domain: String,
    val format: String,
    val method_name: String,
    val req_body: ReqBody
)

data class ReqBody(
    var locale: String?
)