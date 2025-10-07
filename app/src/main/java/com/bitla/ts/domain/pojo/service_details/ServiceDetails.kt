package com.bitla.mba.morningstartravels.mst.pojo.service_details

import com.bitla.ts.domain.pojo.cancellation_policies.CancellationPolicyModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ServiceDetails {
    @SerializedName("number")
    @Expose
    var number: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("origin")
    @Expose
    var origin: Origin? = null

    @SerializedName("destination")
    @Expose
    var destination: Destination? = null

    @SerializedName("travel_date")
    @Expose
    var travelDate: String? = null

    @SerializedName("route_id")
    @Expose
    var routeId: Int? = null

    @SerializedName("phone_blocking_hour")
    @Expose
    var phoneBlockingHour: String? = null

    @SerializedName("available_seats")
    @Expose
    var availableSeats: Int? = null

    @SerializedName("dep_time")
    @Expose
    var depTime: String? = null

    @SerializedName("pay_gay_type")
    @Expose
    var payGayType: MutableList<PayGayType>? = null

    @SerializedName("arr_time")
    @Expose
    var arrTime: String? = null

    @SerializedName("duration")
    @Expose
    var duration: String? = null

    @SerializedName("bus_type")
    @Expose
    var busType: String? = null

    @SerializedName("via")
    @Expose
    var via: Any? = null

    @SerializedName("e_ticket_discount")
    @Expose
    var eTicketDiscount: String? = null

    @SerializedName("cost")
    @Expose
    var cost: String? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("is_child_fare")
    @Expose
    var isChildFare: Boolean? = null

    @SerializedName("coach_details")
    @Expose
    var coachDetails: CoachDetails? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("stage_details")
    @Expose
    var stageDetails: MutableList<StageDetail>? = null

    @SerializedName("cancellation_policies")
    @Expose
    var cancellationPolicies: MutableList<CancellationPolicyModel>? =
        null

    @SerializedName("legend_details")
    @Expose
    var legendDetails: Map<Any, Any>? = null
    //var legendDetails: LegendDetails? = null

    @SerializedName("intr_legend_details")
    @Expose
    var intrLegendDetails: IntrLegendDetails? = null

    @SerializedName("is_hotel_lead_privilege")
    @Expose
    var isHotelLeadPrivilege: Boolean? = null

    @SerializedName("is_pick_up_lead_privilege")
    @Expose
    var isPickUpLeadPrivilege: Boolean? = null

    @SerializedName("is_drop_off_lead_privilege")
    @Expose
    var isDropOffLeadPrivilege: Boolean? = null

    @SerializedName("is_day_visit_lead_privilege")
    @Expose
    var isDayVisitLeadPrivilege: Boolean? = null

    @SerializedName("is_service_tax_applicable")
    @Expose
    var isServiceTaxApplicable: Boolean? = null

    @SerializedName("is_gst_applicable")
    @Expose
    var isGstApplicable: Boolean? = null

    @SerializedName("is_coach_layout_hide")
    @Expose
    var isCoachLayoutHide: Boolean? = null

    /* @SerializedName("st_percent")
     @Expose
     var stPercent: Double? = null*/

    @SerializedName("convenience_charge_percent")
    @Expose
    var convenienceChargePercent: Double? = null

    @SerializedName("do_not_apply_eticket_discount")
    @Expose
    var doNotApplyEticketDiscount: Boolean? = null

    @SerializedName("id_types_arr")
    @Expose
    var idTypesArr: List<List<String>>? = null

    @SerializedName("nationality_list")
    @Expose
    var nationalityList: List<List<String>>? = null

    @SerializedName("all_fare_details")
    @Expose
    var allFareDetails: MutableList<Int>? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("social_distancing_guaranteed")
    @Expose
    var isSocialDistancingGuaranteed: Boolean = false
}