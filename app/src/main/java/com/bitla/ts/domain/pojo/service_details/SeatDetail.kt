package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SeatDetail {
    @SerializedName("available")
    @Expose
    var available: Boolean? = null

    @SerializedName("is_ladies_seat")
    @Expose
    var isLadiesSeat: Boolean? = null

    @SerializedName("is_gents_seat")
    @Expose
    var isGentsSeat: Boolean? = null

    @SerializedName("number")
    @Expose
    var number: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("fare")
    @Expose
    var fare: Any? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Double? = null

    @SerializedName("net_amount")
    @Expose
    var netAmount: Double? = null

    @SerializedName("row_id")
    @Expose
    var rowId: Int? = null

    @SerializedName("col_id")
    @Expose
    var colId: Int? = null

    @SerializedName("is_seat")
    @Expose
    var isSeat: Boolean? = null

    @SerializedName("is_gangway")
    @Expose
    var isGangway: Boolean? = null

    @SerializedName("is_horizontal")
    @Expose
    var isHorizontal: Boolean? = null

    @SerializedName("min_fare")
    @Expose
    var minFare: Double? = null

    @SerializedName("max_fare")
    @Expose
    var maxFare: Double? = null

    @SerializedName("background_color")
    @Expose
    var backgroundColor: String? = null


    //currentseat pojo keys
    @SerializedName("rowSpan")
    @Expose
    var rowSpan: Int? = null

    @SerializedName("isBerth")
    @Expose
    var isBerth: Boolean? = null

    @SerializedName("isReservable")
    @Expose
    var isReservable: Boolean? = null

    @SerializedName("isUpper")
    @Expose
    var isUpper: Boolean? = null

    @SerializedName("isBreak")
    @Expose
    var isBreak: Boolean? = null

    @SerializedName("is_lower")
    @Expose
    var isLower: Boolean? = null

    @SerializedName("floor_type")
    @Expose
    var floorType: String? = null

    @SerializedName("sex")
    @Expose
    var sex: String? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("cat_id")
    @Expose
    var cat_id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("is_social_distancing")
    @Expose
    var isSocialDistancing: Boolean = false

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean = false
}