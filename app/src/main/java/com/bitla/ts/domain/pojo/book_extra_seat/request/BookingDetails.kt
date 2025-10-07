package com.bitla.ts.domain.pojo.book_extra_seat.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookingDetails {
    @SerializedName("ref_booking_number")
    @Expose
    var refBookingNumber: String? = null

    @SerializedName("remarks")
    @Expose
    var remarks: String? = null

    @SerializedName("agent_type")
    @Expose
    var agentType: String? = null

    @SerializedName("on_behalf_online_agent_value")
    @Expose
    var onBehalfOnlineAgentValue: String? = null

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null

    @SerializedName("on_behalf")
    @Expose
    var onBehalf: String? = null

    @SerializedName("onbehalf_paid")
    @Expose
    var onBehalfPaid: String? = null

    @SerializedName("on_behalf_branch")
    @Expose
    var onBehalfBranch: String? = null

    @SerializedName("on_behalf_user")
    @Expose
    var onBehalfUser: String? = null

    @SerializedName("is_redelcom_payment")
    @Expose
    var isRedelcomPayment: Boolean? = false

    @SerializedName("terminal_id")
    @Expose
    var terminalId: String? = ""

    @SerializedName("blocked_flag")
    @Expose
    var blockedFlag: Any? = null

    @SerializedName("is_pinelab_payment")
    @Expose
    var isPinelabPayment: Boolean? = false

    @SerializedName("pinelab_payment_type")
    @Expose
    var pinelabPaymentType: Int? = 0

    @SerializedName("is_ezetap_payment")
    @Expose
    var isEzetapPayment: Boolean? = false

    @SerializedName("ezetap_device_id")
    @Expose
    var ezetapDeviceId: String? = ""
    
    @SerializedName("agent_payment_type")
    @Expose
    var agentPaymentType: String? = ""
    
    @SerializedName("agent_sub_payment_type")
    @Expose
    var agentSubPaymentType: String? = ""
    
    @SerializedName("agent_phone")
    @Expose
    var agentPhone: String? = null
    
    @SerializedName("agent_vpa")
    @Expose
    var agentVpa: String? = null


    @SerializedName("is_paytm_pos_payment")
    @Expose
    var isPaytmPayment: Boolean? = false

    @SerializedName("payment_type_config")
    @Expose
    var paymentTypeConfig: String? = null

    @SerializedName("sub_payment_type")
    @Expose
    var subPaymentType: String? = ""

    @SerializedName("branch_vpa")
    @Expose
    var branchVpa: String? = ""

    @SerializedName("branch_phone")
    @Expose
    var branchPhone: String? = ""

    @SerializedName("credit_transaction_number")
    @Expose
    var creditTransactionNumber: String? = null

    @SerializedName("pay_gay_type")
    @Expose
    var payGayType: Int? = null
}