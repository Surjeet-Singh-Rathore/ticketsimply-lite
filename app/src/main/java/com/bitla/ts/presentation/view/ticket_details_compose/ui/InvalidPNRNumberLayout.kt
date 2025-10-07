package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldLarge
import com.bitla.ts.presentation.components.TextBoldRegular

@Composable
fun InvalidPNRNumberLayout(
    ticketNumber: String,
    onBackButtonClick: (() -> Unit),
) {

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .padding(top = 16.dp, start = 10.dp, end = 16.dp, bottom = 12.dp)

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            Image(painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back Button",
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .clickable {
                        onBackButtonClick.invoke()
                    }
                    .padding(top = 8.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp)

            ) {

                TextBoldLarge(
                    text = stringResource(R.string.ticket_details),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.black),
                    ),
                )

                TextBoldRegular(
                    text = "Ticket No - $ticketNumber",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.colorBlackShadow),
                    ),
                )
            }
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)

        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_no_search_results),
                contentDescription = stringResource(R.string.book_another_ticket_in_this_service),
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            TextBoldRegular(
                text = stringResource(R.string.invalid_pnr),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.black),
                ),
            )
        }
    }
}