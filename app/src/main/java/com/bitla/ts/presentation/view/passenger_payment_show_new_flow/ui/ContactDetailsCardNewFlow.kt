package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.PHONE_VALIDATION_COUNT


@Composable
fun ContactDetailsCardNewFlow(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
) {

    CardComponent(
        shape = RoundedCornerShape(8.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        onClick = {}
    )
    {
        Column(modifier = Modifier.padding(16.dp)) {
            
            TextBoldSmall(
                text = stringResource(id = R.string.passenger_details).uppercase(),
                modifier = Modifier,
                textAlign = TextAlign.Start
            )

            DividerLine(modifier = Modifier.padding(top = 8.dp))
            
            SpaceComponent(modifier = Modifier.height(2.dp))
            // mobile number
            if (passengerDetailsViewModel.mobileNoPrivilege.value != stringResource(id = R.string.hide)) {
                
                TextFieldComponentRounded(
                    isError = if (passengerDetailsViewModel.phoneValidationCountPrivilege.value != null)
                        passengerDetailsViewModel.mobileNoPrivilege.value == stringResource(
                            id = R.string.mandatory
                        ) && passengerDetailsViewModel.primaryMobileNo.length !=
                                passengerDetailsViewModel.phoneValidationCountPrivilege.value
                    else {
                        passengerDetailsViewModel.mobileNoPrivilege.value == stringResource(
                            id = R.string.mandatory
                        ) && passengerDetailsViewModel.primaryMobileNo.isEmpty()
                    },
                    label = stringResource(id = R.string.phone_no),
                    value = passengerDetailsViewModel.primaryMobileNo,
                    onValueChange = {
                        passengerDetailsViewModel.apply {
                            if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                passengerDetailsViewModel.primaryMobileNo = it.take(passengerDetailsViewModel.phoneValidationCountPrivilege.value ?: PHONE_VALIDATION_COUNT)
                            } else {
                                passengerDetailsViewModel.primaryMobileNo = it
                            }
                            isRetrieveClicked.value = false
                        }

                        if(passengerDetailsViewModel.promotionCouponCode != ""){
                            passengerDetailsViewModel.apply {
                                promotionCouponCode = ""
                                isEditButtonVisible.value = false
                                isPromotionCouponChecked.value = false
                                isPrePostponeTicketEnable.value = true
                                isPrePostponeTicketEnable.value = true
                                isCouponCodeEnable.value = true
                                isPrivilegeCardEnable.value = true
                                isApplySmartMilesEnable.value = true
                                isQuotePreviousPNREnable.value = true
                                removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon))

                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .focusRequester(passengerDetailsViewModel.focusRequesterPrimaryMobile),

                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    visualTransformation = VisualTransformation.None,
                    isEnable = true,
                )
            }
            
            SpaceComponent(modifier = Modifier.height(2.dp))
            
            // alternate mobile number
            if (passengerDetailsViewModel.alternateNoPrivilege.value != stringResource(id = R.string.hide)) {
                TextFieldComponentRounded(
                    isError = if (passengerDetailsViewModel.phoneValidationCountPrivilege.value != null) passengerDetailsViewModel.alternateNoPrivilege.value == stringResource(
                        id = R.string.mandatory
                    ) && passengerDetailsViewModel.alternateMobileNo.length != passengerDetailsViewModel.phoneValidationCountPrivilege.value else passengerDetailsViewModel.alternateNoPrivilege.value == stringResource(
                        id = R.string.mandatory
                    ) && passengerDetailsViewModel.alternateMobileNo.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .focusRequester(passengerDetailsViewModel.focusRequesterPrimaryMobile),
                    label = stringResource(id = R.string.alternate_phone_no),
                    value = passengerDetailsViewModel.alternateMobileNo,
                    onValueChange = {
                        passengerDetailsViewModel.apply {
                            alternateMobileNo = it
                            
                            if (phoneValidationCountPrivilege.value != null && phoneValidationCountPrivilege.value != 0) {
                                alternateMobileNo = it.take(phoneValidationCountPrivilege.value ?: PHONE_VALIDATION_COUNT)
                            } else {
                                alternateMobileNo = it
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    visualTransformation = VisualTransformation.None,
                    isEnable = true,
                )
            }
            
            SpaceComponent(modifier = Modifier.height(2.dp))
            
            // email id
            if (passengerDetailsViewModel.emailPrivilege.value != stringResource(id = R.string.hide)) {
                
                TextFieldComponentRounded(
                    isError = passengerDetailsViewModel.emailPrivilege.value == stringResource(id = R.string.mandatory
                    ) && !isEmailValid(passengerDetailsViewModel.emailId),
                    value = passengerDetailsViewModel.emailId,
                    onValueChange = {
                        passengerDetailsViewModel.emailId = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .focusRequester(passengerDetailsViewModel.focusRequesterPrimaryMobile),
                    label = stringResource(id = R.string.email_id),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Email,
                    ),
                    isEnable = true,

                )
                
                SpaceComponent(modifier = Modifier.height(4.dp))
            }
            
            SpaceComponent(modifier = Modifier.height(2.dp))
            
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ){
                
                // send sms on Booking
                
                Row (
                    modifier = Modifier
                        .weight(0.8f)
                        .requiredHeight(26.dp),
                    Arrangement.Center
                ) {
                    
                    TextBoldSmall(
                        text = stringResource(id = R.string.sendSms),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            color = colorResource(id = R.color.black),
                            fontSize = 12.sp
                        )
                    )
                    
                    Switch(
                        checked = passengerDetailsViewModel.isSendSmsOnBooking.value,
                        onCheckedChange = {
                            passengerDetailsViewModel.isSendSmsOnBooking.value = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.Green
                        ),
                        modifier = Modifier.weight(0.1f)
                    )
                }
                
                // send sms on whatsapp
                Row (
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 26.dp)
                        .padding(end = 8.dp),
                    Arrangement.Center
                ) {
                    
                    TextBoldSmall(
                        text = stringResource(id = R.string.share_details_on_whatsapp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            color = colorResource(id = R.color.black),
                            fontSize = 12.sp
                        )
                    )
                    
                    Switch(
                        checked = passengerDetailsViewModel.sendWhatsAppOnBooking.value,
                        onCheckedChange = {
                            passengerDetailsViewModel.sendWhatsAppOnBooking.value = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.Green
                        ),
                        modifier = Modifier.weight(0.1f)
                    )
                }
            }
        }
    }
    
}