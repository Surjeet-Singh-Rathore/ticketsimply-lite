package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel

@Composable
fun SendSmsOnWhatsAppBookingCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .wrapContentHeight(),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp),
                Arrangement.Center
            ) {

                TextBoldSmall(
                    text = stringResource(id = R.string.share_details_on_whatsapp),
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically),
                    textAlign = TextAlign.Start,
                )

                Switch(
                    checked = passengerDetailsViewModel.sendWhatsAppOnBooking.value,
                    onCheckedChange = {
                        passengerDetailsViewModel.sendWhatsAppOnBooking.value = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green
                    ),
                    modifier = Modifier.weight(0.1f)
                )
            }
        }
    }
}