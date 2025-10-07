package com.bitla.ts.domain.pojo.eta

data class Eta(
    val code: Int,
    val eta_details: MutableList<EtaDetail>,
    val message: String?
)