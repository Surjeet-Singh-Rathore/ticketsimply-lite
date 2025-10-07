package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class CurrencyType(
    @SerializedName("currency_name")
    val currencyName: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    @SerializedName("currency_type")
    val currencyType: String,
    @SerializedName("currency_value")
    val currencyValue: Any,
    @SerializedName("to_currency")
    val toCurrency: String
)