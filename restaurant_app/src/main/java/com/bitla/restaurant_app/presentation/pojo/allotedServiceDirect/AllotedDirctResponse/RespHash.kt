package com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse


data class RespHash(

    val services: ArrayList<Service>,
    val hub_name: String? = null
)