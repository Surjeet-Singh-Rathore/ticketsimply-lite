package com.bitla.ts.domain.pojo.photo_block_tickets.request


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.*
import org.json.JSONObject

data class ReqBody(
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("payment_type")
    val paymentType: Int,
    @SerializedName("pnr_number")
    val pnrNumber: String,
    @SerializedName("ticket")
    val ticket: Ticket,
    @SerializedName("travel_branch")
    val travelBranch: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("is_from_middle_tier")
    val isFromMiddleTier: Boolean = true,
    var locale: String?,
    @SerializedName("agent_payment_type")
    val agentPaymentType: String? = null,
    @SerializedName("agent_sub_payment_type")
    val agentSubPaymentType: String? = null,
    @SerializedName("agent_phone")
    var agentPhone: String? = null,
    @SerializedName("agent_vpa")
    var agentVpa: String? = null,
    @SerializedName("sub_payment_type")
    var subPaymentType: String? = "",
    @SerializedName("branch_vpa")
    var branchVpa: String? = "",
    @SerializedName("boarding_point_id")
    var boardingPointId: String? = "",
    @SerializedName("passenger_details")
    var passengerDetails: JsonArray? = null,
    @SerializedName("on_behalf_branch")
    var onbehalfBranch: String? = "",
    @SerializedName("on_behalf_user")
    var onbehalfUser: String? = "",
    @SerializedName("on_behalf_online_agent_value")
    var onBehalfOnlineAgentValue: String? = "",
    @SerializedName("on_behalf")
    var onBehalf: String? = "",
    @SerializedName("agent_type")
    var agentType: String? = "",
    @SerializedName("payment_type_config")
    var paymentTypeConfig: String? = "",


    var branchPhone: String? = "",
    @SerializedName("pay_gay_type")
    var payGayType: Int? = null,
    @SerializedName("device_info")
    var deviceInfo: String? = null,
)