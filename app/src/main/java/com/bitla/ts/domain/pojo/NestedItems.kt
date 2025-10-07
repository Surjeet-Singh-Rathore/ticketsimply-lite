package com.bitla.ts.domain.pojo

data class NestedItems(
    val name: String,
    val subItemList: List<NestedSubItems>
)