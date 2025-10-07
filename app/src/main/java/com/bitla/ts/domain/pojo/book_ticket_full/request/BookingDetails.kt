package com.bitla.ts.domain.pojo.book_ticket_full.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookingDetails {
    @SerializedName("remarks")
    @Expose
    var remarks: String? = null

    @SerializedName("is_free_booking_allowed")
    @Expose
    var isFreeBookingAllowed: String? = null

    @SerializedName("is_vip_ticket")
    @Expose
    var isVipTicket: String? = null

    @SerializedName("is_pinelab_payment")
    @Expose
    var isPinelabPayment: Boolean? = false

    @SerializedName("pinelab_payment_type")
    @Expose
    var pinelabPaymentType: Int? = 0

    @SerializedName("vip_booking_category")
    @Expose
    var vipBookingCategory: String? = null

    @SerializedName("discount_on_total_amount")
    @Expose
    var discountOnTotalAmount: String? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: String? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("promotion_coupon")
    @Expose
    var promotionCoupon: String? = null

    @SerializedName("agent_type")
    @Expose
    var agentType: String? = null

    @SerializedName("smart_miles_hash")
    @Expose
    var smartMilesHash: SmartMilesHash? = null

    @SerializedName("privilege_card_hash")
    @Expose
    var privilegeCardHash: PrivilegeCardHash? = null

    @SerializedName("pre_post_pone_pnr")
    @Expose
    var prePostPonePnr: String? = null

    @SerializedName("is_match_prepostpone_amount")
    @Expose
    var isMatchPrepostponeAmount: String? = null

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null

    @SerializedName("previous_pnr_number")
    @Expose
    var previousPnrNumber: String? = null

    @SerializedName("phone_number")
    @Expose
    var phoneNumber: String? = null

    @SerializedName("credit_transaction_number")
    @Expose
    var creditTransactionNumber: String? = null

    @SerializedName("blocked_flag")
    @Expose
    var blockedFlag: Any? = null
    @SerializedName("permanent_blocked_flag")
    @Expose
    var permanentBlockedFlag: String? = null

    @SerializedName("blocking_date")
    @Expose
    var blockingDate: String? = null

    @SerializedName("blocking_time_hours")
    @Expose
    var blockingTimeHours: String? = null

    @SerializedName("blocking_time_mins")
    @Expose
    var blockingTimeMins: String? = null

    @SerializedName("blocking_am_pm")
    @Expose
    var blockingAmPm: String? = null

    @SerializedName("on_behalf_online_agent_value")
    @Expose
    var onBehalfOnlineAgentValue: String? = null

    @SerializedName("on_behalf_branch")
    @Expose
    var onBehalfBranch: String? = null

    @SerializedName("on_behalf_user")
    @Expose
    var onBehalfUser: String? = null

    @SerializedName("on_behalf")
    @Expose
    var onBehalf: String? = null

    @SerializedName("onbehalf_paid")
    @Expose
    var onBehalfPaid: String? = null

    @SerializedName("ref_booking_number")
    @Expose
    var refBookingNumber: String? = ""

    @SerializedName("payment_type_config")
    @Expose
    var paymentTypeConfig: String? = null

    @SerializedName("wallet_payment_hash")
    @Expose
    var walletPaymentHash: WalletPaymentHash? = null

    @SerializedName("is_bima_ticket")
    @Expose
    var isBimaTicket: Boolean? = null

    @SerializedName("upi_payment_hash")
    @Expose
    var upiPaymentHash: UpiPaymentHash? = null

    @SerializedName("upi_direct_payment_hash")
    @Expose
    var upiDirectPaymentHash: UpiDirectPaymentHash? = null

    @SerializedName("is_redelcom_payment")
    @Expose
    var isRedelcomPayment: Boolean? = false

    @SerializedName("terminal_id")
    @Expose
    var terminalId: String? = ""


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

    @SerializedName("sub_payment_type")
    @Expose
    var subPaymentType: String? = ""

    @SerializedName("branch_vpa")
    @Expose
    var branchVpa: String? = ""

    @SerializedName("branch_phone")
    @Expose
    var branchPhone: String? = ""

    @SerializedName("pickup_address")
    @Expose
    var pickupAddress: String? = ""

    @SerializedName("dropoff_address")
    @Expose
    var dropoffAddress: String? = ""

    @SerializedName("pay_gay_type")
    @Expose
    var payGayType: Int? = null
}