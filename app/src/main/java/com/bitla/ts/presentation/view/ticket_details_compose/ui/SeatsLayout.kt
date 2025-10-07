package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@Composable
fun SeatsLayout(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>,
                onClick: (() -> Unit)) {
    Column(modifier = Modifier.padding(top = 16.dp)) {

        TextNormalRegular(text = stringResource(id = R.string.seats), modifier = Modifier)

        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .border(
                    BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)),
                    RoundedCornerShape(
                        topStartPercent = 10,
                        topEndPercent = 10,
                        bottomEndPercent = 10,
                        bottomStartPercent = 10
                    )
                )
                .padding(8.dp)
                .clickable {
                    onClick.invoke()
                }, verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.user_management),
                contentDescription = "Booked Seats Button",
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .height(16.dp)
                    .width(16.dp)
            )

            TextBoldRegular(
                text = ticketDetailsComposeViewModel.bookedSeats,
                modifier = Modifier,
                textStyle = TextStyle(
                    color = colorResource(id = R.color.colorPrimary),
                ),
            )
        }
    }
}