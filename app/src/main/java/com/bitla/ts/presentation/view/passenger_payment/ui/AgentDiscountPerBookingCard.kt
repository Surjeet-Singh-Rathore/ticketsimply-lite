package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.DividerLine
import com.bitla.ts.presentation.components.EditButton
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.components.TextFieldComponent
import com.bitla.ts.presentation.components.TextNormalSmall
import com.bitla.ts.presentation.components.TickIcon
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import timber.log.Timber
import toast


@Composable
fun AgentDiscountPerBookingCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
) {

    val context = LocalContext.current

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

            passengerDetailsViewModel.apply {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                )  {

                    TextBoldSmall(
                        text = stringResource(id = R.string.additional_options),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )

                    Image(
                        painter = if (isExpandPerBookingDiscount) {
                            painterResource(id = R.drawable.ic_arrow_up)
                        } else {
                            painterResource(id = R.drawable.ic_arrow_down)
                        },
                        contentDescription = "arrow",
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable {
                                isExpandPerBookingDiscount = !isExpandPerBookingDiscount
                            }
                    )
                }

                if (isEnableCampaignPromotions && isEnableCampaignPromotionsChecked && isExpandPerBookingDiscount) {
                    AgentDiscountPerBooking(context, passengerDetailsViewModel)
                }
            }
        }
    }
}

@Composable
private fun AgentDiscountPerBooking(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isEnableCampaignPromotionsChecked,
                onCheckedChange = {
                    isEnableCampaignPromotionsChecked = it
                    isFareBreakupApiCalled = true
                },
            )

            TextNormalSmall(
                text = stringResource(id = R.string.discount_amount),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp),
            )

            if (isPerBookingDiscountEditButtonVisible) {
                EditButton(
                    borderStroke = BorderStroke(
                        width = 1.dp,
                        color = colorResource(id = R.color.colorPrimary)
                    ),
                    onClick = {
                        isPerBookingDiscountEditButtonVisible = !isPerBookingDiscountEditButtonVisible
                    }
                )
            }
        }

        if (isEnableCampaignPromotionsPerBookingChecked && !isPerBookingDiscountEditButtonVisible) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = "$perBookingEditedDiscountValue",
                label = stringResource(id = R.string.enter_discount_amount),
                placeholder = stringResource(id = R.string.enter_discount_amount),
                onValueChange = {

                    isTickIconVisible.value = it.isNotEmpty()

                    if (it.isNotEmpty()) {
                        try {
                            if (it.toDouble() > perBookingDiscountValue) {
                                perBookingEditedDiscountValue = perBookingDiscountValue.toString()
                                context.toast("${context.getString(R.string.discount_validation)} $perBookingDiscountValue")
                            } else {
                                perBookingEditedDiscountValue = it
                            }
                        } catch (e: Exception) {
                            Timber.d(e.message)
                        }
                    } else {
                        perBookingEditedDiscountValue = ""
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    val flag = try {
                        perBookingEditedDiscountValue.toDouble() > -1
                    } catch(e: Exception) {
                        false
                    }
                    if (flag) {
                        TickIcon()
                    }
                }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextBoldRegular(
                    text = stringResource(id = R.string.cancel),
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                        .clickable {
                            isPerBookingDiscountEditButtonVisible = true
                            perBookingEditedDiscountValue = perBookingDiscountValue.toString()
                            isPerBookingDiscountAmountChanged = true
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            try {
                                if (perBookingEditedDiscountValue.toDouble() <= perBookingDiscountValue) {
                                    isPerBookingDiscountAmountChanged = true
                                    isPerBookingDiscountEditButtonVisible = true

                                } else {
                                    context.toast("${context.getString(R.string.discount_validation)} $perBookingDiscountValue")
                                }
                            } catch (e: Exception) {
                                Timber.d(e.message)
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        }/* else {
            if (!isExpandPerBookingDiscountEditButtonVisible){
                perBookingEditedDiscountValue = 0.0
            }
        }*/

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}
