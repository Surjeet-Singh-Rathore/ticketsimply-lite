package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.view.passenger_payment.ui.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils


@Composable
fun BookingTypeCardNewFlow(
    role: String?,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit,
    onPhoneBookingClick: (String) -> Unit,
    onBookingTypeClick: (Boolean) -> Unit,
) {

    var paddingValue8: Dp
    var paddingValue16: Dp

    if (passengerDetailsViewModel.roleType != null && role?.contains(context.getString(R.string.role_agent), true) == true
        && passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)
        && passengerDetailsViewModel.privilegeResponseModel?.allowToDoPhoneBlocking == true
    ) {
        paddingValue8 = 0.dp
        paddingValue16 = 0.dp
        if (passengerDetailsViewModel.rapidBookingType != 0) {
            paddingValue8 = 8.dp
            paddingValue16 = 8.dp
        }
    } else {
        paddingValue8 = 8.dp
        paddingValue16 = 8.dp
    }
    Column(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 0.dp,
            bottom = paddingValue16
        ), Arrangement.Center
    ) {

        passengerDetailsViewModel.apply {

            if (isBookingTypeCardVisible || isAgentLogin) {
                Row {
                    BookingTypeDropDown(passengerDetailsViewModel, context,role, onBookingTypeClick)
                }

                if (isStatusCardVisible){
                    SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))
                    StatusBookingCard(passengerDetailsViewModel, context,role)
                }
            }

            SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

            if (isPhoneBlockDateTimeVisible){
                Row(
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.light_purple_notification_pickupchart),
                            shape = RoundedCornerShape(2.dp),
                        )
                        .padding(8.dp)
                ) {
                    TextNormalRegular(
                        text = stringResource(id = R.string.time_and_date),
                        modifier = Modifier,
                        textStyle = TextStyle(textAlign = TextAlign.Start)
                    )

                    ClickableText(
                        text = AnnotatedString(passengerDetailsViewModel.phoneBlockTime?.asString(context.resources) ?: ""),
                        modifier = Modifier.padding(start = 12.dp),
                        style = TextStyle(
                            textAlign = TextAlign.Start,
                            color = colorResource(
                                id = R.color.colorBlue
                            ), textDecoration = TextDecoration.Underline,
                            letterSpacing = 1.sp
                        ),
                        onClick = {
                            passengerDetailsViewModel.setPhoneDialogViewVisible(true)
                        }
                    )
                }
            }

            if (isOnlineViewVisible) {
                SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

                OnlineAgentView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    }
                )
            }

            if (isOfflineViewVisible) {
                SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

                OfflineAgentView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    }
                )
            }

            if (isBranchViewVisible) {
                SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

                BranchView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    }
                )
            }



            if (isSubAgentViewVisible) {
                SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))
                SubAgentView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    }
                )
            }
        }
    }

}

@Composable
fun OnlineAgentView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {
        ClickableDropDown(context,stringResource(id = R.string.city), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

        ClickableDropDown(context,stringResource(id = R.string.on_behalf_of), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

        TextFieldComponentRounded(
            modifier = Modifier.fillMaxWidth(),
            value = passengerDetailsViewModel.bookingReferenceNo,
            label = stringResource(id = R.string.enter_reference_number),
            onValueChange = {
                passengerDetailsViewModel.bookingReferenceNo = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}

@Composable
fun OfflineAgentView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {

        OfflineAmountCheckBox(passengerDetailsViewModel)

        ClickableDropDown(context,stringResource(id = R.string.city), passengerDetailsViewModel, onClick = {
                onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

        ClickableDropDown(context,stringResource(id = R.string.on_behalf_of), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

        TextFieldComponentRounded(
            modifier = Modifier.fillMaxWidth(),
            value = passengerDetailsViewModel.bookingReferenceNo,
            label = stringResource(id = R.string.enter_reference_number),
            onValueChange = {
                passengerDetailsViewModel.bookingReferenceNo = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}



@Composable
fun SubAgentView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {
        ClickableDropDown(context,stringResource(id = R.string.on_behalf_of), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

    }
}


@Composable
fun BranchView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {
        ClickableDropDown(context,stringResource(id = R.string.selectBranch), passengerDetailsViewModel, onClick = {
                onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(spaceBetweenTextField))

        ClickableDropDown(context,stringResource(id = R.string.selectUser), passengerDetailsViewModel, onClick = {
            onClick(it)
        })
    }
}

@Composable
fun OfflineAmountCheckBox(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
    CheckBoxComponent(stringResource(id = R.string.amount_paid_offline), isChecked = passengerDetailsViewModel.amountPaidOffline) {
        passengerDetailsViewModel.amountPaidOffline = it
    }
}

@Composable
private fun PhoneBookingCard(context: Context, passengerDetailsViewModel : PassengerDetailsViewModel<Any?>, onPhoneBookingClick: (String) -> Unit) {
    CardComponent(
        shape = RoundedCornerShape(8.dp),
        bgColor = colorResource(id = if(passengerDetailsViewModel.phoneBookingCardColor == PHONE_BOOKING_NOT_SELECTED) R.color.button_secondary_bg else R.color.colorBlue1), modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            onClick = {
               onPhoneBookingClick("")
            })
    {
        Row(modifier = Modifier.padding(12.dp)) {
            Row(Modifier.weight(0.7f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_phone_),
                    contentDescription = stringResource(id = R.string.do_phone_booking),
                    tint = colorResource(
                        id = R.color.white
                    )
                )
                SpaceComponent(modifier = Modifier.padding(start = 4.dp))
                TextBoldRegular(
                    text = stringResource(id = R.string.do_phone_booking),
                    modifier = Modifier,
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.white
                        )
                    )
                )
            }
            Row(Modifier.weight(0.3f), verticalAlignment = Alignment.CenterVertically) {
                TextNormalSmall(
                    text = passengerDetailsViewModel.phoneBlockTime?.asString(context.resources) ?: "",
                    modifier = Modifier,
                    textStyle = TextStyle(
                        colorResource(id = R.color.white)
                    )
                )
            }


        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun BookingTypeDropDown(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    role: String?,
    onBookingTypeClick: (Boolean) -> Unit,
) {

    // Auto-select Walkin option when component is first composed
    LaunchedEffect(Unit) {
        passengerDetailsViewModel.apply {
            selectedRadioWalking = true
            selectedRadioPhoneBooking = false
            selectedRadioOnlineAgent = false
            selectedRadioOfflineAgent = false
            selectedRadioBranch = false
            selectedRadioSubAgentBooking = false

            setBookingType(ResourceProvider.TextResource.fromText("Walkin"))
            setBookingTypeId(0)
            setStatusType(ResourceProvider.TextResource.fromText("Confirm"))
            if (privilegeResponseModel?.isAgentLogin == true) {
                setPaymentOptionsAgents()
            } else {
                setPaymentOptions()
            }
            selectedCityName = ""
            selectedCityId = 0
            onBehalfOfAgentName = ""
            onBehalfOfAgentId = 0
            bookingReferenceNo = ""
            amountPaidOffline = false
            selectedBranchId = 0
            selectedBranchName = ""
            selectedUserId = 0
            selectedUserName = ""
            isBookingTypeCardExpanded = false
            isSeatWiseDiscountEdit = false
            setPaymentOptionsVisibility(context, passengerDetailsViewModel)
        }

        onBookingTypeClick(true)

        manageLayouts(
            selectedBookingId = 0,
            passengerDetailsViewModel = passengerDetailsViewModel,
            context = context,
            role = role ?: ""
        )
    }

    Column {
        TextBoldLarge(
            text = stringResource(id = R.string.booking_type),
            modifier = Modifier.padding(top = 10.dp),
            style = TextStyle(
                colorResource(id = R.color.black),
            )
        )

        if (passengerDetailsViewModel.isExtraSeat) {
            passengerDetailsViewModel.apply {
                selectedRadioWalking = true
                selectedRadioPhoneBooking = false
                selectedRadioOnlineAgent = false
                selectedRadioOfflineAgent = false
                selectedRadioBranch = false
                selectedRadioSubAgentBooking = false
            }
        }

        FlowRow(
            modifier = Modifier
                .background(
                    if (!passengerDetailsViewModel.isExtraSeat) {
                        colorResource(R.color.white)
                    } else {
                        colorResource(R.color.gray_light)
                    }
                )
                .absoluteOffset((-8).dp, 0.dp)
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp)
            ,horizontalArrangement = Arrangement.Start
        ) {
            // Walkin
            Row  (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .requiredHeight(30.dp)
                    .selectable(
                        selected = passengerDetailsViewModel.selectedRadioWalking,
                        onClick = {
                            passengerDetailsViewModel.apply {
                                selectedRadioWalking = true
                                selectedRadioPhoneBooking = false
                                selectedRadioOnlineAgent = false
                                selectedRadioOfflineAgent = false
                                selectedRadioBranch = false
                                selectedRadioSubAgentBooking = false
                            }

                            onBookingTypeClick(true)
                            passengerDetailsViewModel.apply {
                                setBookingType(ResourceProvider.TextResource.fromText("Walkin"))
                                setBookingTypeId(0)
                                setStatusType(ResourceProvider.TextResource.fromText("Confirm"))
                                setPaymentOptions()
                                selectedCityName = ""
                                selectedCityId = 0
                                onBehalfOfAgentName = ""
                                onBehalfOfAgentId = 0
                                bookingReferenceNo = ""
                                amountPaidOffline = false
                                selectedBranchId = 0
                                selectedBranchName = ""
                                selectedUserId = 0
                                selectedUserName = ""
                                isBookingTypeCardExpanded = false
                                isSeatWiseDiscountEdit = false
                                setPaymentOptionsVisibility(context, passengerDetailsViewModel)
                            }

                            manageLayouts(
                                selectedBookingId = 0,
                                passengerDetailsViewModel = passengerDetailsViewModel,
                                context = context,
                                role = role ?: ""
                            )
                        }
                    )
            ) {
                RadioButton(
                    selected = passengerDetailsViewModel.selectedRadioWalking,
                    modifier = Modifier
                        .requiredHeight(20.dp)
                        .absoluteOffset((-10).dp, 0.dp),
                    onClick = {
                        passengerDetailsViewModel.apply {
                            selectedRadioWalking = true
                            selectedRadioPhoneBooking = false
                            selectedRadioOnlineAgent = false
                            selectedRadioOfflineAgent = false
                            selectedRadioBranch = false
                            selectedRadioSubAgentBooking = false
                        }

                        onBookingTypeClick(true)
                        passengerDetailsViewModel.apply {
                            setBookingType(ResourceProvider.TextResource.fromText("Walkin"))
                            setBookingTypeId(0)
                            setStatusType(ResourceProvider.TextResource.fromText("Confirm"))
                            if (privilegeResponseModel?.isAgentLogin == true) {
                                setPaymentOptionsAgents()
                            } else {
                                setPaymentOptions()
                            }
                            selectedCityName = ""
                            selectedCityId = 0
                            onBehalfOfAgentName = ""
                            onBehalfOfAgentId = 0
                            bookingReferenceNo = ""
                            amountPaidOffline = false
                            selectedBranchId = 0
                            selectedBranchName = ""
                            selectedUserId = 0
                            selectedUserName = ""
                            isBookingTypeCardExpanded = false
                            isSeatWiseDiscountEdit = false
                            setPaymentOptionsVisibility(context, passengerDetailsViewModel)
                        }

                        manageLayouts(
                            selectedBookingId = 0,
                            passengerDetailsViewModel = passengerDetailsViewModel,
                            context = context,
                            role = role ?: ""
                        )
                    }
                )

                TextNormalSmall(
                    modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                    text = if (passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                        && passengerDetailsViewModel.privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                    ) {
                        stringResource(id = R.string.confirm)
                    } else {
                        stringResource(id = R.string.walkin)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StatusBookingCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    role: String?
) {

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 12.dp),
        expanded = passengerDetailsViewModel.isStatusBookingCardExpanded,
        onExpandedChange = {
            passengerDetailsViewModel.isStatusBookingCardExpanded = !passengerDetailsViewModel.isStatusBookingCardExpanded
        }
    )
    {
        TextFieldComponentRounded(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            readOnly = true,
            value = if(passengerDetailsViewModel.selectedStatusType != null) {
                passengerDetailsViewModel.selectedStatusType?.asString(context.resources)!!
            } else "",
            onValueChange = { },
            label = stringResource(id = R.string.selectStatusType),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = passengerDetailsViewModel.isStatusBookingCardExpanded
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
            )
        )

        if (passengerDetailsViewModel.rapidBookingType != 0){
            ExposedDropdownMenu(
                expanded = passengerDetailsViewModel.isStatusBookingCardExpanded,
                onDismissRequest = {
                    passengerDetailsViewModel.isStatusBookingCardExpanded = false
                }
            ) {
                passengerDetailsViewModel.bookingStatusTypes.forEach {
                    DropdownMenuItem(
                        onClick = {
                            passengerDetailsViewModel.apply {
                                setStatusType(ResourceProvider.TextResource.fromText(it.value))
//                                setStatusTypeId(it.id)
                                isStatusBookingCardExpanded = false
                                isSeatWiseDiscountEdit = false

                            }
                            manageLayouts(
                                selectedBookingId = it.id,
                                passengerDetailsViewModel = passengerDetailsViewModel,
                                context = context,
                                role = role ?: ""
                            )
                        }
                    ) {
                        TextNormalSmall(
                            text = it.value,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ClickableDropDown(
    context: Context,
    text: String,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onClick: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 12.dp),
        expanded = false,
        onExpandedChange = {
            onClick(text)
        }
    )
    {
        TextFieldComponentRounded(
            modifier = Modifier
                .fillMaxWidth(),
            readOnly = true,
            value = if (text == context.getString(R.string.city)) {
                val loginModelPref = PreferenceUtils.getLogin()
                if (passengerDetailsViewModel.selectedCityName.isEmpty()){
                    passengerDetailsViewModel.selectedCityName = loginModelPref?.city_name ?: ""
                    passengerDetailsViewModel.selectedCityId = loginModelPref?.city_id?.toInt() ?: 0
                    passengerDetailsViewModel.selectedCityName
                } else {
                    passengerDetailsViewModel.selectedCityName
                }
            }
            else if (text == context.getString(R.string.selectBranch)) passengerDetailsViewModel.selectedBranchName
            else if (text == context.getString(R.string.selectUser)) passengerDetailsViewModel.selectedUserName
            else passengerDetailsViewModel.onBehalfOfAgentName,
            onValueChange = { },
            label = text,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = false
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
            )
        )
        ExposedDropdownMenu(
            expanded = false,
            onDismissRequest = {

            }
        ) {

        }
    }
}