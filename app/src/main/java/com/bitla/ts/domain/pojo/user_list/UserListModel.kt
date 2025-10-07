package com.bitla.ts.domain.pojo.user_list

data class UserListModel(
    val code: Int?,
    val active_users: List<ActiveUser>,
    val hub_list:List<ActiveUser>,
    val message: String?
)