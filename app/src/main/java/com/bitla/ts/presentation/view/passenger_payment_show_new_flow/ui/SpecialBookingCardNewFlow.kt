package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.view.passenger_payment.ui.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import toast

@Composable
fun SpecialBookingCardNewFlow(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        Arrangement.Center
    ) {

        passengerDetailsViewModel.apply {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(start = 8.dp, end = 8.dp)
            )  {

                TextBoldSmall(
                    text = stringResource(id = R.string.special_booking),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            if (isFreeTicketVisible.value)
                FreeTicket(context, passengerDetailsViewModel)

            if (isVIPTicketVisible.value)
                VIPTicket(context, passengerDetailsViewModel)

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VIPTicket(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier.padding(top = 12.dp)
                .fillMaxWidth()
                .background(
                    if (isFreeTicketChecked.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
        ) {
            Checkbox(
                modifier = Modifier.requiredHeight(26.dp).absoluteOffset((-10).dp, 0.dp),
                checked = isVIPTicketChecked.value,
                onCheckedChange = {
                    isVIPTicketChecked.value = !isVIPTicketChecked.value
                    isDisableAdditionalOfferCard.value = !isDisableAdditionalOfferCard.value
                    isEnableVIPTicket(passengerDetailsViewModel)
                    selectedVIPTicketId = ""
                    removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.vip_ticket))
                    setPaymentOptionsVisibility(context,passengerDetailsViewModel)

                },
                enabled = !isFreeTicketChecked.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.vip_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, (0).dp)
            )

            if (selectedVIPTicketId.isNotEmpty() && isEditButtonVisible.value) {
                EditButton(
                    borderStroke = BorderStroke(
                        width = 1.dp,
                        color = colorResource(id = R.color.colorPrimary)
                    ),
                    onClick = {
                        isEditButtonVisible.value = !isEditButtonVisible.value
                    }
                )
            }

            if (!isVIPTicketEnable.value){
                CrossIcon()
            }
        }

        if (isVIPTicketChecked.value) {  // && !isEditButtonVisible.value
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .requiredSizeIn(maxHeight = 46.dp)
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                expanded = isExposedVIPTicketDropdown.value,
                onExpandedChange = {
                    isExposedVIPTicketDropdown.value = !isExposedVIPTicketDropdown.value
                }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        color = colorResource(id = R.color.colorBlackShadow),
                        fontSize = 12.sp
                    ),
                    readOnly = true,
                    value = selectedVIPTicketId,
                    onValueChange = { },
                    label = {
                        TextNormalSmall(
                            text = stringResource(id = R.string.selectVip),
                            modifier = Modifier,
                            textStyle = if (selectedVIPTicketId.isNotEmpty()) {
                                TextStyle(
                                    colorResource(id = R.color.colorPrimary)
                                )
                            } else {
                                TextStyle(
                                    colorResource(id = R.color.colorBlackShadow)
                                )
                            }
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isExposedVIPTicketDropdown.value
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                        cursorColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = isExposedVIPTicketDropdown.value,
                    onDismissRequest = {
                        isExposedVIPTicketDropdown.value = false
                    }
                ) {
                    vipCategoryList.forEach { selectionId ->
                        DropdownMenuItem(
                            onClick = {
                                selectedVIPTicketId = selectionId.value
                                isExposedVIPTicketDropdown.value = false
                                checkedOfferTypeResId =
                                    ResourceProvider.TextResource.fromStringId(R.string.vip_ticket)

                                if (selectedVIPTicketId.isNotEmpty()) {
                                    addAppliedCoupon(selectedVIPTicketId)
                                    isEditButtonVisible.value = true
                                } else {
                                    context.toast(context.getString(R.string.selectVip))
                                }
                            }
                        ) {
                            Text(
                                text = selectionId.value,
                                fontSize = 14.sp,
                                style = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                                fontFamily = FontFamily(Font(R.font.notosans_regular))
                            )
                        }
                    }
                }
            }

//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                TextBoldRegular(
//                    text = stringResource(id = R.string.cancel),
//                    modifier = Modifier
//                        .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
//                        .clickable {
//                            isVIPTicketChecked.value = false
//                            selectedVIPTicketId = ""
//                            isEnableVIPTicket(passengerDetailsViewModel)
//                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.vip_ticket))
//                        },
//                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
//                )
//                TextBoldRegular(
//                    text = stringResource(id = R.string.apply),
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .clickable {
//
//                            checkedOfferTypeResId =
//                                ResourceProvider.TextResource.fromStringId(R.string.vip_ticket)
//
//                            if (selectedVIPTicketId.isNotEmpty()) {
//                                addAppliedCoupon(selectedVIPTicketId)
//                                isEditButtonVisible.value = true
//                            } else {
//                                context.toast(context.getString(R.string.selectVip))
//                            }
//                        },
//                    textStyle = TextStyle(
//                        color = colorResource(
//                            id = R.color.colorPrimary
//                        )
//                    )
//                )
//            }

        } else {
            if (!isEditButtonVisible.value){
                selectedVIPTicketId = ""
            }
        }

//        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun FreeTicket(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier.padding(top = 12.dp)
                .fillMaxWidth()
                .background(
                    if (!isFreeTicketEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
        ) {
            Checkbox(
                modifier = Modifier.requiredHeight(26.dp).absoluteOffset((-10).dp, 0.dp),
                checked = isFreeTicketChecked.value,
                onCheckedChange = {
                    isFreeTicketChecked.value = !isFreeTicketChecked.value
                    isEnableFreeTicket(passengerDetailsViewModel)
                    checkedOfferTypeResId =
                        ResourceProvider.TextResource.fromStringId(R.string.free_ticket)
                    if (isFreeTicketChecked.value) {
                        addAppliedCoupon(context.getString(R.string.empty))
                    } else {
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.free_ticket))
                    }
                    setPaymentOptionsVisibility(context,passengerDetailsViewModel)
                },
                enabled = isFreeTicketEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.free_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, (0).dp)
            )

            if (!isFreeTicketEnable.value){
                CrossIcon()
            }
        }
//        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}