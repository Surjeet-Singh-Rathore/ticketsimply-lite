package com.bitla.ts.domain.pojo.logout_auth_post_req

data class FullLogoutReqBody (
    var api_key:String="",
    var device_id:String="",
    var is_middle_tier:Boolean=false,
    var is_encrypted:Boolean=false
)