package com.bitla.ts.domain.pojo

data class SpinnerItems(
    val id: Int,
    var value: String,
    var type: String = "",
    var branch_discount: Double? = 0.0,
    var role_discount: Double? = 0.0
) {
    override fun toString(): String {
        return value
    }
}