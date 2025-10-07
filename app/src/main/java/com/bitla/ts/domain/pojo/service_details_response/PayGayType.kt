package com.bitla.ts.domain.pojo.service_details_response


import com.google.gson.annotations.SerializedName


class PayGayType {

    @SerializedName("pay_gay_type_id")
    var payGayTypeId: String? = null

    @SerializedName("pay_gay_type_name")
    var payGayTypeName: String? = null

    @SerializedName("transaction_type")
    var transactionType: String? = null

    @SerializedName("transaction_charges")
    var transactionCharges: Int? = null

}
