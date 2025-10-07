package com.bitla.ts.domain.pojo.activate_deactivate_route

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Message {
    @SerializedName("message")
    @Expose
    var message: String = ""
}