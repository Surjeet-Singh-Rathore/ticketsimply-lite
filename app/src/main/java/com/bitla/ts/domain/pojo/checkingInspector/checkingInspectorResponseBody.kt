package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.annotations.SerializedName
import org.json.JSONArray

data class checkingInspectorResponseBody(
    var code: String = "",
    var result: checkingInspectorResponseBody,
    var message: String="",


)