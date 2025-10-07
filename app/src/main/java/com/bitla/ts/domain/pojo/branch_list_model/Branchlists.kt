package com.bitla.ts.domain.pojo.branch_list_model

data class Branchlists(
    val id: Int,
    val label: String,
    val type: String?,
    var branch_discount: Any?
)