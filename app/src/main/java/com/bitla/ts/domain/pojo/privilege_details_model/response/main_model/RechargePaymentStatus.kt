package com.bitla.ts.domain.pojo.privilege_details_model.response.main_model

data class RechargePaymentStatus(
    val id: Int,
    val label: String
) {
    override fun toString(): String {
        return label
    }
}