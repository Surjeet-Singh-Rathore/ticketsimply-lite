package com.bitla.ts.domain.pojo.remote_config

data class UpdateDetailsData(
    val id: Int,
    val country_name: String,
    val version: Int,
    var title: String?,
    var description: String?,
    var is_update: Boolean?,
    var is_critical: Boolean?,
)

