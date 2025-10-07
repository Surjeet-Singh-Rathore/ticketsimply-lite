package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*

@Composable
fun RapidBookingBookingTypeCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onRapidBookingCheck: (Boolean) -> Unit,
) {
    Row(
        Modifier.wrapContentHeight()
    ) {
        Checkbox(
            checked = passengerDetailsViewModel.rapidBookingSkip,
            onCheckedChange = {
                onRapidBookingCheck(it)
            }
        )
        
        TextBoldSmall(
            text = stringResource(id = R.string.skip_passenger_details).uppercase(),
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Start
        )
    }
}