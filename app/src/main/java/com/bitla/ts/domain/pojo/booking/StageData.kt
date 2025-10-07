package com.bitla.ts.domain.pojo.booking

data class StageData(
    val title: String = "",
    val isRemovable: Boolean = false,
    var isSelected: Boolean = false,
    var layoutType: String = "",
    var origin_id: String? = "0",
    var destination_id: String? = "0"
)