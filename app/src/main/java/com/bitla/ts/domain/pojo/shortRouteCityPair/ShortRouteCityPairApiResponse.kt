package com.bitla.ts.domain.pojo.shortRouteCityPair

data class ShortRouteCityPairApiResponse(
    val cities: List<City>,
    val code: Int,
    val message: String?=null
)