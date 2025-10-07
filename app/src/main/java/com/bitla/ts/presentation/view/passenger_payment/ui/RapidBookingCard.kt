package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.TextBoldSmall

@Composable
fun RapidBookingCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,onRapidBookingCheck: (Boolean) -> Unit) {
        CardComponent(
            shape = RoundedCornerShape(4.dp),
            bgColor = colorResource(id = R.color.white), modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {}
        )
        {
            Row(
                Modifier.height(48.dp)
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
}