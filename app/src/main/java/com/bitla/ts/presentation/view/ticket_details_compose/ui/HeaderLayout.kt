package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldLarge
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@Composable
fun HeaderLayout(
    ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                color = ticketDetailsComposeViewModel.headerBgColor
                    ?: colorResource(R.color.colorPrimary)
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = ticketDetailsComposeViewModel.ticketStatusIcon ?: R.drawable.thumbs_up),
            contentDescription = "Thumbs up icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(32.dp)
                .width(32.dp),
        )

        TextBoldLarge(
            modifier = Modifier.padding(8.dp),
            text = ticketDetailsComposeViewModel.ticketStatusTitle ?: stringResource(R.string.ticket_booked_successfully),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        )

        TextBoldRegular(
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, Color.White), RoundedCornerShape(
                        topStartPercent = 80,
                        topEndPercent = 80,
                        bottomEndPercent = 80,
                        bottomStartPercent = 80
                    )
                )
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
            text = ticketDetailsComposeViewModel.bookingType,
            textStyle = TextStyle(
                fontSize = 10.sp,
                color = Color.White,
            ),
        )
    }
}