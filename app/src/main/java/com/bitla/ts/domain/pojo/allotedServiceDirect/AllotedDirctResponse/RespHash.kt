package com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse

data class RespHash(

    val services: ArrayList<Service>,
    val hub_name: String? = null
)