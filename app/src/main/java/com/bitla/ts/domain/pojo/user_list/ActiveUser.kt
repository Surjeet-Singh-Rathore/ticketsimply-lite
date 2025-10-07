package com.bitla.ts.domain.pojo.user_list

data class ActiveUser(
    val id: Int,
    val label: String,
    val type: String?,
    val role_discount: Any?
)