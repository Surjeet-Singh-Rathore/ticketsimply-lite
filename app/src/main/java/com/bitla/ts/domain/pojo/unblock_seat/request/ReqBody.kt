package com.bitla.ts.domain.pojo.unblock_seat.request


import com.google.gson.annotations.SerializedName

data class ReqBody(
    @SerializedName("api_key")
    var apiKey: String?,
    @SerializedName("is_from_middle_tier")
    var isFromMiddleTier: Boolean?,
    @SerializedName("locale")
    var locale: String?,
    @SerializedName("main_op_id")
    var mainOpId: String?,
    @SerializedName("operator_api_key")
    var operatorApiKey: String?,
    @SerializedName("record")
    var record: Record?,
    @SerializedName("res_id")
    var resId: String?,
    @SerializedName("row_count_dates")
    var rowCountDates: String?,
    @SerializedName("searchbus_params")
    var searchbusParams: SearchbusParams?,
    @SerializedName("selection_type")
    var selectionType: String?,
    @SerializedName("ticket")
    var ticket: Ticket?
)