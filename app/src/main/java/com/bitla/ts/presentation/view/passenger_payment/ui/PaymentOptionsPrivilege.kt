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
    passengerDetailsViewModel.selectedPaymentOptionId = 1
    passengerDetailsViewModel.selectedPaymentOption =
        ResourceProvider.TextResource.fromStringId(R.string.cash)
    passengerDetailsViewModel.isPaymentOptionCardVisible=false

}