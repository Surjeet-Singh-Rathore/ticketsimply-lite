package com.bitla.ts.domain.pojo.privilege_details_model.response.child_model


import com.google.gson.annotations.SerializedName

data class BoLicenses(
    @SerializedName("Allot Services To User")
    val allotServicesToUser: Boolean,
    @SerializedName("Allow Auto Discount")
    val allowAutoDiscount: Boolean,
    @SerializedName("Allow Booking For All Services")
    val allowBookingForAllServices: Boolean,
    @SerializedName("Allow Booking For Alloted Services")
    val allowBookingForAllotedServices: Boolean,
    @SerializedName("Allow Branch Discount for Booking")
    val allowBranchDiscountForBooking: Boolean,
    @SerializedName("Allow Discount While Booking")
    val allowDiscountForBooking: Boolean,
    @SerializedName("Allow Bus Operator App")
    val allowBusOperatorApp: Boolean,
    @SerializedName("Allow Call")
    val allowCall: Boolean,
    @SerializedName("Allow field officers to select different Services/Coaches while doing fuel filling")
    val allowFieldOfficersToSelectDifferentServicesCoachesWhileDoingFuelFilling: Boolean,
    @SerializedName("Allow to scan and update the Boarding status in Rapid/MOT booking page for TS App")
    val AllowToScanAndUpdateTheBoardingStatusInRapidMOTBookingPageForTSApp: Boolean? = false,
    @SerializedName("Allow Layout Chart Print")
    val allowLayoutChartPrint: Boolean,
    @SerializedName("Allow Luggage")
    val allowLuggage: Boolean,
    @SerializedName("Allow Modify")
    val allowModify: Boolean,
    @SerializedName("Allow Other Details in Update Ticket")
    val allowOtherDetailsInUpdateTicket: Boolean,
    @SerializedName("Allow PNR Search")
    val allowPNRSearch: Boolean,
    @SerializedName("Allow Passenger Search")
    val allowPassengerSearch: Boolean,
    @SerializedName("Allow Rapid Booking Flow")
    val allowRapidBookingFlow: Boolean,
    @SerializedName("Allow Reprint")
    val allowReprint: Boolean,
    @SerializedName("Allow SMS")
    val allowSMS: Boolean,
    @SerializedName("Allow Status")
    val allowStatus: Boolean,
    @SerializedName("Allow To Close Pickup By City")
    val allowToClosePickupByCity: Boolean,
    @SerializedName("Allow To Update The Fuel Filling Details From Bus Mobiity App")
    val allowToUpdateTheFuelFillingDetailsFromBusMobiityApp: Boolean,
    @SerializedName("Allow to view the coach document")
    val allowToViewTheCoachDocument: Boolean,
    @SerializedName("Allow Updation of Fare in Update Ticket")
    val allowUpdationOfFareInUpdateTicket: Boolean,
    @SerializedName(" Allow Updation Of Fare Of Phone Blocked Ticket")
    val allowUpdationOfFareOfPhoneBlockedTicket: Boolean,
    @SerializedName("Change Password")
    val changePassword: Boolean,
    @SerializedName("Show Booking mode")
    val showBookingMode: Boolean,
    @SerializedName("Show Checking Inspector Mode")
    val showCheckingInspectorMode: Boolean,
    @SerializedName("Show Checking Inspector Report")
    val showCheckingInspectorReport: Boolean,
    @SerializedName("Show Dispatch Manager Mode")
    val showDispatchManagerMode: Boolean,
    @SerializedName("Show Driver Mode")
    val showDriverMode: Boolean,
    @SerializedName("Show Field Collection Mode")
    val showFieldCollectionMode: Boolean,
    @SerializedName("Show Notifications")
    val showNotifications: Boolean,
    @SerializedName("Show Reports")
    val showReports: Boolean,
    @SerializedName("Show Settings")
    val showSettings: Boolean,
    @SerializedName("Update Passenger Travel Status")
    val updatePassengerTravelStatus: Boolean?=null,
    @SerializedName("Allow User to Change The Boarding Status Only Once")
    val allowUserToBoardingStatusOnlyOnce: Boolean,
    @SerializedName("Allow User to Change the Check-In Status to Boarded Status Only")
    val allowUserToChangeCheckInStatusToBoardedOnly: Boolean?=null,
    @SerializedName("Show booking and collection tab in TS app")
    val showBookingAndCollectionTabInTsApp: Boolean=false,
    @SerializedName("Hide fare for booked seats in the single booking page for the user")
    val hideFareForBookedSeatsInTheSingleBookingPageForTheUser: Boolean?=false,
    @SerializedName("Hide fare for blocked seats in the single booking page for the user")
    val hidefareForBlockedSeatsInTheSingleBookingPageForTheUser: Boolean?=false,
    @SerializedName("Trip Sheet Collection Option In TS App Reservation Chart")
    val tripSheetCollectionOptionInTSAppReservationChart: Boolean? = false,
    @SerializedName("Allow To Update Vehicle Expenses")
    val allowToUpdateVehicleExpenses: Boolean? = false,
    @SerializedName("Allow to view vehicle document option")
    val allowToViewVehicleDocumentOption: Boolean? = false,
)