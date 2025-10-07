package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@Composable
fun NotePhoneBooking(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 10.dp),
        Arrangement.Center
    ) {

        ticketDetailsComposeViewModel.apply {

            TextBoldSmall(
                text = stringResource(id = R.string.phone_booking_note),
                modifier = Modifier,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(
                        id = R.color.colorRed1
                    )
                )
            )
        }
    }
}
