package com.bitla.ts.domain.pojo.view_reservation


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject

data class CheckingInspectorRequestBody(
    var remarks: String = "",
    var extra_cabins: String="",
    var inspection_summary: JsonObject ?= null,
    var boarded_details: JsonArray?= null,


)