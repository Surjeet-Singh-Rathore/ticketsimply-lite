package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import asString
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.PHONE_VALIDATION_COUNT
import com.bitla.ts.utils.dialog.*
import kotlinx.coroutines.*
import timber.log.Timber
import toast

@Composable
fun PaymentOptions(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onPaymentOptionSelection: (String) -> Unit
) {



    if (!passengerDetailsViewModel.isAgentLogin && !passengerDetailsViewModel.isAllowUpiForDirectPgBookingForAgents) {
        CardComponent(shape = RoundedCornerShape(4.dp),
            bgColor = colorResource(id = R.color.white), modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .wrapContentHeight(), onClick = {}) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                TextBoldRegular(
                    text = stringResource(id = R.string.payment_options),
                    modifier = Modifier.wrapContentHeight(),
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorBlackShadow
                        )
                    )
                )


                passengerDetailsViewModel.paymentOptionsList.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .wrapContentWidth()
                                .selectable(
                                    selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                                    onClick = {
                                        passengerDetailsViewModel.selectedPaymentOptionId =
                                            it.id
                                                .toString()
                                                .toInt()
                                        passengerDetailsViewModel.selectedPaymentOption =
                                            it.paymentType!!
                                        onPaymentOptionSelection(
                                            passengerDetailsViewModel.selectedPaymentOption.asString(
                                                context.resources
                                            )
                                        )
                                        passengerDetailsViewModel.isShowUserSubPaymentDialog = true
                                    }
                                )
                                .padding(horizontal = 4.dp),
                            verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                                onClick = {
                                    passengerDetailsViewModel.selectedPaymentOptionId =
                                        it.id.toString().toInt()
                                    passengerDetailsViewModel.selectedPaymentOption = it.paymentType!!
                                    onPaymentOptionSelection(
                                        passengerDetailsViewModel.selectedPaymentOption.asString(
                                            context.resources
                                        )
                                    )
                                    passengerDetailsViewModel.isShowUserSubPaymentDialog = true
                                }
                            )
                            TextNormalSmall(
                                text = it.paymentType?.asString(context.resources) ?: "",
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
        if (passengerDetailsViewModel.payableAmount > 0.0
            && passengerDetailsViewModel.selectedPaymentOptionId != 1 && passengerDetailsViewModel.selectedPaymentOptionId == 11) {
            UserSubPaymentOptions(
                context,
                passengerDetailsViewModel,
                onPaymentOptionSelection = {
                    onPaymentOptionSelection(it)
                }
            )
        }
    }
    else {
        if (!passengerDetailsViewModel.phoneBlock && !passengerDetailsViewModel.isFreeTicketChecked.value) {
            CardComponent(shape = RoundedCornerShape(4.dp),
                bgColor = colorResource(id = R.color.white), modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentHeight(), onClick = {}) {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    TextBoldRegular(
                        text = stringResource(id = R.string.payment_options),
                        modifier = Modifier.wrapContentHeight(),
                        textStyle = TextStyle(
                            color = colorResource(
                                id = R.color.colorBlackShadow
                            )
                        )
                    )

                    // Only cash should be pre selected else not pre selected for agent
//                    if (!passengerDetailsViewModel.isPaymentOptionClicked && passengerDetailsViewModel.paymentOptionsList.isNotEmpty()) {
//                        passengerDetailsViewModel.selectedPaymentOptionId = passengerDetailsViewModel.paymentOptionsList[0].id.toString().toInt()
//                        passengerDetailsViewModel.selectedPaymentOption = passengerDetailsViewModel.paymentOptionsList[0].paymentType!!
//                    }

                    passengerDetailsViewModel.paymentOptionsList.forEach {
                        
//                        Timber.d("paymentTypeX - ${it.paymentType?.asString(context.resources)} == ${passengerDetailsViewModel.selectedPaymentOption?.asString(context.resources)}")
//                        Timber.d("paymentTypeX - ${it.paymentType} == ${passengerDetailsViewModel.selectedPaymentOption}")
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier
                                    .wrapContentWidth()
                                    .selectable(
                                        selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                                        onClick = {
                                            passengerDetailsViewModel.selectedPaymentOptionId =
                                                it.id
                                                    .toString()
                                                    .toInt()
                                            passengerDetailsViewModel.selectedPaymentOption =
                                                it.paymentType!!
                                            onPaymentOptionSelection(
                                                passengerDetailsViewModel.selectedPaymentOption.asString(
                                                    context.resources
                                                )
                                            )
                                            passengerDetailsViewModel.payableAmount = 0.0
                                            passengerDetailsViewModel.isFareBreakupApiCalled = true
                                            passengerDetailsViewModel.isShowAgentSubPaymentDialog =
                                                true

                                            passengerDetailsViewModel.isPaymentOptionClicked = true
                                        }
                                    ),
                                verticalAlignment = CenterVertically
                            ) {
                                RadioButton(
                                    selected = (it.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                                    onClick = {
                                        passengerDetailsViewModel.selectedPaymentOptionId =
                                            it.id.toString().toInt()
                                        passengerDetailsViewModel.selectedPaymentOption = it.paymentType!!
                                        onPaymentOptionSelection(
                                            passengerDetailsViewModel.selectedPaymentOption.asString(
                                                context.resources
                                            )
                                        )
                                        passengerDetailsViewModel.payableAmount = 0.0
                                        passengerDetailsViewModel.isFareBreakupApiCalled = true
                                        passengerDetailsViewModel.isShowAgentSubPaymentDialog = true

                                        passengerDetailsViewModel.isPaymentOptionClicked = true
                                    }
                                )
                                TextNormalSmall(
                                    text = if(it.paymentType == ResourceProvider.TextResource.fromStringId(R.string.wallet)) {
                                        "${it.paymentType?.asString(context.resources)} (Bal: ${passengerDetailsViewModel.getAvailableBalance})"
                                        
                                    } else {
                                        "${it.paymentType?.asString(context.resources)}"
                                    },
                                    modifier = Modifier,
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                        fontSize = 14.sp
                                    ),
                                )
                            }
                        }

//                    Timber.d("agentWalletBalance  = ${passengerDetailsViewModel.getAvailableBalance}")
                    }
                }
            }
        }
    }
    
//    Timber.d("selectedPaymentOptionId  = ${passengerDetailsViewModel.paybleAmount}")

    if (passengerDetailsViewModel.payableAmount > 0.0
        && passengerDetailsViewModel.selectedPaymentOptionId != 1) {
        AgentSubPaymentOptions(
            context,
            passengerDetailsViewModel,
            onPaymentOptionSelection = {
                onPaymentOptionSelection(it)
            }
        )
    }
}

@Composable
fun AgentSubPaymentOptions(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onPaymentOptionSelection: (String) -> Unit,
) {
    
    if (passengerDetailsViewModel.isShowAgentSubPaymentDialog) {
        Dialog(
            onDismissRequest = { passengerDetailsViewModel.isShowAgentSubPaymentDialog = false },
            
            ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(top = 8.dp)
            ) {
                Column {
                    
                    SpaceComponent(modifier = Modifier.padding(top= 8.dp))
                    TextBoldRegular(
                        text = passengerDetailsViewModel.selectedPaymentOption.asString(context.resources),
                        modifier = Modifier.padding(start = 16.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily(Font(R.font.notosans_bold)),
                            fontSize = 18.sp
                        ),
                    )
                    SpaceComponent(modifier = Modifier.padding(top= 16.dp))
                    
                    DividerLine()
                    
                    SpaceComponent(modifier = Modifier.padding(top= 12.dp))
                    
                    passengerDetailsViewModel.paymentSubOptionsList.forEach {
                        
                        //sub
                        Timber.d("paymentTypeX-Sub - ${it.paymentType?.asString(context.resources)} == ${passengerDetailsViewModel.selectedSubPaymentOption?.asString(context.resources)}")
//                        Timber.d("paymentTypeX-Sub - ${it.paymentType} == ${passengerDetailsViewModel.selectedSubPaymentOption}")
                        
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                Modifier
                                    .wrapContentWidth()
                                    .selectable(
                                        selected = (it.paymentType == passengerDetailsViewModel.selectedSubPaymentOption),
                                        onClick = {
                                            passengerDetailsViewModel.selectedSubPaymentOptionId =
                                                it.id.toString()
                                            passengerDetailsViewModel.selectedSubPaymentOption =
                                                it.paymentType!!
                                            onPaymentOptionSelection(
                                                passengerDetailsViewModel.selectedSubPaymentOption.asString(
                                                    context.resources
                                                )
                                            )
                                        }
                                    ),
                                verticalAlignment = CenterVertically
                            ) {
                                RadioButton(
                                    selected = (it.paymentType == passengerDetailsViewModel.selectedSubPaymentOption),
                                    onClick = {
                                        passengerDetailsViewModel.selectedSubPaymentOptionId = it.id.toString()
                                        passengerDetailsViewModel.selectedSubPaymentOption = it.paymentType!!
                                        onPaymentOptionSelection(
                                            passengerDetailsViewModel.selectedSubPaymentOption.asString(
                                                context.resources
                                            )
                                        )
                                    }
                                )
                                Column {
                                    TextNormalRegular(
                                        text = it.paymentType?.asString(context.resources) ?: "",
                                        modifier = Modifier,
                                        textStyle = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.notosans_regular)),
                                        ),
                                    )
                                    
                                    SpaceComponent(modifier = Modifier.padding(top= 8.dp))
                                    DividerLine()
                                }
                            }
                        }
                    }
                    
                    if (passengerDetailsViewModel.selectedSubPaymentOptionId == "SMS") {
                        TextFieldComponentRounded(
                            label = stringResource(id = R.string.mobile_number),
                            value = passengerDetailsViewModel.agentPayViaPhoneNumberSMS,
                            onValueChange = { it ->
                                val digitsOnly = it.filter { it.isDigit() }
                                passengerDetailsViewModel.apply {
                                    if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                        passengerDetailsViewModel.agentPayViaPhoneNumberSMS =
                                            digitsOnly.take(passengerDetailsViewModel.phoneValidationCountPrivilege.value ?: PHONE_VALIDATION_COUNT)
                                    } else {
                                        passengerDetailsViewModel.agentPayViaPhoneNumberSMS = it
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            visualTransformation = VisualTransformation.None
                        )
                    }else{
                        passengerDetailsViewModel.agentPayViaPhoneNumberSMS=""
                    }


                    
                    if (passengerDetailsViewModel.selectedSubPaymentOptionId == "VPA") {
                        TextFieldComponentRounded(
                            label = stringResource(id = R.string.enter_upi_id),
                            value = passengerDetailsViewModel.agentPayViaVPA,
                            onValueChange = {
                                passengerDetailsViewModel.apply {
                                    passengerDetailsViewModel.agentPayViaVPA = it
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                    
                    SpaceComponent(modifier = Modifier.padding(top= 16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 8.dp)
                    ) {
                        
                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.button_light_color).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = R.color.button_light_color))
                                .align(Alignment.Bottom)
                                .weight(1F),
                            text = stringResource(id = R.string.goBack),
                            onClick = {
                                passengerDetailsViewModel.apply {
                                    isShowAgentSubPaymentDialog = false
                                    selectedPaymentOptionId = 1
                                    selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)

                                    isFareBreakupApiCalled = true
                                    passengerDetailsViewModel.isAgentSubPaymentSelected = false
                                    passengerDetailsViewModel.agentPayViaPhoneNumberSMS=""
                                }
                                
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            ),
                            textColor = colorResource(id = R.color.colorPrimary)
                        
                        )
                        
                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.colorPrimary).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = R.color.colorPrimary))
                                .weight(1F),
                            text = stringResource(id = R.string.select),
                            onClick = {
                                if(passengerDetailsViewModel.selectedSubPaymentOptionId == "SMS" && (passengerDetailsViewModel.agentPayViaPhoneNumberSMS.isEmpty() || passengerDetailsViewModel.agentPayViaPhoneNumberSMS.length<10)) {
                                    context.toast("Please Enter Valid Mobile Number")
                                    return@CreateButton
                                }
                                passengerDetailsViewModel.isShowAgentSubPaymentDialog = false
                                passengerDetailsViewModel.isAgentSubPaymentSelected = true
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserSubPaymentOptions(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onPaymentOptionSelection: (String) -> Unit,
) {

    if (passengerDetailsViewModel.isShowUserSubPaymentDialog) {
        Dialog(
            onDismissRequest = { passengerDetailsViewModel.isShowUserSubPaymentDialog = false },

            ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(top = 8.dp)
            ) {
                Column {

                    SpaceComponent(modifier = Modifier.padding(top= 8.dp))
                    TextBoldRegular(
                        text = passengerDetailsViewModel.selectedPaymentOption.asString(context.resources),
                        modifier = Modifier.padding(start = 16.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily(Font(R.font.notosans_bold)),
                            fontSize = 18.sp
                        ),
                    )
                    SpaceComponent(modifier = Modifier.padding(top= 16.dp))

                    DividerLine()

                    SpaceComponent(modifier = Modifier.padding(top= 12.dp))

                    passengerDetailsViewModel.paymentSubOptionsList.forEach {

                        //sub
                        Timber.d("paymentTypeX-Sub - ${it.paymentType?.asString(context.resources)} == ${passengerDetailsViewModel.selectedSubPaymentOption?.asString(context.resources)}")
//                        Timber.d("paymentTypeX-Sub - ${it.paymentType} == ${passengerDetailsViewModel.selectedSubPaymentOption}")

                        Row(
                            modifier = Modifier.wrapContentSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                Modifier
                                    .wrapContentWidth()
                                    .selectable(
                                        selected = (it.paymentType == passengerDetailsViewModel.selectedSubPaymentOption),
                                        onClick = {
                                            passengerDetailsViewModel.selectedSubPaymentOptionId =
                                                it.id.toString()
                                            passengerDetailsViewModel.selectedSubPaymentOption =
                                                it.paymentType!!
                                            onPaymentOptionSelection(
                                                passengerDetailsViewModel.selectedSubPaymentOption.asString(
                                                    context.resources
                                                )
                                            )

                                            passengerDetailsViewModel.enableSelectBtn =
                                                when (passengerDetailsViewModel.selectedSubPaymentOption.asString(
                                                    context.resources
                                                )) {
                                                    context.getString(R.string.pay_via_sms_user) -> {
                                                        passengerDetailsViewModel.userPayViaPhoneNumberSMS.isNotEmpty() && passengerDetailsViewModel.userPayViaPhoneNumberSMS.length == 10
                                                    }

                                                    context.getString(R.string.pay_via_upi_user) -> {
                                                        passengerDetailsViewModel.userPayViaVPA.isNotEmpty()
                                                    }

                                                    else -> {
                                                        true
                                                    }
                                                }
                                        }
                                    ),
                                verticalAlignment = CenterVertically
                            ) {
                                RadioButton(
                                    selected = (it.paymentType == passengerDetailsViewModel.selectedSubPaymentOption),
                                    onClick = {
                                        passengerDetailsViewModel.selectedSubPaymentOptionId = it.id.toString()
                                        passengerDetailsViewModel.selectedSubPaymentOption = it.paymentType!!
                                        onPaymentOptionSelection(
                                            passengerDetailsViewModel.selectedSubPaymentOption.asString(
                                                context.resources
                                            )
                                        )

                                        passengerDetailsViewModel.enableSelectBtn = when (passengerDetailsViewModel.selectedSubPaymentOption.asString(context.resources)) {
                                            context.getString(R.string.pay_via_sms_user) -> {
                                                passengerDetailsViewModel.userPayViaPhoneNumberSMS.isNotEmpty() && passengerDetailsViewModel.userPayViaPhoneNumberSMS.length == 10
                                            }
                                            context.getString(R.string.pay_via_upi_user) -> {
                                                passengerDetailsViewModel.userPayViaVPA.isNotEmpty()
                                            }
                                            else -> {
                                                true
                                            }
                                        }
                                    }
                                )
                                Column {
                                    TextNormalRegular(
                                        text = it.paymentType?.asString(context.resources) ?: "",
                                        modifier = Modifier,
                                        textStyle = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.notosans_regular)),
                                        ),
                                    )

                                    SpaceComponent(modifier = Modifier.padding(top= 8.dp))
                                    DividerLine()
                                }
                            }
                        }
                    }

                    if (passengerDetailsViewModel.selectedSubPaymentOptionId == "SMS") {
                        TextFieldComponentRounded(
                            label = stringResource(id = R.string.enter_mobile_number),
                            value = passengerDetailsViewModel.userPayViaPhoneNumberSMS,
                            onValueChange = {
                                passengerDetailsViewModel.apply {
                                    if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                        passengerDetailsViewModel.userPayViaPhoneNumberSMS =
                                            it.take(passengerDetailsViewModel.phoneValidationCountPrivilege.value!!)
                                    } else {
                                        passengerDetailsViewModel.userPayViaPhoneNumberSMS = it
                                    }
                                }
                                passengerDetailsViewModel.enableSelectBtn = passengerDetailsViewModel.userPayViaPhoneNumberSMS.isNotEmpty() && passengerDetailsViewModel.userPayViaPhoneNumberSMS.length == 10
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.NumberPassword
                            ),
                            visualTransformation = VisualTransformation.None
                        )
                    }

                    if (passengerDetailsViewModel.selectedSubPaymentOptionId == "VPA") {
                        TextFieldComponentRounded(
                            label = stringResource(id = R.string.enter_upi_id),
                            value = passengerDetailsViewModel.userPayViaVPA,
                            onValueChange = {
                                passengerDetailsViewModel.apply {
                                    passengerDetailsViewModel.userPayViaVPA = it
                                }
                                passengerDetailsViewModel.enableSelectBtn = passengerDetailsViewModel.userPayViaVPA.isNotEmpty()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),

                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    SpaceComponent(modifier = Modifier.padding(top= 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 8.dp)
                    ) {

                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.button_light_color).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = R.color.button_light_color))
                                .align(Alignment.Bottom)
                                .weight(1F),
                            text = stringResource(id = R.string.goBack),
                            onClick = {
                                passengerDetailsViewModel.apply {

                                    isShowUserSubPaymentDialog = false
                                    selectedPaymentOptionId = 1
                                    selectedPaymentOption = ResourceProvider.TextResource.fromStringId(R.string.cash)
                                    isFareBreakupApiCalled = true
                                    passengerDetailsViewModel.isUserSubPaymentSelected = false
                                }
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            ),
                            textColor = colorResource(id = R.color.colorPrimary)

                        )

                        CreateButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.colorPrimary).copy(
                                    alpha = 1f
                                )
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .background(colorResource(id = if (passengerDetailsViewModel.enableSelectBtn) R.color.colorPrimary else R.color.colorShadow))
                                .weight(1F),
                            text = stringResource(id = R.string.select),
                            onClick = {
                                if (passengerDetailsViewModel.enableSelectBtn) {
                                    passengerDetailsViewModel.isShowUserSubPaymentDialog = false
                                    passengerDetailsViewModel.isUserSubPaymentSelected = true
                                }
                            },
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.notosans_bold)),
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}