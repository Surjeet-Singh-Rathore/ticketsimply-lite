package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import asString
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import toast

fun setPaymentOptionsVisibility(context: Context,passengerDetailsViewModel: PassengerDetailsViewModel<Any?>)
{
    val privileges = (context as BaseActivity).getPrivilegeBase()
    if (passengerDetailsViewModel.roleType?.contains(context.getString(R.string.role_agent), true) == true && !passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents
        || (passengerDetailsViewModel.selectedBookingType?.asString(context.resources) == "1") || passengerDetailsViewModel.paymentOptionsList.isEmpty()
    ) {
        passengerDetailsViewModel.selectedPaymentOptionId = 0
        passengerDetailsViewModel.selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.empty)
        passengerDetailsViewModel.isPaymentOptionCardVisible = false
    }
    else if (passengerDetailsViewModel.roleType?.contains(context.getString(R.string.role_agent), true) == true
        && passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents
    ) {
        passengerDetailsViewModel.selectedPaymentOptionId = 1
        passengerDetailsViewModel.selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)
        passengerDetailsViewModel.isPaymentOptionCardVisible = true
        
    }
    else {
        passengerDetailsViewModel.selectedPaymentOptionId = 1
        passengerDetailsViewModel.selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)

        if(privileges?.isEzetapEnabledInTsApp == true && !passengerDetailsViewModel.rapidBookingSkip  && !passengerDetailsViewModel.isFreeTicketChecked.value){

            if (!passengerDetailsViewModel.isVIPTicketChecked.value || !privileges.isVipAFreeBooking) {
                passengerDetailsViewModel.selectedPaymentOptionId = 14
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.ezetap)

            }
        }


        if(privileges?.isPaytmPosEnabled == true && !passengerDetailsViewModel.rapidBookingSkip  && !passengerDetailsViewModel.isFreeTicketChecked.value){

            if (!passengerDetailsViewModel.isVIPTicketChecked.value || !privileges.isVipAFreeBooking) {
                passengerDetailsViewModel.selectedPaymentOptionId = 14
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.paytm)

            }

        }


        if (passengerDetailsViewModel.phoneBlock ) {
            passengerDetailsViewModel.isPaymentOptionCardVisible = false
        } else if(privileges?.isEzetapEnabledInTsApp == true &&( (passengerDetailsViewModel.isVIPTicketChecked.value && privileges.isVipAFreeBooking) || passengerDetailsViewModel.isFreeTicketChecked.value)){
            passengerDetailsViewModel.isPaymentOptionCardVisible = false
        }
        else if(privileges?.isPaytmPosEnabled == true &&( (passengerDetailsViewModel.isVIPTicketChecked.value && privileges.isVipAFreeBooking) || passengerDetailsViewModel.isFreeTicketChecked.value)){
            passengerDetailsViewModel.isPaymentOptionCardVisible = false
        }else if (passengerDetailsViewModel.selectedExtraSeatDetails.size > 0) {
            passengerDetailsViewModel.isPaymentOptionCardVisible = true
        } else if (privileges?.isEzetapEnabledInTsApp == true && !passengerDetailsViewModel.rapidBookingSkip) {




            if(passengerDetailsViewModel.selectedBookingTypeId == 1 || passengerDetailsViewModel.selectedBookingTypeId == 2){
                passengerDetailsViewModel.selectedPaymentOptionId = 1
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.cash)
                passengerDetailsViewModel.isPaymentOptionCardVisible=false

            }else{
                passengerDetailsViewModel.selectedPaymentOptionId = 14
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.ezetap)
                passengerDetailsViewModel.isPaymentOptionCardVisible=true
            }
        }else if (privileges?.isPaytmPosEnabled == true ) {
            if(passengerDetailsViewModel.selectedBookingTypeId == 1 || passengerDetailsViewModel.selectedBookingTypeId == 2 ||  passengerDetailsViewModel.rapidBookingSkip){

                passengerDetailsViewModel.selectedPaymentOptionId = 1
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.cash)
                passengerDetailsViewModel.isPaymentOptionCardVisible=false
            }else{
                passengerDetailsViewModel.selectedPaymentOptionId = 14
                passengerDetailsViewModel.selectedPaymentOption =
                    ResourceProvider.TextResource.fromStringId(R.string.paytm)
                passengerDetailsViewModel.isPaymentOptionCardVisible=true
            }
        }
        else passengerDetailsViewModel.isPaymentOptionCardVisible = !passengerDetailsViewModel.isExtraSeat
    }
}