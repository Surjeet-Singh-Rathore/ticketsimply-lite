package com.bitla.ts.domain.pojo.remote_config

data class UpdateCountryListData(
    val is_manual_update: Boolean,
    var is_global_update: Boolean?,
    var use_privilege_version_code: Boolean?,
    var global_update_details: UpdateDetailsData?,
    var country: ArrayList<UpdateDetailsData> = arrayListOf()
)
