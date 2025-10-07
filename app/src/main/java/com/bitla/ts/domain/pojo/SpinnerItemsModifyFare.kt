package com.bitla.ts.domain.pojo

data class SpinnerItemsModifyFare(
    val id: String,
    var value: String,
    var type: String = "",
) {
    override fun toString(): String {
        return value
    }
}