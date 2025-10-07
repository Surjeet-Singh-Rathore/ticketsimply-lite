package com.bitla.ts.domain.pojo.crew_update

data class UpdateCrew(
    val code: Int,
    val message: String,
    val pdf_url: String,
    var result: Result
)