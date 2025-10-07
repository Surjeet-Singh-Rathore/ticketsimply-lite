package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.DividerLine
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.convert

@Composable
fun FareBreakupBottomSheet(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
)
{
    if (passengerDetailsViewModel.isFareBreakupBottomSheetVisible) Box(
        modifier = Modifier.height(204.dp)
    ) {
        CardComponent(
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            bgColor = colorResource(id = R.color.light_purple_notification_pickupchart),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            onClick = {}) {

            LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp).align(alignment = Alignment.TopStart)) {

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBoldRegular(
                            text = stringResource(id = R.string.fare_details),
                            modifier = Modifier,
                            textStyle = TextStyle(textAlign = TextAlign.Start)
                        )

                        IconButton(onClick = {
                            passengerDetailsViewModel.isFareBreakupBottomSheetVisible =
                                false
                        }, modifier = Modifier.padding(start = 8.dp)) {
                            
                            Icon(
//                              imageVector = ImageVector.vectorResource(id = R.drawable.ic_cross),
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.fare_details),
                            )
                        }
                    }
                }

                items(passengerDetailsViewModel.fareBreakupDetails) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        TextNormalRegular(
                            text = it.label,
                            modifier = Modifier,
                            textStyle = TextStyle(textAlign = TextAlign.Start)
                        )
                        TextNormalRegular(
                            text = if(it.value.toString().isNotEmpty())"${passengerDetailsViewModel.amountCurrency} ${it.value.toString().toDouble().convert(passengerDetailsViewModel.currencyFormat)}" else "${passengerDetailsViewModel.amountCurrency} ${it.value}",
                            modifier = Modifier,
                            textStyle = TextStyle(textAlign = TextAlign.Start)
                        )
                    }
                }

                item {
                    DividerLine()
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        TextNormalRegular(
                            text = stringResource(id = R.string.net_payable_fare),
                            modifier = Modifier,
                            textStyle = TextStyle(textAlign = TextAlign.Start)
                        )
                        TextBoldRegular(
                            text = if(passengerDetailsViewModel.partialAmount == 0.0 || !passengerDetailsViewModel.isPartialPayment || passengerDetailsViewModel.isInsuranceChecked.value) "${passengerDetailsViewModel.amountCurrency} ${passengerDetailsViewModel.totalFareString.toDouble().convert(passengerDetailsViewModel.currencyFormat)}" else "${passengerDetailsViewModel.amountCurrency} ${passengerDetailsViewModel.partialAmount.convert(passengerDetailsViewModel.currencyFormat)}",
                            modifier = Modifier,
                            textStyle = TextStyle(textAlign = TextAlign.End)
                        )
                    }
                }
            }

        }
    }
}