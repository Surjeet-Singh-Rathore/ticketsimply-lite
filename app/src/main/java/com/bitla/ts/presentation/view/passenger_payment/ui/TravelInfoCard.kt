package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.SpaceComponent
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.components.TextMultiStyle
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel

@Composable
fun TravelInfoCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    CardComponent(shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .wrapContentHeight(), onClick = {}) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                if (!passengerDetailsViewModel.boardingPoint.isNullOrEmpty()) {
                    Row(Modifier.weight(0.4f)) {
                        TextMultiStyle(
                            originalText = "${passengerDetailsViewModel.boardingStageDetail?.time ?: ""}, ${passengerDetailsViewModel.boardingPoint ?: ""},${passengerDetailsViewModel.boardingStageDetail?.address ?: ""}",
                            boldText = "${passengerDetailsViewModel.boardingStageDetail?.time ?: ""},",
                            normalText = " ${passengerDetailsViewModel.boardingPoint ?: ""},${passengerDetailsViewModel.boardingStageDetail?.address ?: ""}"
                        )
                    }
                }

                SpaceComponent(modifier = Modifier.padding(8.dp))

                Column(
                    Modifier
                        .weight(0.2f)
                        .height(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    /*TextNormalSmall(text = "time", modifier = Modifier, textStyle = TextStyle(color = colorResource(
                        id = R.color.light_purple_notification_pickupchart
                    )))*/
                    Image(
                        painterResource(R.drawable.arrow_right_grey),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                    )
                }

                SpaceComponent(modifier = Modifier.padding(8.dp))

                if (!passengerDetailsViewModel.droppingPoint.isNullOrEmpty()) {
                    Row(Modifier.weight(0.4f)) {
                        TextMultiStyle(
                            originalText = "${passengerDetailsViewModel.droppingStageDetail?.time ?: ""}, ${passengerDetailsViewModel.droppingPoint ?: ""},${passengerDetailsViewModel.droppingStageDetail?.address ?: ""}",
                            boldText = "${passengerDetailsViewModel.droppingStageDetail?.time ?: ""},",
                            normalText = " ${passengerDetailsViewModel.droppingPoint ?: ""},${passengerDetailsViewModel.droppingStageDetail?.address ?: ""}"
                        )
                    }
                }
            }

            SpaceComponent(modifier = Modifier.height(8.dp))

            var selectedSeats = ""
            for (i in 0 until passengerDetailsViewModel.selectedSeatDetails.size) {
                selectedSeats = passengerDetailsViewModel.selectedSeatDetails[i].number.toString()
            }

            if (selectedSeats.isNotEmpty()){
                Row {
                    TextBoldSmall(
                        text = stringResource(id = R.string.selectedSeats),
                        modifier = Modifier,
                        textAlign = TextAlign.Start
                    )

                    SpaceComponent(modifier = Modifier.width(8.dp))

                    LazyRow {
                        items(passengerDetailsViewModel.selectedSeatDetails) { item ->
                            Box(
                                modifier = Modifier.background(
                                    color = colorResource(id = R.color.light_purple_notification_pickupchart),
                                    shape = RoundedCornerShape(2.dp)
                                )
                            ) {
                                TextBoldSmall(
                                    text = item.number ?: "",
                                    modifier = Modifier
                                        .padding(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 2.dp),
                                    textAlign = TextAlign.Center
                                )

                            }
                            SpaceComponent(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}