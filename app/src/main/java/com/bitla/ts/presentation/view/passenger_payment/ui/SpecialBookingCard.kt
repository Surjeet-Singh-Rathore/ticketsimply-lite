package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CrossIcon
import com.bitla.ts.presentation.components.SpaceComponent
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.components.TextNormalSmall
import com.bitla.ts.presentation.components.isEnableFreeTicket
import com.bitla.ts.presentation.components.isEnableVIPTicket
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.ResourceProvider
import toast

@Composable
fun SpecialBookingCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        Arrangement.Center
    ) {

        passengerDetailsViewModel.apply {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (isFreeTicketVisible.value || isVIPTicketVisible.value)
                    TextBoldSmall(
                        text = stringResource(id = R.string.special_booking),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
            }
            
            SpaceComponent(modifier = Modifier.height(4.dp))

            if (isFreeTicketVisible.value)
                FreeTicket(context, passengerDetailsViewModel)

            if (isVIPTicketVisible.value)
                VIPTicket(context, passengerDetailsViewModel)

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VIPTicket(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white)
//                    if (isFreeTicketChecked.value)
//                        colorResource(id = R.color.button_color)
//                    else
//                        colorResource(id = R.color.white)
                )
                .padding(start = 8.dp, end = 8.dp),
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isVIPTicketChecked.value,
//                enabled = !isFreeTicketChecked.value,
                onCheckedChange = {
                    isVIPTicketChecked.value = !isVIPTicketChecked.value
                    isDisableAdditionalOfferCard.value = !isDisableAdditionalOfferCard.value
                    isEnableVIPTicket(passengerDetailsViewModel)
                    selectedVIPTicketId = ""
                    removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.vip_ticket))
                    setPaymentOptionsVisibility(context, passengerDetailsViewModel)

                },
            )

            TextNormalSmall(
                text = stringResource(id = R.string.vip_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

//            if (selectedVIPTicketId.isNotEmpty() && isEditButtonVisible.value) {
//                EditButton(
//                    borderStroke = BorderStroke(
//                        width = 1.dp,
//                        color = colorResource(id = R.color.colorPrimary)
//                    ),
//                    onClick = {
//                        isEditButtonVisible.value = !isEditButtonVisible.value
//                    }
//                )
//            }

//            if (!isVIPTicketEnable.value){
//                CrossIcon()
//            }
        }

        if (isVIPTicketChecked.value) {  // && !isEditButtonVisible.value
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .requiredSizeIn(maxHeight = 52.dp)
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
                            TextNormalRegular(
                                text = selectionId.value,
                                modifier = Modifier
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
            if (!isEditButtonVisible.value) {
                selectedVIPTicketId = ""
            }
        }

//        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun FreeTicket(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background( colorResource(id = R.color.white)
//                    if (!isFreeTicketEnable.value)
//                        colorResource(id = R.color.button_color)
//                    else
//                        colorResource(id = R.color.white)
                )
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isFreeTicketChecked.value,
//                enabled = isFreeTicketEnable.value,
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
                    setPaymentOptionsVisibility(context, passengerDetailsViewModel)
                },
            )

            TextNormalSmall(
                text = stringResource(id = R.string.free_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

//            if (!isFreeTicketEnable.value) {
//                CrossIcon()
//            }
        }
//        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}