package com.bitla.ts.domain.pojo.add_driver.request

import com.google.gson.annotations.SerializedName

data class Driver(
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("mobile_number")
    val mobileNumber: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("sex")
    val sex: String? = null,
    @SerializedName("date_of_birth")
    val dateOfBirth: String? = null,
    @SerializedName("pin_code")
    val pinCode: String? = null,
    @SerializedName("esi")
    val esi: String? = null,
    @SerializedName("pf")
    val pf: String? = null,
    @SerializedName("uan_no")
    val uanNo: String? = null,
    @SerializedName("address_line1")
    val addressLine1: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("micr_code")
    val micrCode: String? = null,
    @SerializedName("account_number")
    val accountNumber: String? = null,
    @SerializedName("account_type")
    val accountType: String? = null,
    @SerializedName("ifsc_code")
    val ifscCode: String? = null,
    @SerializedName("bank_name")
    val bankName: String? = null,
    @SerializedName("beneficiary_name")
    val beneficiaryName: String? = null,
    @SerializedName("branch_name")
    val branchName: String? = null,
    @SerializedName("dl_expiry_date")
    val dlExpiryDate: String? = null,
    @SerializedName("driver_licence_issuing_authority")
    val driverLicenceIssuingAuthority: String? = null,
    @SerializedName("driving_licence")
    val drivingLicence: String? = null,
    @SerializedName("badge_no")
    val badgeNo: String? = null,
    @SerializedName("travel_branch")
    val travelBranch: String? = null,
    @SerializedName("payment_type")
    val paymentType: String? = null,
    @SerializedName("response_format")
    val responseFormat: String? = null,
)