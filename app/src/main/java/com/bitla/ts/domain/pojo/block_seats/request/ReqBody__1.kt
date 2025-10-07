package com.bitla.ts.domain.pojo.block_seats.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ReqBody__1 {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("operator_api_key")
    @Expose
    var operatorApiKey: String? = null

    @SerializedName("res_id")
    @Expose
    var resId: String? = null

    @SerializedName("main_op_id")
    @Expose
    var mainOpId: String? = null

    @SerializedName("locale")
    @Expose
    var locale: String? = null

    @SerializedName("searchbus_params")
    @Expose
    var searchbusParams: SearchbusParams? = null

    @SerializedName("agent_type")
    @Expose
    var agentType: String? = null

    @SerializedName("reserved-seat-count")
    @Expose
    var reservedSeatCount: Int? = null

    @SerializedName("ticket")
    @Expose
    var ticket: Ticket? = null

    @SerializedName("is_from_middle_tier")
    @Expose
    var isFromMiddleTier: Boolean? = null

    @SerializedName("selection_type")
    @Expose
    var selectionType: String? = null

    @SerializedName("record")
    @Expose
    var record: Record? = null

    /*User Quota*/
    @SerializedName("searchbus_on_behalf_branch")
    @Expose
    var searchBusOnBehalfBranch: String? = null

    @SerializedName("searchbus_on_behalf_user")
    @Expose
    var searchBusOnBehalfUser: String? = null


    /*Offline Agent Quota*/
    @SerializedName("searchbus_on_behalf")
    @Expose
    var searchBusOnBehalf: String? = null


    /*Online Agent Quota*/
    @SerializedName("searchbus_on_behalf_online_agent")
    @Expose
    var searchBusOnBehalfOnlineAgent: String? = null

    //For multiple quota
    @SerializedName("agent_types")
    @Expose
    var agent_types: String? = null

    @SerializedName("is_bima")
    @Expose
    var is_bima: Boolean? = null

    @SerializedName("remarks")
    @Expose
    var remarks: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null



}
