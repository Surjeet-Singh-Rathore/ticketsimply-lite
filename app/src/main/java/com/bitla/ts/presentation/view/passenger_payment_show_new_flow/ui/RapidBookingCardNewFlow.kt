package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import androidx.compose.runtime.*
import com.bitla.ts.presentation.viewModel.*

@Composable
fun RapidBookingBookingTypeCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onRapidBookingCheck: (Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        passengerDetailsViewModel.rapidBookingSkip = false
        onRapidBookingCheck(false)
    }

}