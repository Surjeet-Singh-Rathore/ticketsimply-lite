package com.bitla.ts.domain.pojo.login_auth_post.request

data class LoginAuthPostRequest(
    val device_id: String?,
    val is_encrypted: Boolean?,
    val is_from_middle_tier: Boolean?,
    val login: String?,
    val password: String?,
    val locale: String?,
    val shift_id: Int? = null,
    val counter_id: Int? = null,
    val counter_balance: String? = ""
)