package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel


@Composable
fun Toolbar(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>,
            onBackButtonClick: (() -> Unit),
            onPrintButtonClick: (() -> Unit),
            onSideBarMenuClick: (() -> Unit)
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                color = ticketDetailsComposeViewModel.headerBgColor
                    ?: colorResource(R.color.colorPrimary)
            )
            .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
    ) {

        Image(painter = painterResource(id = R.drawable.arrow_back_white),
            contentDescription = "Back Button",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(end = 16.dp)
                .height(24.dp)
                .width(24.dp)
                .clickable {
                    onBackButtonClick.invoke()
                }
                .align(Alignment.CenterVertically))

        Column(
            Modifier.weight(1f)
        ) {
            TextBoldLarge(
                text = "${stringResource(id = R.string.ticket_no)} - ${ticketDetailsComposeViewModel.pnrNumber}",
                modifier = Modifier,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            )

            TextNormalRegular(
                text = "${stringResource(id = R.string.travel_date)}: ${ticketDetailsComposeViewModel.travelDate}",
                modifier = Modifier,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White,
                )
            )
        }

       if (ticketDetailsComposeViewModel.printTicketBtnVisibility() && ticketDetailsComposeViewModel.allowBluetoothPrint) {
           Image(painter = painterResource(id = R.drawable.ic_print),
               contentDescription = stringResource(id = R.string.print),
               contentScale = ContentScale.Fit,
               colorFilter = ColorFilter.tint(Color.White),
               modifier = Modifier
                   .padding(start = 16.dp, end = 16.dp)
                   .height(24.dp)
                   .width(24.dp)
                   .clickable {
                       onPrintButtonClick.invoke()
                   }
                   .align(Alignment.CenterVertically))
       }

        Image(painter = painterResource(id = R.drawable.ic_dots_grey),
            contentDescription = "3 Dots Button",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
                .clickable {
                    onSideBarMenuClick.invoke()
                }
                .align(Alignment.CenterVertically))
    }
}

