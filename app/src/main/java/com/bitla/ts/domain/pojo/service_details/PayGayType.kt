package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PayGayType {
    @SerializedName("pay_gay_type_id")
    @Expose
    var payGayTypeId: String? = null

    @SerializedName("pay_gay_type_name")
    @Expose
    var payGayTypeName: String? = null

    @SerializedName("transaction_type")
    @Expose
    var transactionType: String? = null

    @SerializedName("transaction_charges")
    @Expose
    var transactionCharges: Double? = null

}