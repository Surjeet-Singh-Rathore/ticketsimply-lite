package com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse



data class AllotedDirectResponse(
    val resp_hash: ArrayList<RespHash>? = null,
    val code: Int,
    val current_page: String,
    val header: String,
    val number_of_pages: Int,
    val result: Result?,
    val services: ArrayList<Service>? = null,
    val picku_van_services: ArrayList<PickuVanService>?= null
)

data class Result(
    val message: String,

    )