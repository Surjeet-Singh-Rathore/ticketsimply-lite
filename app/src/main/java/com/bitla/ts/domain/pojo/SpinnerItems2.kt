package com.bitla.ts.domain.pojo

data class SpinnerItems2(
    val id: Int,
    var value: String,
    var dash: String,

    val id2: Int,
    var value2: String,

    ) {

    override fun toString(): String {
        return "$value $dash $value2"
    }
}