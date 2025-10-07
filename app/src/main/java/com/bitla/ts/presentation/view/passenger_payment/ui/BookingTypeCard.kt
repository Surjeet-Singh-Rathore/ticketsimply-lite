package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.sharedPref.PreferenceUtils


@Composable
fun BookingTypeCard(
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
            paddingValue16 = 16.dp
        }
    } else {
        paddingValue8 = 8.dp
        paddingValue16 = 16.dp
    }

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValue8)
            .wrapContentHeight(),onClick = {}
    ) {
        Column(modifier = Modifier.padding(paddingValue16), Arrangement.Center) {

            passengerDetailsViewModel.apply {

                if (isBookingTypeCardVisible || passengerDetailsViewModel.isAgentLogin) {
                    TextBoldSmall(
                        text = stringResource(id = R.string.booking_type).uppercase(),
                        modifier = Modifier,
                        textAlign = TextAlign.Start
                    )

                    SpaceComponent(modifier = Modifier.height(8.dp))

                    BookingTypeDropDown(passengerDetailsViewModel, context,role, onBookingTypeClick)

                    if (isStatusCardVisible){
                        SpaceComponent(modifier = Modifier.height(8.dp))

                        StatusBookingCard(passengerDetailsViewModel, context,role)
                    }
                }

                SpaceComponent(modifier = Modifier.height(8.dp))

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


                if (isOnlineViewVisible) SpaceComponent(modifier = Modifier.height(8.dp))

                if (isOnlineViewVisible) OnlineAgentView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    })

                if (isOfflineViewVisible) SpaceComponent(modifier = Modifier.height(8.dp))

                if (isOfflineViewVisible) OfflineAgentView(
                    passengerDetailsViewModel,
                    context,
                    onClick = {
                        onClick(it)
                    })

                if (isBranchViewVisible) {
                    SpaceComponent(modifier = Modifier.height(8.dp))
                }

                if (isBranchViewVisible) {
                    BranchView(
                        passengerDetailsViewModel,
                        context,
                        onClick = {
                            onClick(it)
                        })
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

                if (!isExtraSeats && isBima==true) {
                    if (passengerDetailsViewModel.isAgentLogin) {
                        if (isPhoneBookingVisible && rapidBookingType != 0) {
                            SpaceComponent(modifier = Modifier.height(4.dp))
                            PhoneBookingCard(
                                context = context,
                                passengerDetailsViewModel = passengerDetailsViewModel,
                                onPhoneBookingClick = { onPhoneBookingClick(it) })
                        }
                    }
                    else {
                        if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                            && !privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                        ) {
                            if (isPhoneBookingVisible && rapidBookingType != 0) {
                                SpaceComponent(modifier = Modifier.height(4.dp))
                                PhoneBookingCard(
                                    context = context,
                                    passengerDetailsViewModel = passengerDetailsViewModel,
                                    onPhoneBookingClick = { onPhoneBookingClick(it) })
                            }
                        }
                    }
                } else {
                    if (passengerDetailsViewModel.isAgentLogin) {

                        if (isPhoneBookingVisible && rapidBookingType != 0 && !isExtraSeat) {
//                                SpaceComponent(modifier = Modifier.height(4.dp))
//                                PhoneBookingCard(
//                                    context = context,
//                                    passengerDetailsViewModel = passengerDetailsViewModel,
//                                    onPhoneBookingClick = { onPhoneBookingClick(it) })
                        }else{
                            setPhoneBookingVisibility(false)
//                            setBookingCardVisibility(false)
                        }
                    } else {
                        if (privilegeResponseModel?.allowToSwitchSinglePageBooking != null
                            && !privilegeResponseModel?.allowToSwitchSinglePageBooking!!
                        ) {
                            if (isPhoneBookingVisible && rapidBookingType != 0) {
                                SpaceComponent(modifier = Modifier.height(4.dp))
                                PhoneBookingCard(
                                    context = context,
                                    passengerDetailsViewModel = passengerDetailsViewModel,
                                    onPhoneBookingClick = { onPhoneBookingClick(it) })
                            }
                        }
                    }
                }

            }

            /*if (passengerDetailsViewModel.country.equals("india", true)) {
                if (passengerDetailsViewModel.rapidBookingType != 0) {
                    if (!passengerDetailsViewModel.isAdditionalOfferCardVisible && !passengerDetailsViewModel.isAgentLogin) {
                        SpaceComponent(modifier = Modifier.height(4.dp))

                        SpecialBookingCard(
                            passengerDetailsViewModel
                        )
                    }
                }
            }*/
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

        SpaceComponent(modifier = Modifier.height(4.dp))

        ClickableDropDown(context,stringResource(id = R.string.on_behalf_of), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(4.dp))

        TextFieldComponent(
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

        SpaceComponent(modifier = Modifier.height(4.dp))

        ClickableDropDown(context,stringResource(id = R.string.on_behalf_of), passengerDetailsViewModel, onClick = {
            onClick(it)
        })

        SpaceComponent(modifier = Modifier.height(4.dp))

        TextFieldComponent(
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
fun BranchView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {
        ClickableDropDown(
            context,
            stringResource(id = R.string.selectBranch),
            passengerDetailsViewModel,
            onClick = {
                onClick(it)
            })

        SpaceComponent(modifier = Modifier.height(4.dp))

        ClickableDropDown(
            context,
            stringResource(id = R.string.selectUser),
            passengerDetailsViewModel,
            onClick = {
                onClick(it)
            })
    }
}

@Composable
fun SubAgentView(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    onClick: (String) -> Unit
) {
    Column {
        ClickableDropDown(
            context,
            stringResource(id = R.string.on_behalf_of),
            passengerDetailsViewModel,
            onClick = {
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
        shape = RoundedCornerShape(4.dp),
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

                SpaceComponent(modifier = Modifier.padding(start = 4.dp, top = 6.dp))

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
@OptIn(ExperimentalMaterialApi::class)
private fun BookingTypeDropDown(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    context: Context,
    role: String?,
    onBookingTypeClick: (Boolean) -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = Modifier.requiredSizeIn(maxHeight = 52.dp),
        expanded = passengerDetailsViewModel.isBookingTypeCardExpanded,
        onExpandedChange = {
            if (!passengerDetailsViewModel.isExtraSeat){
                passengerDetailsViewModel.isBookingTypeCardExpanded = !passengerDetailsViewModel.isBookingTypeCardExpanded
            }
        }
    )
    {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = colorResource(id = R.color.colorBlackShadow),
                fontSize = 12.sp
            ),
            readOnly = true,
            value = if(passengerDetailsViewModel.selectedBookingType != null) passengerDetailsViewModel.selectedBookingType?.asString(context.resources)!! else "",
            onValueChange = { },
            label = {
                TextNormalSmall(
                    text = stringResource(id = R.string.selectBookingType),
                    modifier = Modifier,
                    textStyle = TextStyle(
                        colorResource(id = R.color.colorPrimary)
                    )
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = passengerDetailsViewModel.isBookingTypeCardExpanded
                )
            },
            colors = if (!passengerDetailsViewModel.isExtraSeat) {
                TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                    cursorColor = Color.Gray
                )
            }
            else {
                TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Gray,
                    disabledTextColor = Color.Gray.copy(alpha = ContentAlpha.disabled),
                    backgroundColor = Color.Gray.copy(alpha = ContentAlpha.disabled)
                )
            }
        )

        if (passengerDetailsViewModel.rapidBookingType != 0){
            ExposedDropdownMenu(
                expanded = passengerDetailsViewModel.isBookingTypeCardExpanded,
                onDismissRequest = {
                    passengerDetailsViewModel.isBookingTypeCardExpanded = false
                }
            ) {
                passengerDetailsViewModel.bookingTypes.forEach {
                    DropdownMenuItem(
                        onClick = {
                            onBookingTypeClick(true)
                            passengerDetailsViewModel.apply {
                                setBookingType(ResourceProvider.TextResource.fromText(it.value))
                                setBookingTypeId(it.id)
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
                                setPaymentOptionsVisibility(context,passengerDetailsViewModel)
                            }

                            manageLayouts(
                                selectedBookingId = it.id,
                                passengerDetailsViewModel = passengerDetailsViewModel,
                                context = context,
                                role = role ?: ""
                            )
                        }
                    ) {
                        TextNormalSmall(text = it.value, modifier = Modifier)
                    }
                }
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
        modifier = Modifier.requiredSizeIn(maxHeight = 52.dp),
        expanded = passengerDetailsViewModel.isStatusBookingCardExpanded,
        onExpandedChange = {
            passengerDetailsViewModel.isStatusBookingCardExpanded = !passengerDetailsViewModel.isStatusBookingCardExpanded
        }
    )
    {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = colorResource(id = R.color.colorBlackShadow),
                fontSize = 12.sp
            ),
            readOnly = true,
            value = if(passengerDetailsViewModel.selectedStatusType != null) {
                passengerDetailsViewModel.selectedStatusType?.asString(context.resources)!!
            } else "",
            onValueChange = { },
            label = {
                TextNormalSmall(
                    text = stringResource(id = R.string.selectStatusType),
                    modifier = Modifier,
                    textStyle = TextStyle(
                        colorResource(id = R.color.colorPrimary)
                    )
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = passengerDetailsViewModel.isStatusBookingCardExpanded
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                cursorColor = Color.Gray
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
        modifier = Modifier.requiredSizeIn(maxHeight = 52.dp),
        expanded = false,
        onExpandedChange = {
            onClick(text)
        }
    )
    {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = colorResource(id = R.color.colorBlackShadow),
                fontSize = 12.sp
            ),
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
            } else if (text == context.getString(R.string.selectBranch)) {
                passengerDetailsViewModel.selectedBranchName
            } else if (text == context.getString(R.string.selectUser)) {
                passengerDetailsViewModel.selectedUserName
            } else passengerDetailsViewModel.onBehalfOfAgentName,
            onValueChange = { },
            label = {
                TextNormalSmall(
                    text = text,
                    modifier = Modifier,
                    textStyle = TextStyle(
                        colorResource(id = R.color.colorPrimary)
                    )
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = false
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                cursorColor = Color.Gray
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