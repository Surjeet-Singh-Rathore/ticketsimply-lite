package com.example.buscoach.service_details_response


import android.view.View
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SeatDetail: Serializable {
    @SerializedName("available")
    var available: Boolean? = null

    @SerializedName("is_ladies_seat")
    var isLadiesSeat: Boolean? = null

    @SerializedName("is_gents_seat")
    var isGentsSeat: Boolean? = null

    @SerializedName("number")
    var number: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("fare")
    var fare: Any? = null

    @SerializedName("is_apply_to_all")
    var isApplyToAll: Boolean? = false

    @SerializedName("edit_fare")
    var editFare: Any? = null

    @SerializedName("discount_amount")
    var discountAmount: Double? = 0.0

    @SerializedName("net_amount")
    var netAmount: Double? = null

    @SerializedName("row_id")
    var rowId: Int? = null

    @SerializedName("col_id")
    var colId: Int? = null

    @SerializedName("is_seat")
    var isSeat: Boolean? = null

    @SerializedName("is_gangway")
    var isGangway: Boolean? = null

    @SerializedName("is_horizontal")
    var isHorizontal: Boolean? = false

    @SerializedName("is_blocked_seat")
    var isBlocked: Boolean? = null

    @SerializedName("min_fare")
    var minFare: Double? = null

    @SerializedName("max_fare")
    var maxFare: Double? = null

    @SerializedName("background_color")
    var backgroundColor: String? = null

    @SerializedName("remarks")
    var remarks: String? = null

    //currentseat pojo keys
    @SerializedName("rowSpan")
    var rowSpan: Int? = null

    @SerializedName("isBerth")
    var isBerth: Boolean? = false

    @SerializedName("isReservable")
    var isReservable: Boolean? = null

    @SerializedName("isUpper")
    var isUpper: Boolean? = null

    @SerializedName("isBreak")
    var isBreak: Boolean? = null

    @SerializedName("is_lower")
    var isLower: Boolean? = null

    @SerializedName("floor_type")
    var floorType: String? = null

    @SerializedName("sex")
    var sex: String? = null

    @SerializedName("age")
    var age: String? = null

    @SerializedName("cat_id")
    var cat_id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("is_social_distancing")
    var isSocialDistancing: Boolean = false

    @SerializedName("is_selected")
    var isSelected: Boolean = false

    @SerializedName("seat_status_data")
    var seatStatusData: SeatStatusData? = null

    @SerializedName("seat_count")
    var seatCount: Int? = null

    @SerializedName("passenger_details")
    var passengerDetails: PassengerDetails? = null

    @SerializedName("base_fare_filter")
    var baseFareFilter: Any? = null

//    @SerializedName("passenger_details")
//    var passengerDetails: MutableList<PassengerDetails>? = null

/*    @Expose
    @Transient
    @SerializedName("seat_view")
    var seatView: View? = null*/

    @SerializedName("additional_fare")
    var additionalFare: Double? = 0.0

    @SerializedName("is_primary")
    @Expose
    var isPrimary: Boolean? = null

    @SerializedName("id_card_type")
    var idCardType: Int? = 0

    @SerializedName("id_card_number")
    var idCardNumber: String? = ""

    @SerializedName("passport_issued_date")
    var passportIssuedDate: String? = ""

    @SerializedName("passport_expiry_date")
    var passportExpiryDate: String? = ""

    @SerializedName("place_of_issue")
    var placeOfIssue: String? = ""

    @SerializedName("nationality")
    var nationality: String? = ""

    @SerializedName("is_extra_seat")
    var isExtraSeat: Boolean = false


    @SerializedName("is_shifted")
    var isShifted: Boolean? = false

    @SerializedName("is_boarded")
    var isBoarded: Boolean = false

    @SerializedName("is_updated")
    var isUpdated: Boolean? = false

    @SerializedName("is_multi_hop")
    var isMultiHop: Boolean? = false

    @SerializedName("is_in_journey")
    var isInJourney: Boolean? = false

    @SerializedName("applied_fare")
    var appliedFare: Any? = null

    @SerializedName("updated_fare")
    var updatedFare: Any? = null

    @SerializedName("meal_required")
    var mealRequired: Boolean = false
    @SerializedName("selected_meal_type")
    var selectedMealType: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    var isimage: Boolean? = false

    @SerializedName("seat_discount")
    var seatDiscount: Any? = null

    var isEditFareApply: Boolean? = false

    @SerializedName("multi_hop_pnr")
    var otherPnrNumber: MutableList<String?>? = null

/*
    var editFareMap: MutableMap<String, String> = mutableMapOf()
*/
}
