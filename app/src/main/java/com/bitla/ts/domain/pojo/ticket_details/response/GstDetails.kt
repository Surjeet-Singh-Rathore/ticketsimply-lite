package com.bitla.ts.domain.pojo.ticket_details.response


import com.google.gson.annotations.SerializedName

data class GstDetails(
    @SerializedName("booking_amount")
    val bookingAmount: Double,
    @SerializedName("category")
    val category: Boolean,
    @SerializedName("cgst_amount")
    val cgstAmount: Double,
    @SerializedName("cgst_percentage")
    val cgstPercentage: Double,
    @SerializedName("gst_amount")
    val gstAmount: Double,
    @SerializedName("gst_id")
    val gstId: String,
    @SerializedName("gst_percentage")
    val gstPercentage: Double,
    @SerializedName("igst_amount")
    val igstAmount: Double,
    @SerializedName("igst_percentage")
    val igstPercentage: Double,
    @SerializedName("registeration_name")
    val registerationName: String,
    @SerializedName("sale_type")
    val saleType: String,
    @SerializedName("sgst_amount")
    val sgstAmount: Double,
    @SerializedName("sgst_percentage")
    val sgstPercentage: Double,
    @SerializedName("trans_type")
    val transType: String
)