package com.bitla.ts.domain.pojo.domain_model

data class DomainModel(
    val code: Int,
    val logo_url: String,
    val bcc_id: Int? = null,
    val mba_url: String,
    val message: String,
    val result: Result,
    val dailing_code: ArrayList<Int>
)

data class Result(
    val logo_url: String
)