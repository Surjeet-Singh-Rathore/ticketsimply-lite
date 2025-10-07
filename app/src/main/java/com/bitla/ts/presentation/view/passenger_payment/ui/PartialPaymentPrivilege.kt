package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.common.getTodayDate
import timber.log.Timber

fun handlePartialPaymentPrivilege(context: Context,passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,onPartialPaymentChange : ()-> Unit) {

    if (passengerDetailsViewModel.privilegeResponseModel != null) {
            if (passengerDetailsViewModel.privilegeResponseModel?.availableAppModes?.allowToDoPartialPayment == true && !passengerDetailsViewModel.isExtraSeat && !(passengerDetailsViewModel.phoneBlock) && !passengerDetailsViewModel.isInsuranceChecked.value
            ) {
                hidePartialPaymentInfo(passengerDetailsViewModel)
                passengerDetailsViewModel.isFullPartialCardVisible = true

                if (passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType != null) {
                    if (passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType == "1") {
                        val partialPercentValue = passengerDetailsViewModel.getPartialPercent()
                        passengerDetailsViewModel.partialPercentValue = partialPercentValue.toString()
                    } else {
                        passengerDetailsViewModel.partialPercentValue = passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue ?: ""
                    }
                }


                val boldPartialAmt =
                    SpannableStringBuilder().append("${context.getString(R.string.total_amount)}:")
                        .bold { append(" ${passengerDetailsViewModel.privilegeResponseModel?.currency} ${passengerDetailsViewModel.totalFareString.toDouble().convert(passengerDetailsViewModel.currencyFormat)}") }
                passengerDetailsViewModel.fullPartialTotalAmount = boldPartialAmt.toString()

                //passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue = null

                if (passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitType == "1")
                    passengerDetailsViewModel.partialAmount =  passengerDetailsViewModel.getPartialPercent()
                else
                {
                    passengerDetailsViewModel.partialAmount = if (!passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue.isNullOrEmpty())
                        passengerDetailsViewModel.privilegeResponseModel?.partialPaymentLimitValue?.toDouble() ?: 0.0
                    else
                        0.0
                }

                passengerDetailsViewModel.pendingAmount = passengerDetailsViewModel.totalFareString.toDouble().minus(passengerDetailsViewModel.partialAmount)
                val boldPartialAmtAmt =
                    SpannableStringBuilder().append("${context.getString(R.string.remaining_amount)}:")
                        .bold { append(" ${passengerDetailsViewModel.privilegeResponseModel?.currency} ${passengerDetailsViewModel.pendingAmount}") }
                passengerDetailsViewModel.fullPartialRemainingAmount = boldPartialAmtAmt.toString()

                releaseRadioListener(context,passengerDetailsViewModel)
                onPartialPaymentChange()
            } else
                passengerDetailsViewModel.isFullPartialCardVisible = false
        }
}

fun releaseRadioListener(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
) {
    if (passengerDetailsViewModel.selectedPartialPayment.asString(resources = context.resources) == context.getString(R.string.do_not_release))
    {
        passengerDetailsViewModel.isShowReleaseDate = false
        passengerDetailsViewModel.isShowReleaseTime = false
        passengerDetailsViewModel.partialType = "1"
    }
    else{
        passengerDetailsViewModel.partialBlockingDate = getTodayDate()
        passengerDetailsViewModel.isShowReleaseDate = true
        passengerDetailsViewModel.isShowReleaseTime = true
        passengerDetailsViewModel.partialType = "2"
    }
}


fun showPartialPaymentInfo(passengerDetailsViewModel : PassengerDetailsViewModel<Any?>) {
    passengerDetailsViewModel.isPartialPaymentInfoVisible = true
}



fun hidePartialPaymentInfo(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    passengerDetailsViewModel.isPartialPaymentInfoVisible = false
}
