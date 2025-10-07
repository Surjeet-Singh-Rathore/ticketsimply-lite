package com.bitla.ts.domain.pojo.available_routes


data class AvailableRoutesModel(
    val code: Int,
    val message: String,
    val error: String,
    val number_of_pages: Int?= null,
    val current_page: Int?= null,
    val total_count: Int?= null,
    val result: MutableList<Result> = arrayListOf()
)