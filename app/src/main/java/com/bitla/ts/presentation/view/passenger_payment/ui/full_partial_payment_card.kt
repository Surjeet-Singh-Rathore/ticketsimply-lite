package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.bold
import androidx.core.text.isDigitsOnly
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.DividerLine
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextFieldComponent
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.components.TextNormalSmall
import com.bitla.ts.presentation.components.bottomBorder
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.common.convert
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullPartialPaymentCard(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onPaymentTypeSelection: (String) -> Unit,
    onReleaseDateClick: (String) -> Unit
) {
    CardComponent(shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .wrapContentHeight(), onClick = {})
    {
        Column(modifier = Modifier.padding(16.dp)) {
            TextBoldRegular(
                text = stringResource(id = R.string.payment_type),
                modifier = Modifier.wrapContentHeight(),
                textStyle = TextStyle(
                    color = colorResource(
                        id = R.color.colorBlackShadow
                    )
                )
            )

            passengerDetailsViewModel.paymentTypes.forEach { it ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentType),
                            onClick = {
                                passengerDetailsViewModel.selectedPaymentType = it.paymentType!!
                                onPaymentTypeSelection(
                                    passengerDetailsViewModel.selectedPaymentType.asString(
                                        context.resources
                                    )
                                )
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentType),
                        onClick = {
                            passengerDetailsViewModel.selectedPaymentType = it.paymentType!!
                            onPaymentTypeSelection(
                                passengerDetailsViewModel.selectedPaymentType.asString(
                                    context.resources
                                )
                            )
                        }
                    )
                    TextNormalSmall(
                        text = it.paymentType?.asString(context.resources) ?: "",
                        modifier = Modifier
                    )
                }
            }

            if (passengerDetailsViewModel.isPartialPaymentInfoVisible) DividerLine()

            Row()
            {
                if (passengerDetailsViewModel.isPartialPaymentInfoVisible) passengerDetailsViewModel.partialPaymentTypes.forEach { it ->
                    Row(
                        Modifier
                            .wrapContentWidth()
                            .selectable(
                                selected = (it.paymentType == passengerDetailsViewModel.selectedPartialPayment),
                                onClick = {
                                    passengerDetailsViewModel.selectedPartialPayment = it.paymentType!!

                                    onPaymentTypeSelection(
                                        passengerDetailsViewModel.selectedPartialPayment.asString(
                                            context.resources
                                        )
                                    )
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (it.paymentType == passengerDetailsViewModel.selectedPartialPayment),
                            onClick = {
                                passengerDetailsViewModel.selectedPartialPayment = it.paymentType!!
                                onPaymentTypeSelection(
                                    passengerDetailsViewModel.selectedPartialPayment.asString(
                                        context.resources
                                    )
                                )
                            }
                        )
                        TextNormalSmall(
                            text = it.paymentType?.asString(context.resources) ?: "",
                            modifier = Modifier
                        )
                    }
                }
            }


            if (passengerDetailsViewModel.isShowReleaseDate) CustomDateDropDown(
                stringResource(id = R.string.releaseDate),
                passengerDetailsViewModel,
                onClick = {
                    onReleaseDateClick(it)
                })


            if (passengerDetailsViewModel.isShowReleaseTime) Row(
                Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(end = 8.dp)
                        .height(50.dp),
                    expanded = passengerDetailsViewModel.isPartialHoursDropdownExpanded,
                    onExpandedChange = {
                        passengerDetailsViewModel.isPartialHoursDropdownExpanded =
                            !passengerDetailsViewModel.isPartialHoursDropdownExpanded
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = passengerDetailsViewModel.partialBlockingTimeHours,
                        onValueChange = {
                            passengerDetailsViewModel.partialBlockingTimeHours = it
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.hh),
                                fontSize = 14.sp,
                                style = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                                fontFamily = FontFamily(Font(R.font.notosans_regular))
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = passengerDetailsViewModel.isPartialHoursDropdownExpanded
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                            cursorColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = passengerDetailsViewModel.isPartialHoursDropdownExpanded,
                        onDismissRequest = {
                            passengerDetailsViewModel.isPartialHoursDropdownExpanded = false
                        }
                    ) {
                        stringArrayResource(id = R.array.hourArray).forEach { it ->
                            DropdownMenuItem(
                                onClick = {
                                    passengerDetailsViewModel.partialBlockingTimeHours = it
                                    passengerDetailsViewModel.isPartialHoursDropdownExpanded = false
                                }
                            ) {
                                TextNormalRegular(
                                    text = it,
                                    modifier = Modifier,
                                    textStyle = TextStyle(colorResource(id = R.color.colorBlackShadow))
                                )
                            }
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(50.dp),
                    expanded = passengerDetailsViewModel.isPartialTimeDropdownExpanded,
                    onExpandedChange = {
                        passengerDetailsViewModel.isPartialTimeDropdownExpanded =
                            !passengerDetailsViewModel.isPartialTimeDropdownExpanded
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = passengerDetailsViewModel.partialBlockingTimeMins,
                        onValueChange = {
                            passengerDetailsViewModel.partialBlockingTimeMins = it
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.mm),
                                fontSize = 14.sp,
                                style = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                                fontFamily = FontFamily(Font(R.font.notosans_regular))
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = passengerDetailsViewModel.isPartialTimeDropdownExpanded
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                            cursorColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = passengerDetailsViewModel.isPartialTimeDropdownExpanded,
                        onDismissRequest = {
                            passengerDetailsViewModel.isPartialTimeDropdownExpanded = false
                        }
                    ) {
                        stringArrayResource(id = R.array.minuteArray).forEach { it ->
                            DropdownMenuItem(
                                onClick = {
                                    passengerDetailsViewModel.partialBlockingTimeMins = it
                                    passengerDetailsViewModel.isPartialTimeDropdownExpanded = false
                                }
                            ) {
                                TextNormalRegular(
                                    text = it,
                                    modifier = Modifier,
                                    textStyle = TextStyle(colorResource(id = R.color.colorBlackShadow))
                                )
                            }
                        }
                    }
                }
            }

            if (passengerDetailsViewModel.isPartialPaymentInfoVisible) TextBoldRegular(
                text = "${stringResource(id = R.string.partial_payment_amount)} (${passengerDetailsViewModel.privilegeResponseModel?.currency ?: ""})",
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                textStyle = TextStyle(
                    color = colorResource(
                        id = R.color.colorBlackShadow
                    )
                )
            )

            if (passengerDetailsViewModel.isPartialPaymentInfoVisible) TextFieldComponent(context = context,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                value = if(passengerDetailsViewModel.partialPercentValue == "0.0" || passengerDetailsViewModel.partialPercentValue == "") "" else passengerDetailsViewModel.partialPercentValue.toDouble().toInt().toString(),
                label = stringResource(id = R.string.enterAmount),
                onValueChange = {
                    if (it.isNotEmpty()) {
                        if (it != "0.0" && it.isDigitsOnly())
                            passengerDetailsViewModel.partialPercentValue = it.toDouble().toString()
                        else
                            passengerDetailsViewModel.partialPercentValue = ""
                        passengerDetailsViewModel.partialAmount = it.toDouble()
                        passengerDetailsViewModel.pendingAmount =
                            passengerDetailsViewModel.totalFareString.toDouble()
                                .minus(passengerDetailsViewModel.partialAmount)
                        val boldPartialAmtAmt =
                            SpannableStringBuilder().append("${context.getString(R.string.remaining_amount)}:")
                                .bold { append(" ${passengerDetailsViewModel.privilegeResponseModel?.currency} ${passengerDetailsViewModel.pendingAmount.convert(passengerDetailsViewModel.currencyFormat)}") }
                        passengerDetailsViewModel.fullPartialRemainingAmount =
                            boldPartialAmtAmt.toString()
                    }
                    else {
                        passengerDetailsViewModel.partialPercentValue = ""
                        passengerDetailsViewModel.partialAmount = 0.0
                        passengerDetailsViewModel.pendingAmount =
                            passengerDetailsViewModel.totalFareString.toDouble()
                                .minus(passengerDetailsViewModel.partialAmount)
                        val boldPartialAmtAmt =
                            SpannableStringBuilder().append("${context.getString(R.string.remaining_amount)}:")
                                .bold { append(" ${passengerDetailsViewModel.privilegeResponseModel?.currency} ${passengerDetailsViewModel.pendingAmount.convert(passengerDetailsViewModel.currencyFormat)}") }
                        passengerDetailsViewModel.fullPartialRemainingAmount =
                            boldPartialAmtAmt.toString()
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            )

            if (passengerDetailsViewModel.isPartialPaymentInfoVisible) TextNormalRegular(
                text = passengerDetailsViewModel.fullPartialRemainingAmount.ifEmpty {
                    stringResource(
                        id = R.string.remaining_amount
                    )
                },
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                textStyle = TextStyle(
                    color = colorResource(
                        id = R.color.colorBlackShadow
                    )
                )
            )

            if (passengerDetailsViewModel.isPartialPaymentInfoVisible) TextNormalRegular(
                text = passengerDetailsViewModel.fullPartialTotalAmount.ifEmpty { stringResource(id = R.string.total_amount) },
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                textStyle = TextStyle(
                    color = colorResource(
                        id = R.color.colorBlackShadow
                    )
                )
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun CustomDateDropDown(
    label: String,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onClick: (String) -> Unit
) {
    Box(modifier = Modifier
        .height(50.dp)
        .clip(shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        .fillMaxWidth()
        .background(color = colorResource(id = R.color.colorDimShadow5))
        .clickable(enabled = true) {
            onClick("")
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(49.dp)
                .fillMaxWidth()
                .bottomBorder(1.dp, colorResource(id = R.color.un_select_color)),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            TextNormalSmall(
                text = passengerDetailsViewModel.partialBlockingDate.ifEmpty { label },
                modifier = Modifier.padding(start = 16.dp)
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_calendar_release),
                contentDescription = stringResource(
                    id = R.string.open_calender
                ),
                Modifier.padding(end = 16.dp)
            )
        }
    }

}



