package com.bitla.ts.domain.pojo.instant_recharge

import com.bitla.ts.domain.pojo.pay_bitla.TransactionData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AgentPGDataResponse (
    val merchant_id: String,
    val salt: String,
    val payment_link: String,
    val salt_index: String,
    val callback_url: String,
    val pnr_number: String,
    val passenger_name: String,
    val email_id: String,
    val phone_no: String,
    val amount: Any,
    val redirect_url: String,
    val is_phonepe_direct: Boolean,
    val code: Int,
    val message: String? = "",


    val is_razorpay_payment: Boolean,
    val is_paybitla_payment:Boolean,
    val payment_id:String,
    val order_id: String,
    val customerTransactionId: String,
    val secretKey: String,
    val wallet_sel: String= "",

    val result:Result? = null,
    val access_key : String? = null,
    val is_easebuzz_from_mobile_app: Boolean,


    val txn_status: TransactionData? = null,





    val currency: String,
    val billing_cust_name: String,
    val address: String?,
    val billing_cust_address: String?,
    val billing_cust_country: String,
    val billing_cust_tel: String,
    val billing_cust_email: String,
    val billing_cust_notes: String,
    val merchant_Param: String,
    val from: String,
    val to: String,
    val preferred_seats: String,
    val checksum: String,
    val pay_mode: String?, // Change to appropriate type if known
    val user_key: String,
    val password_key: String,
    val referral_url_ts: String,
    val agent_recharge: String,
    // Change to appropriate type if known

    // PhonePe V2
    val is_phonepe_v2_payment: Boolean = false,
    val token: String,
    @SerializedName("orderId")
    val orderIdPhonePe: String,
    var is_live_environment: Boolean?,
)

data class Result(
    val transaction_charge: Double? = null,
    val net_amount: Double? = null
)