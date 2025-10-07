package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.*
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import timber.log.*
import toast

fun manageLayouts(
    selectedBookingId: Int,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    role: String
) {
    when (selectedBookingId) {
        0 -> {
            if (passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true
                && role != null && !role.contains(context.getString(R.string.role_agent), true)
                && !role.contains(context.getString(R.string.role_field_officer), true)
            ) {
                if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOtherRoutes!!) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking!!)) {
                    privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel)
                    privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel)
                    privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel)

                } else {
                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
                }
            } else {
                if (role != null && role?.contains(context.getString(R.string.role_agent), true) == true
                    && passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)
                    && passengerDetailsViewModel.privilegeResponseModel?.allowToDoPhoneBlocking == true
                    && !passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
                ) {
                    passengerDetailsViewModel.setPhoneBookingVisibility(true)

                } else {
                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
                }
            }

            passengerDetailsViewModel.apply {
                setOnlineViewVisibility(false)
                setOfflineViewVisibility(false)
                setBranchViewVisible(false)
                setSubAgentViewVisible(false)
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!) {
                    setSatusViewVisible(false)
                    setPhoneDialogViewVisible(false)
                    setPhoneBlockDateTimeVisible(false)
                    phoneBlock = false
                }
            }
        }

        1 -> {
            if (passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true && role != null && !role!!.contains(
                    context.getString(R.string.role_agent),
                    true
                ) && !role!!.contains(context.getString(R.string.role_field_officer), true)
            ) {
//                if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOnlineAgentForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowPhoneBlockingTicketOnbehalfOnlineAgent == true)) {
//
//
//                } else {
//                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
//                }
                privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel)
//                privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel)
//                privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }

            passengerDetailsViewModel.apply {
                setOnlineViewVisibility(true)
                setOfflineViewVisibility(false)
                setBranchViewVisible(false)
                setSubAgentViewVisible(false)
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!) {
                    setSatusViewVisible(true)
                    setPhoneDialogViewVisible(false)
                    setPhoneBlockDateTimeVisible(false)
                    phoneBlock = false
                }
            }
        }

        2 -> {
            if (passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true
                && role != null && !role.contains(context.getString(R.string.role_agent), true)
                && !role.contains(context.getString(R.string.role_field_officer), true)
            ) {
//                if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOfflineAgentForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOfflineAgent == true)) {
//
//
//                } else {
//                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
//                }
                
//                privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel)
                privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel)
//                privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }

            passengerDetailsViewModel.apply {
                setOfflineViewVisibility(true)
                setOnlineViewVisibility(false)
                setBranchViewVisible(false)
                setSubAgentViewVisible(false)
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null && privilegeResponseModel?.allowToSwitchSinglePageBooking!!) {
                    setSatusViewVisible(true)
                    setPhoneDialogViewVisible(false)
                    setPhoneBlockDateTimeVisible(false)
                    phoneBlock = false
                }
            }
        }

        3 -> {
            if (passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true
                && role != null && !role.contains(context.getString(R.string.role_agent), true)
                && !role.contains(context.getString(R.string.role_field_officer), true)
            ) {
                if ((!passengerDetailsViewModel.isOwnRoute
                            && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOtherRoutes == true)
                    || (passengerDetailsViewModel.isOwnRoute
                            && passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking!!)
                ) {
                    privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel)
                    privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel)
                    privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel)
                } else {
                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
                }
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }

            passengerDetailsViewModel.apply {
                setOnlineViewVisibility(false)
                setOfflineViewVisibility(false)
                setBranchViewVisible(true)
                setSubAgentViewVisible(false)
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                    && privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                ) {
                    setSatusViewVisible(true)
                    setPhoneDialogViewVisible(false)
                    setPhoneBlockDateTimeVisible(false)
                    phoneBlock = false
                }
            }
        }

        4 -> {
            passengerDetailsViewModel.apply {
                setOnlineViewVisibility(false)
                setOfflineViewVisibility(false)
                setBranchViewVisible(false)
                if (isAgentLogin ||
                    (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                    && privilegeResponseModel?.allowToSwitchSinglePageBooking!!)
                ) {
                    setSatusViewVisible(false)
                    setPhoneDialogViewVisible(true)
                    setPhoneBlockDateTimeVisible(true)
                    setSubAgentViewVisible(false)
                }
            }
        }

        5 -> {
            passengerDetailsViewModel.apply {
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                    && privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                ) {
                    setSatusViewVisible(true)
                    setPhoneDialogViewVisible(true)
                    setPhoneBlockDateTimeVisible(true)
                    setSubAgentViewVisible(false)
                }
            }
        }

        6 -> {
            passengerDetailsViewModel.apply {
                if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                    && privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                ) {
                    setSatusViewVisible(true)
                    setPhoneDialogViewVisible(false)
                    setPhoneBlockDateTimeVisible(false)
                    setSubAgentViewVisible(false)
                    phoneBlock = false
                }
            }
        }


        33 -> {
            passengerDetailsViewModel.apply {
                setOfflineViewVisibility(false)
                setOnlineViewVisibility(false)
                setBranchViewVisible(false)
                setSatusViewVisible(false)
                setPhoneDialogViewVisible(false)
                setPhoneBlockDateTimeVisible(false)
                setSubAgentViewVisible(true)
                phoneBlock = false
            }
        }
    }
}

private fun privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    //online Agent
    
//    Timber.d("privilegesPhone==online--- ${passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id} and ${passengerDetailsViewModel.parentTravelId}")
    
    if (passengerDetailsViewModel.isBima != null
        && passengerDetailsViewModel.isBima==true
        && passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id == passengerDetailsViewModel.parentTravelId.toInt()
    ) {
        if (passengerDetailsViewModel.isAllowPhoneBlockingInBima && passengerDetailsViewModel.isAllowPhoneBlockingTicketOnbehalfOnlineAgentInBima){
            passengerDetailsViewModel.setPhoneBookingVisibility(true)
        } else {
            passengerDetailsViewModel.setPhoneBookingVisibility(false)
        }
    } else {
        if (passengerDetailsViewModel.selectedBookingTypeId == 1) {
            if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOnlineAgentForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowPhoneBlockingTicketOnbehalfOnlineAgent == true)) {
                if (!passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat })
                    passengerDetailsViewModel.setPhoneBookingVisibility(true)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }
        }
    }

}

private fun privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    // offline agent
    
//    Timber.d("privilegesPhone==offline--- ${passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id} and ${passengerDetailsViewModel.parentTravelId}")
    
    if (passengerDetailsViewModel.isBima != null
        && passengerDetailsViewModel.isBima==true
        && passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id == passengerDetailsViewModel.parentTravelId.toInt()
    ) {
        passengerDetailsViewModel.setPhoneBookingVisibility(false)
    } else {
        if (passengerDetailsViewModel.selectedBookingTypeId == 2) {
            if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOfflineAgentForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOfflineAgent == true)) {
                if (!passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat })
                    passengerDetailsViewModel.setPhoneBookingVisibility(true)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }
        }
    }

}

private fun privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    // branch & walkin (one more condition pending from API side)

//    Timber.d("parentTravelId_isBima-BookingType - ${passengerDetailsViewModel.parentTravelId} = ${passengerDetailsViewModel.isBima}")

    if (passengerDetailsViewModel.isBima != null
        && passengerDetailsViewModel.isBima==true
        && passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id == passengerDetailsViewModel.parentTravelId.toInt()
    ) {
//        Timber.d("allowPhoneBooking ${passengerDetailsViewModel.isAllowPhoneBlockingInBima}")
      /*  if (!passengerDetailsViewModel.isAllowPhoneBlockingInBima){
            passengerDetailsViewModel.setPhoneBookingVisibility(false)
        }*/
        
        if (passengerDetailsViewModel.selectedBookingTypeId == 3 ){
            passengerDetailsViewModel.setPhoneBookingVisibility(false)
        } else {
            if (passengerDetailsViewModel.selectedBookingTypeId == 0) {
                if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOtherRoutes == true) ||
                    (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.isAllowPhoneBlockingInBima)) {
                    if (!passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat })
                        passengerDetailsViewModel.setPhoneBookingVisibility(true)
                } else {
                    passengerDetailsViewModel.setPhoneBookingVisibility(false)
                }
            }
        }
        

    } else {
        if (passengerDetailsViewModel.selectedBookingTypeId == 3 || passengerDetailsViewModel.selectedBookingTypeId == 3 || passengerDetailsViewModel.selectedBookingTypeId == 0) {
            if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true)) {
                if (!passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat })
                    passengerDetailsViewModel.setPhoneBookingVisibility(true)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }
        }
    }

}

fun handlePrivileges(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>, role :String?, context: Context) {
    if (role == context.getString(R.string.role_agent) || role == context.getString(R.string.role_field_officer)) {
        passengerDetailsViewModel.setBookingTypeId(passengerDetailsViewModel.walkinId)

        val selectedBookingType =
            if (passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking != null && passengerDetailsViewModel.privilegeResponseModel!!.allowToSwitchSinglePageBooking)
                context.getString(R.string.confirmBooking)
            else
                context.getString(R.string.walkin)

        passengerDetailsViewModel.setBookingType(ResourceProvider.TextResource.fromText(selectedBookingType))
        if (selectedBookingType == context.getString(R.string.walkin)) {
            passengerDetailsViewModel.setBookingCardVisibility(false)
        }

        if (role == context.getString(R.string.role_agent)) {
            passengerDetailsViewModel.setBookingCardVisibility(true)
        }
    } else {
        passengerDetailsViewModel.setBookingCardVisibility(true)
    }

    if (passengerDetailsViewModel.selectedBookingType?.asString(context.resources) == context.getString(R.string.walkin)
        || passengerDetailsViewModel.selectedBookingType?.asString(context.resources) == context.getString(R.string.confirmBooking)) {

        if (passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking == true && role != null
            && !role.contains(context.getString(R.string.role_agent), true) && !role.contains(context.getString(R.string.role_field_officer), true)
        ) {
            if ((!passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.allowTimeBlockingForOtherRoutes == true) || (passengerDetailsViewModel.isOwnRoute && passengerDetailsViewModel.privilegeResponseModel?.isPhoneBooking!!)) {
                privilegesPhoneBookingForOnlineAgent(passengerDetailsViewModel)
                privilegesPhoneBookingForOfflineAgent(passengerDetailsViewModel)
                privilegesPhoneBookingForBranchAndWalkin(passengerDetailsViewModel)

            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
            }

        } else {
            if (passengerDetailsViewModel.roleType != null && role?.contains(context.getString(R.string.role_agent), true) == true
                && passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)
                && passengerDetailsViewModel.privilegeResponseModel?.allowToDoPhoneBlocking == true
            ) {
                passengerDetailsViewModel.setPhoneBookingVisibility(true)
            } else {
                passengerDetailsViewModel.setPhoneBookingVisibility(false)
                Timber.d("setPhoneBookingVisibility 13 role $role")
            }
        }
    }

    if (passengerDetailsViewModel.isBima != null
        && passengerDetailsViewModel.isBima==true
        && passengerDetailsViewModel.privilegeResponseModel?.chartSharedPrivilege?.get(0)?.parent_travel_id == passengerDetailsViewModel.parentTravelId.toInt()
    ) {
        if (!passengerDetailsViewModel.isAllowPhoneBlockingInBima){
            passengerDetailsViewModel.setPhoneBookingVisibility(false)
        }
    }
}