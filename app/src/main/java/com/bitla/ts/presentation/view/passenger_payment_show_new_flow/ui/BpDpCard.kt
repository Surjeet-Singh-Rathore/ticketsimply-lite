package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*

@Composable
fun BpDpCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onBoardingPointSelection: (String) -> Unit,
    onDroppingPointSelection: (String) -> Unit
) {
    CardComponent(
        shape = RoundedCornerShape(8.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(), onClick = {}
    ) {
        Column(modifier = Modifier.padding(16.dp), Arrangement.Center) {
            
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
                                        .padding(start = 16.dp, end = 12.dp, top = 2.dp, bottom = 2.dp),
                                    textAlign = TextAlign.Center
                                )
                                
                            }
                            SpaceComponent(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            
            SpaceComponent(modifier = Modifier.height(8.dp))
            
            
            BoardingPointDropDown(passengerDetailsViewModel, onBoardingPointSelection)
            
            SpaceComponent(modifier = Modifier.height(10.dp))
            
            DroppingPointDropDown(passengerDetailsViewModel, onDroppingPointSelection)
            
            SpaceComponent(modifier = Modifier.height(8.dp))
            
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            Canvas(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)) {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
            
            SpaceComponent(modifier = Modifier.height(8.dp))
            
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
            
            
        }
    }
}


@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun BoardingPointDropDown(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onBoardingPointSelection: (String) -> Unit,
) {
    Row (){
        
//        TextBoldLarge(
//            text = stringResource(id = R.string.boarding_point),
//            modifier = Modifier
//                .requiredSizeIn(100.dp)
//                .padding(top = 10.dp),
//            style = TextStyle(
//                colorResource(id = R.color.black),
//            )
//        )
//
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 12.dp)
                .background(colorResource(id = R.color.white)),
            expanded = passengerDetailsViewModel.isBoardingPointCardExpanded,
            onExpandedChange = {
                passengerDetailsViewModel.isBoardingPointCardExpanded =
                    !passengerDetailsViewModel.isBoardingPointCardExpanded
            }
        )
        {
            TextFieldComponentRounded(
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = if (passengerDetailsViewModel.boardingPoint != null) passengerDetailsViewModel.boardingPoint ?: "" else "",
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = passengerDetailsViewModel.isBoardingPointCardExpanded
                    )
                },
                label = stringResource(id = R.string.boarding_point) ,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                )
            )
            
            if (passengerDetailsViewModel.boardingSpinnerList.size > 1) {
                ExposedDropdownMenu(
                    expanded = passengerDetailsViewModel.isBoardingPointCardExpanded,
                    onDismissRequest = {
                        passengerDetailsViewModel.isBoardingPointCardExpanded = false
                    }
                ) {
                    passengerDetailsViewModel.boardingSpinnerList.forEach {
                        DropdownMenuItem(
                            onClick = {
                                onBoardingPointSelection("")
                                passengerDetailsViewModel.apply {
                                    passengerDetailsViewModel.boardingPoint = it.value
                                    passengerDetailsViewModel.boardingId = it.id
                                    
                                    isBoardingPointCardExpanded = false
                                }
                            }
                        ) {
                            TextNormalSmall(text = it.value, modifier = Modifier)
                        }
                    }
                }
            }
            
        }
    }
    
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun DroppingPointDropDown(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onDroppingPointSelection: (String) -> Unit,
) {
    
    Row {
//        TextBoldLarge(
//            text = stringResource(id = R.string.dropping_point),
//            modifier = Modifier
//                .requiredSizeIn(100.dp)
//                .padding(top = 10.dp),
//            style = TextStyle(
//                colorResource(id = R.color.black),
//            )
//        )

        ExposedDropdownMenuBox(
            modifier = Modifier
                .defaultMinSize(minHeight = 12.dp)
                .fillMaxWidth(),
            expanded = passengerDetailsViewModel.isDroppingPointCardExpanded,
            onExpandedChange = {
                if(passengerDetailsViewModel.droppingSpinnerList.size > 1) {
                    passengerDetailsViewModel.isDroppingPointCardExpanded = !passengerDetailsViewModel.isDroppingPointCardExpanded
                }
            }
        )
        {
            TextFieldComponentRounded(
                modifier = Modifier
                    .fillMaxWidth(),
                readOnly = true,
                value = if (passengerDetailsViewModel.droppingPoint != null) passengerDetailsViewModel.droppingPoint ?: "" else "",
                onValueChange = { },
                
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = passengerDetailsViewModel.isDroppingPointCardExpanded
                    )
                },
                label = stringResource(id = R.string.dropping_point) ,
                
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                )
            )
            
            if (passengerDetailsViewModel.droppingSpinnerList.size > 1) {
                ExposedDropdownMenu(
                    expanded = passengerDetailsViewModel.isDroppingPointCardExpanded,
                    onDismissRequest = {
                        passengerDetailsViewModel.isDroppingPointCardExpanded = false
                    }
                ) {
                    passengerDetailsViewModel.droppingSpinnerList.forEach {
                        DropdownMenuItem(
                            onClick = {
                                onDroppingPointSelection("")
                                passengerDetailsViewModel.apply {
                                    passengerDetailsViewModel.droppingPoint = it.value
                                    passengerDetailsViewModel.droppingId = it.id
                                    isDroppingPointCardExpanded = false
                                }
                                
                            }
                        ) {
                            TextNormalSmall(text = it.value, modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}

