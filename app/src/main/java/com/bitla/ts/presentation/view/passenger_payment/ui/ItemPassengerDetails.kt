package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.*
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.passenger_details_result.*
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.*
import kotlinx.coroutines.*
import timber.log.Timber
import toast
import kotlin.math.roundToInt

@Composable
fun ItemPassengerDetails(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    item: PassengerDetailsResult?,
    paxIndex: Int,
    onMealChecked: (Boolean) -> Unit
) {

    passengerDetailsViewModel.apply {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {

            if (paxIndex == 0 && passengerDataList[paxIndex].isExtraSeat) {
                TextBoldRegular(
                    text = stringResource(id = R.string.extra_seats_details),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily(Font(R.font.notosans_bold)),
                        color = colorResource(id = R.color.colorBlackShadow),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            CardComponent(
                shape = RoundedCornerShape(4.dp),
                bgColor = colorResource(id = R.color.white),
                modifier = Modifier
                    .wrapContentHeight(), onClick = {}
            )
            {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        TextBoldSmall(
                            text = "P${paxIndex+1}:",
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )

                        if (rapidBookingType != 0) {
                            TextBoldSmall(
                                text = stringResource(id = R.string.seat).uppercase(),
                                modifier = Modifier
                                    .weight(1.8f)
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                        }

//                    if (passengerDetailsViewModel.passengerDataList[paxIndex].isExtraSeat) {
//                        TextFieldComponent(context = context,
//                            isError = passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
//                                    && passengerDetailsViewModel.passengerDataList[paxIndex].seatNumber?.isEmpty() == true,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(4f),
//                            value = passengerDetailsViewModel.passengerDataList[paxIndex].seatNumber ?: "",
//                            label = stringResource(id = R.string.seat_no),
//                            placeholder = stringResource(id = R.string.seat_no),
//                            onValueChange = {
//                                when {
//                                    it.isNotEmpty() -> {
//                                        passengerDetailsViewModel.setExtraSeatNo(paxIndex, it)
//                                    }
//
//                                    else -> {
//                                        passengerDetailsViewModel.setExtraSeatNo(paxIndex, it)
//                                    }
//                                }
//                            },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Text,
//                                imeAction = ImeAction.Next
//                            )
//                        )
//                    }

                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            TextBoldSmall(
                                text = item?.seatNumber.toString(),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Box(modifier = Modifier
                            .weight(8f)
                            .padding(end = 8.dp)) {
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                TextNormalSmall(
                                    text = stringResource(id = R.string.fare),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .align(Alignment.CenterVertically),
                                    textStyle = TextStyle(textAlign = TextAlign.End)
                                )

                                TextBoldLarge(
                                    text = if (!item?.fare.isNullOrEmpty()) {
                                        if (item?.fare.toString().contains(",")) {
                                            val fare = item?.fare?.replace(",", "")
                                            "$amountCurrency ${fare?.toDouble()?.convert(currencyFormat)}"
                                        } else {
                                            "$amountCurrency ${item?.fare?.toDouble()?.convert(currencyFormat)}"
                                        }
                                    } else {
                                        "$amountCurrency ${item?.fare}"
                                    },
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        if (paxIndex != 0) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete_red),
                                contentDescription = "Delete Icon",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clickable {
                                        isDeletePassengerClicked = true
                                        passengerDataList.removeAt(paxIndex)
                                        isAllMandatoryFieldsFilled.remove(paxIndex)
                                        noOfSeats =
                                            if (isExtraSeats) passengerDataList.size.toString() else passengerDataList.size.toString()
                                        if (passengerDataList.isNotEmpty()) {
                                            if (!passengerDataList.any { it.isExtraSeat }) {
                                                passengerDataList.forEach {
                                                    totalFare += it.fare?.toDouble() ?: 0.0
                                                    selectedSeatNo = it.seatNumber.toString()
                                                }
                                            } else {
                                                totalFare = 0.0
                                                passengerDataList.forEach {
                                                    totalFare += it.fare?.toDoubleOrNull() ?: 0.0
                                                    individualDiscountAmount += it.discountAmount?.toIntOrNull()
                                                        ?: 0
                                                    bookExtraSeatNoList.add(it.seatNumber.toString())
                                                }

                                                isExtraSeat = true
                                                val commaSeparatedExtraSeats =
                                                    android.text.TextUtils.join(
                                                        ",",
                                                        bookExtraSeatNoList
                                                    )
                                                selectedSeatNo = commaSeparatedExtraSeats
                                            }
                                        }

                                        if (isEnableCampaignPromotionsChecked) {
                                            isPassengerAgeChanged = true
                                        } else {
                                            isFareBreakupApiCalled = true
                                            showShimmer = true
                                        }
                                    }
                            )
                        }
                    }

                    if (rapidBookingType != 0) {
                        DividerLine(modifier = Modifier.padding(top = 4.dp))

                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            if (paxIndex == 0) {
                                TextBoldSmall(
                                    text = stringResource(id = R.string.primary_passenger),
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .padding(top = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                            } else {
                                TextBoldSmall(
                                    text = "${stringResource(id = R.string.passenger).uppercase()} ${paxIndex + 1}",
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .padding(top = 8.dp),
                                    textAlign = TextAlign.Start
                                )
                            }

                            /*TextNormalRegular(
                                text = if (passengerDetailsViewModel.passengerDataList[paxIndex].isFilled) {
                                    stringResource(id = R.string.filled)
                                } else {
                                    stringResource(id = R.string.not_filled)
                                },
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (passengerDetailsViewModel.passengerDataList[paxIndex].isFilled
                                        ) {
                                            colorResource(id = R.color.colorAccent)
                                        } else {
                                            Color.Gray
                                        }
                                    )
                                    .padding(top = 2.dp, bottom = 2.dp),
                                textStyle = TextStyle(color = Color.White, textAlign = TextAlign.Center)
                            )*/

//                        if (paxIndex != 0) {
//
//                        }
                            Image(
                                painter = if (passengerDataList[paxIndex].expand == true && paxIndex >= paxPosition) {
                                    painterResource(id = R.drawable.ic_arrow_up)
                                } else {
                                    painterResource(id = R.drawable.ic_arrow_down)
                                },
                                contentDescription = "arrow",
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .weight(0.2f)
                                    .clickable {

                                        if (paxIndex == 0) {
                                            paxPosition = 0
                                            if (passengerDataList[paxIndex].expand == false) {
                                                isExpand(paxIndex, item = true)
                                            } else {
                                                isExpand(paxIndex, item = false)
                                            }
                                        } else {
                                            if (passengerDataList[paxIndex].expand == false) {
                                                isExpand(paxIndex, item = true)
                                            } else {
                                                isExpand(paxIndex, item = false)
                                            }
                                        }
                                    }
                            )
                        }

                        if (passengerDataList[paxIndex].expand == true && paxIndex >= paxPosition) {
                            PassengerExpandCollapseView(context, paxIndex, passengerDetailsViewModel, item, onMealChecked = {onMealChecked(it)})
                        }

                        if (paxIndex == 0 && passengerDataList.size > 1 && passengerDetailsViewModel.isEnableCopyPassengerCheckbox) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 58.dp)
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .border(
                                        width = 1.dp,
                                        color = colorResource(id = R.color.colorAccent),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(end = 8.dp)
                            ) {
                                Checkbox(

                                    checked = checkedCopyState.value,
                                    onCheckedChange = {

                                        passengerDetailsViewModel.paxPosition = 1
                                        passengerDetailsViewModel.visibleTextField.value = false
                                        passengerDetailsViewModel.checkedCopyState.value = it

                                        if (passengerDataList[paxIndex].expand == false) {
                                            isExpand(paxIndex, item = true)
                                        } else {
                                            isExpand(paxIndex, item = false)
                                        }

                                        for (i in 0 until passengerDataList.size) {

                                            if (i > 0) {
                                                if (it) {
                                                    passengerDetailsViewModel.apply {

//                                                        if ((allowQoalaInsurance.value == true && !selectedSeatDetails.any { it.isExtraSeat })
//                                                            && isInsuranceChecked.value && !country.equals("india", true)
//                                                        ) {
//                                                            setFirstName(i, passengerDataList[paxIndex].firstName ?: "")
//                                                            setLastName(i, passengerDataList[paxIndex].lastName ?: "")
//                                                        }

                                                        if (passengerDetailsViewModel.firstNamePrivilege.value != context.getString(R.string.hide)) {
                                                            setFirstName(i, passengerDataList[paxIndex].firstName ?: "")
                                                        }
                                                        if (passengerDetailsViewModel.lastNamePrivilege.value != context.getString(R.string.hide)) {
                                                            setLastName(i, passengerDataList[paxIndex].lastName ?: "")
                                                        }

                                                        setFirstName(i, passengerDataList[paxIndex].firstName ?: "")
                                                        setLastName(i, passengerDataList[paxIndex].lastName ?: "")
                                                        setName(i, passengerDataList[paxIndex].name ?: "")
                                                        setAge(i, passengerDataList[paxIndex].age ?: "")
                                                        setGender(i, passengerDataList[paxIndex].sex ?: "")
                                                        setSelectedMealTypeText(i, passengerDataList[paxIndex].selectedMealType ?: "")
                                                        setIdType(i, passengerDataList[paxIndex].idType ?: "")
                                                        setIdNumber(i, passengerDataList[paxIndex].idnumber ?: "")
                                                        setAdditionalFare(i, passengerDataList[paxIndex].additionalFare ?: "0")
                                                        setDiscount(i, passengerDataList[paxIndex].discountAmount ?: "")
                                                        if (passengerDataList[paxIndex].isExtraSeat) {
                                                            setExtraSeatFare(i, passengerDataList[paxIndex].fare ?: "")
                                                        }
                                                        isExpand(i, item = passengerDataList[paxIndex].expand == true)

                                                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                                                        isMandatoryFieldsFilled(paxIndex, item = true)
                                                    }

                                                } else {
                                                    passengerDetailsViewModel.apply {
//                                                        if ((allowQoalaInsurance.value == true && !selectedSeatDetails.any { it.isExtraSeat })
//                                                            && isInsuranceChecked.value
//                                                        ) {
//                                                            setFirstName(i, "")
//                                                            setLastName(i, "")
//                                                        }

                                                        if (passengerDetailsViewModel.firstNamePrivilege.value != context.getString(R.string.hide)) {
                                                            setFirstName(i, "")
                                                        }
                                                        if (passengerDetailsViewModel.lastNamePrivilege.value != context.getString(R.string.hide)) {
                                                            setLastName(i, "")
                                                        }
                                                        setName(i, "")
                                                        setAge(i, "")
                                                        setGender(i, "")
                                                        setSelectedMealTypeText(i, "")
                                                        setIdType(i, "")
                                                        setIdNumber(i, "")
                                                        setAdditionalFare(i, "0")
                                                        setDiscount(i, "")
                                                        if (passengerDataList[paxIndex].isExtraSeat) {
                                                            setExtraSeatFare(i, "")
                                                            setExtraSeatNo(i, "")
                                                        }
                                                        isExpand(
                                                            i,
                                                            item = passengerDataList[paxIndex].expand == true
                                                        )
                                                        isAllMandatoryFieldsFilled.remove(i)
                                                        isMandatoryFieldsFilled(i, item = false)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )

                                TextBoldSmall(
                                    text = stringResource(id = R.string.same_details).uppercase(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }

        EnableDisableView(
            paxIndex = paxIndex,
            passengerDetailsViewModel = passengerDetailsViewModel
        )
    }

    passengerDetailsViewModel.apply {
        when {
            /*isInsuranceChecked.value -> {
                LaunchedEffect(passengerDetailsViewModel.visibleTextField.value) {
                    if (passengerDetailsViewModel.visibleTextField.value && passengerDetailsViewModel.primaryMobileNo.isEmpty()) {
                        try {
                            focusRequesterPrimaryMobile.requestFocus()
                        } catch (e: Exception) {
                            Timber.d("${e.message}")
                        }
                    }
                }
            }*/

            /* emailPrivilege.value == stringResource(id = R.string.mandatory) -> {
                 LaunchedEffect(passengerDetailsViewModel.visibleTextField.value) {
                     if (passengerDetailsViewModel.visibleTextField.value && passengerDetailsViewModel.emailId.isEmpty()) {
                         focusRequesterEmailId.requestFocus()
                     }
                 }
             }*/

            namePrivilege.value == stringResource(id = R.string.mandatory)
                    && mobileNoPrivilege.value != stringResource(id = R.string.mandatory)
                    && emailPrivilege.value != stringResource(id = R.string.mandatory) -> {

                /*  LaunchedEffect(passengerDetailsViewModel.visibleTextField.value) {
                      if (passengerDetailsViewModel.visibleTextField.value
                          && paxIndex == 0 && passengerDetailsViewModel.passengerDataList[0].name?.isEmpty() == true
                      ) {
                          focusRequesterName.requestFocus()
                      }
                  }*/
            }

            agePrivilege.value == stringResource(id = R.string.mandatory)
                    && mobileNoPrivilege.value != stringResource(id = R.string.mandatory)
                    && emailPrivilege.value != stringResource(id = R.string.mandatory) -> {

                /* LaunchedEffect(passengerDetailsViewModel.visibleTextField.value) {
                     if (passengerDetailsViewModel.visibleTextField.value
                         && paxIndex == 0 && passengerDetailsViewModel.passengerDataList[0].age?.isEmpty() == true
                     ) {
                         focusRequesterAge.requestFocus()
                     }
                 }*/
            }

            else -> {
                /*    LaunchedEffect(passengerDetailsViewModel.visibleTextField.value) {
                        if (passengerDetailsViewModel.visibleTextField.value
                            && paxIndex == 0 && passengerDetailsViewModel.passengerDataList[0].name?.isEmpty() == true
                        ) {
                            focusRequesterName.requestFocus()
                        }
                    }*/
            }
        }
    }
}

@Composable
fun isInsuranceCheckedMandatory(
    paxIndex: Int,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
): Boolean {

    passengerDetailsViewModel.apply {

        mobileNoPrivilege.value = stringResource(id = R.string.mandatory)
        firstNamePrivilege.value = stringResource(id = R.string.mandatory)
        lastNamePrivilege.value = stringResource(id = R.string.mandatory)
        agePrivilege.value = stringResource(id = R.string.mandatory)
        genderPrivilege.value = stringResource(id = R.string.mandatory)

        return if(phoneValidationCountPrivilege.value != null) {
            ((primaryMobileNo.isNotEmpty()
                    && primaryMobileNo.length == phoneValidationCountPrivilege.value)
                    && passengerDataList[paxIndex].firstName?.isNotEmpty() == true
                    && passengerDataList[paxIndex].lastName?.isNotEmpty() == true
                    && passengerDataList[paxIndex].age?.isNotEmpty() == true
                    && passengerDataList[paxIndex].sex?.isNotEmpty() == true)
        }else
        {
            (primaryMobileNo.isNotEmpty()
                    && passengerDataList[paxIndex].firstName?.isNotEmpty() == true
                    && passengerDataList[paxIndex].lastName?.isNotEmpty() == true
                    && passengerDataList[paxIndex].age?.isNotEmpty() == true
                    && passengerDataList[paxIndex].sex?.isNotEmpty() == true)
        }
    }
}

@Composable
fun isRapidBooking(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>): Boolean {

    val optional = stringResource(id = R.string.optional)
    
    passengerDetailsViewModel.apply {
        LaunchedEffect(rapidBookingType != 0) {
            mobileNoPrivilege.value = optional
            alternateNoPrivilege.value = optional
            emailPrivilege.value = optional
            firstNamePrivilege.value = optional
            lastNamePrivilege.value = optional
            namePrivilege.value = optional
            agePrivilege.value = optional
            genderPrivilege.value = optional
            idTypePrivilege.value = optional
            idNumberPrivilege.value = optional
        }
        return rapidBookingType == 0
    }
}
@Composable
fun EnableDisableView(paxIndex: Int, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {

        if (isRapidBooking(passengerDetailsViewModel)) {
            for (i in 0 until passengerDataList.size) {
                isAllMandatoryFieldsFilled += Pair(paxIndex, true)
            }
            isRapidBooking = "true"
        } else {
            if (isInsuranceChecked.value) {
                privilegeResponseModel?.appPassengerDetailConfig?.apply {
                    LaunchedEffect(isInsuranceChecked.value) {
                        mobileNoPrivilege.value = phoneNumber?.option ?: ""
                        alternateNoPrivilege.value = alternateNo?.option ?: ""
                        emailPrivilege.value = email?.option ?: ""
                    }
                }

                when {
                    isInsuranceCheckedMandatory(paxIndex, passengerDetailsViewModel)
                            && emailPrivilege.value != stringResource(id = R.string.mandatory)
                            && emailId.isNotEmpty() && isEmailValid(passengerDetailsViewModel.emailId)-> {
                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                        isMandatoryFieldsFilled(paxIndex, item = true)
                    }


                    isInsuranceCheckedMandatory(paxIndex, passengerDetailsViewModel)
                            && alternateNoPrivilege.value != stringResource(id = R.string.mandatory)
                            && alternateMobileNo.isNotEmpty() -> {
                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                        isMandatoryFieldsFilled(paxIndex, item = true)
                    }

                    isInsuranceCheckedMandatory(paxIndex, passengerDetailsViewModel)
                            && alternateNoPrivilege.value == stringResource(id = R.string.mandatory)
                            && alternateMobileNo.isNotEmpty() -> {
                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                        isMandatoryFieldsFilled(paxIndex, item = true)
                    }

                    isInsuranceCheckedMandatory(paxIndex, passengerDetailsViewModel)
                            && emailPrivilege.value != stringResource(id = R.string.mandatory)
                            && alternateNoPrivilege.value != stringResource(id = R.string.mandatory) -> {
                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                        isMandatoryFieldsFilled(paxIndex, item = true)
                    }


                    emailPrivilege.value == stringResource(id = R.string.mandatory)
                            && emailId.isNotEmpty() && isEmailValid(passengerDetailsViewModel.emailId)
                            && alternateNoPrivilege.value == stringResource(id = R.string.mandatory)
                            && alternateMobileNo.isNotEmpty()
                            && alternateMobileNo.length == phoneValidationCountPrivilege.value
                            && isInsuranceCheckedMandatory(paxIndex, passengerDetailsViewModel) -> {
                        isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                        isMandatoryFieldsFilled(paxIndex, item = true)
                    }

                    else -> {
                        isAllMandatoryFieldsFilled.remove(paxIndex)
                        isMandatoryFieldsFilled(paxIndex, item = false)
                    }
                }

                paddingValues = 8.dp
            } else {

                paddingValues = 0.dp

                var isMobileNoMandatory: Boolean
                var isAlternateNoMandatory: Boolean
                var isEmailIdMandatory: Boolean
                var isNameMandatory: Boolean
                var isFirstNameandatory: Boolean
                var isLastNameMandatory: Boolean
                var isAgeMandatory: Boolean
                var isGenderMandatory: Boolean
                var isIdTypeMandatory: Boolean
                var isIdNumberMandatory: Boolean

                privilegeResponseModel?.appPassengerDetailConfig?.apply {
                    // the confirm & book btn not enable if we add LaunchedEffect here
                   // LaunchedEffect(rapidBookingType == 0 || isInsuranceChecked.value) {
                        mobileNoPrivilege.value = phoneNumber?.option ?: ""
                        alternateNoPrivilege.value = alternateNo?.option ?: ""
                        emailPrivilege.value = email?.option ?: ""
                        firstNamePrivilege.value = firstName?.option ?: ""
                        lastNamePrivilege.value = lastName?.option ?: ""
                    //}
                    
                    if (selectedSeatDetails.any { it.isExtraSeat }) {
                        namePrivilege.value = stringResource(id = R.string.mandatory)
                        agePrivilege.value = stringResource(id = R.string.mandatory)
                        genderPrivilege.value = stringResource(id = R.string.mandatory)
                    } else {
                       // LaunchedEffect(rapidBookingType == 0 || isInsuranceChecked.value) {
                            namePrivilege.value = name?.option ?: ""
                            agePrivilege.value = age?.option ?: ""
                            genderPrivilege.value = title?.option ?: ""
                       // }
                        
                    }
                    LaunchedEffect(rapidBookingType == 0 || isInsuranceChecked.value) {
                        idTypePrivilege.value = iDType?.option ?: ""
                        idNumberPrivilege.value = iDNumber?.option ?: ""
                    }

                    isMobileNoMandatory = mobileNoPrivilege.value == stringResource(id = R.string.mandatory)
                    isAlternateNoMandatory = alternateNoPrivilege.value == stringResource(id = R.string.mandatory)
                    isEmailIdMandatory = emailPrivilege.value == stringResource(id = R.string.mandatory)
                    isNameMandatory = namePrivilege.value == stringResource(id = R.string.mandatory)
                    isFirstNameandatory = firstNamePrivilege.value == stringResource(id = R.string.mandatory)
                    isLastNameMandatory = lastNamePrivilege.value == stringResource(id = R.string.mandatory)
                    isAgeMandatory = agePrivilege.value == stringResource(id = R.string.mandatory)
                    isGenderMandatory = genderPrivilege.value == stringResource(id = R.string.mandatory)
                    isIdTypeMandatory = idTypePrivilege.value == stringResource(id = R.string.mandatory)
                    isIdNumberMandatory = idNumberPrivilege.value == stringResource(id = R.string.mandatory)

                    if (isMobileNoMandatory) {
                        mandatoryMap += Pair(0, phoneNumber?.option ?: "")
                        if (country != null && country.equals("India", true)) {
                            when {
                                passengerDetailsViewModel.primaryMobileNo.isNotEmpty()
                                        && passengerDetailsViewModel.primaryMobileNo.length == passengerDetailsViewModel.phoneValidationCountPrivilege.value
                                -> {
                                    passengerDataList[paxIndex].paxMandatoryMap += Pair(0, true)
                                }
                                else -> {
                                    passengerDataList[paxIndex].paxMandatoryMap.remove(0)
                                }
                            }
                        } else {
                            when {
                                passengerDetailsViewModel.primaryMobileNo.isNotEmpty()
                                -> {
                                    passengerDataList[paxIndex].paxMandatoryMap += Pair(0, true)
                                }
                                else -> {
                                    passengerDataList[paxIndex].paxMandatoryMap.remove(0)
                                }
                            }
                        }

                    }
                    if (isAlternateNoMandatory) {
                        mandatoryMap += Pair(1, alternateNo?.option ?: "")

                        if (country != null && country.equals("India", true)) {
                            when {
                                passengerDetailsViewModel.alternateMobileNo.isNotEmpty()
                                        && passengerDetailsViewModel.alternateMobileNo.length == passengerDetailsViewModel.phoneValidationCountPrivilege.value
                                -> {
                                    passengerDataList[paxIndex].paxMandatoryMap += Pair(1, true)
                                }
                                else -> {
                                    passengerDataList[paxIndex].paxMandatoryMap.remove(1)
                                }
                            }
                        } else {
                            when {
                                passengerDetailsViewModel.alternateMobileNo.isNotEmpty()
                                -> {
                                    passengerDataList[paxIndex].paxMandatoryMap += Pair(1, true)
                                }
                                else -> {
                                    passengerDataList[paxIndex].paxMandatoryMap.remove(1)
                                }
                            }
                        }

                    }
                    if (isEmailIdMandatory) {
                        mandatoryMap += Pair(2, email?.option ?: "")
                        when {
                            passengerDetailsViewModel.emailId.isNotEmpty() && isEmailValid(passengerDetailsViewModel.emailId)
                            -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(2, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(2)
                            }
                        }
                    }
                    /*-----------------------------*/
                    if (isNameMandatory) {
                        mandatoryMap += Pair(3, name?.option ?: "")
                        when {
                            passengerDataList[paxIndex].name?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(3, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(3)
                            }
                        }
                    }
                    if (isAgeMandatory) {
                        mandatoryMap += Pair(4, age?.option ?: "")
                        when {
                            passengerDataList[paxIndex].age?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(4, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(4)
                            }
                        }
                    }
                    if (isGenderMandatory) {
                        mandatoryMap += Pair(5, title?.option ?: "")
                        when {
                            passengerDataList[paxIndex].sex?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(5, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(5)
                            }
                        }
                    }
                    if (passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }) {
                        mandatoryMap += Pair(6, "ExtraSeat")
                        when {
                            passengerDataList[paxIndex].seatNumber?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(6, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(6)
                            }
                        }
                    }
                    if (isFirstNameandatory) {
                        mandatoryMap += Pair(7, firstName?.option ?: "")
                        when {
                            passengerDataList[paxIndex].firstName?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(7, true)
                            }

                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(7)
                            }
                        }
                    }
                    if (isLastNameMandatory) {
                        mandatoryMap += Pair(8, lastName?.option ?: "")
                        when {
                            passengerDataList[paxIndex].lastName?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(8, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(8)
                            }
                        }
                    }
                    if (isIdTypeMandatory) {
                        mandatoryMap += Pair(9, iDType?.option ?: "")
                        when {
                            passengerDataList[paxIndex].idType?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(9, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(9)
                            }
                        }
                    }
                    if (isIdNumberMandatory) {
                        mandatoryMap += Pair(10, iDNumber?.option ?: "")
                        when {
                            passengerDataList[paxIndex].idnumber?.isNotEmpty() == true -> {
                                passengerDataList[paxIndex].paxMandatoryMap += Pair(10, true)
                            }
                            else -> {
                                passengerDataList[paxIndex].paxMandatoryMap.remove(10)
                            }
                        }
                    }
                }

                if (passengerDataList[paxIndex].paxMandatoryMap.size == passengerDetailsViewModel.mandatoryMap.size) {
                    isAllMandatoryFieldsFilled += Pair(paxIndex, true)
                    isMandatoryFieldsFilled(paxIndex, item = true)
                } else {
                    isAllMandatoryFieldsFilled.remove(paxIndex)
                    isMandatoryFieldsFilled(paxIndex, item = false)
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PassengerExpandCollapseView(
    context: Context,
    paxIndex: Int,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    item: PassengerDetailsResult?,
    onMealChecked: (Boolean) -> Unit
) {
    var expandedIdType by remember { mutableStateOf(false) }
    var selectedIdTypeText by remember { mutableStateOf("") }
    var selectedIdCardTypeId by remember { mutableStateOf(0) }
    val pattern = remember { Regex("^\\d+\$") }

    Row(modifier = Modifier.padding(top = 8.dp, bottom = paddingValues)) {

        if (!passengerDetailsViewModel.isInsuranceChecked.value) {
            passengerDetailsViewModel.enableInsuranceCheckboxForBooking.value = false
        }

//        else {
//            passengerDetailsViewModel.setFirstName(paxIndex, "")
//            passengerDetailsViewModel.setLastName(paxIndex, "")
//        }

        if (passengerDetailsViewModel.firstNamePrivilege.value != stringResource(id = R.string.hide)) {
            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.firstNamePrivilege.value == stringResource(id = R.string.mandatory)
                        && passengerDetailsViewModel.passengerDataList[paxIndex].firstName?.isEmpty() == true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                // .focusRequester(focusRequester = passengerDetailsViewModel.focusRequesterFirstName)
                ,
                value = passengerDetailsViewModel.passengerDataList[paxIndex].firstName ?: "",
                label = stringResource(id = R.string.first_name),
                placeholder = stringResource(id = R.string.first_name),
                onValueChange = {
                    if (it.length <= passengerDetailsViewModel.maxChar){
                        passengerDetailsViewModel.setFirstName(paxIndex, it)
                    }
                    passengerDetailsViewModel.hasPassengerData.value = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }

        if (passengerDetailsViewModel.lastNamePrivilege.value != stringResource(id = R.string.hide)
        ) {
            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.lastNamePrivilege.value == stringResource(id = R.string.mandatory)
                        && passengerDetailsViewModel.passengerDataList[paxIndex].lastName?.isEmpty() == true,
                modifier = Modifier
                    .weight(1f)
                //.focusRequester(focusRequester = passengerDetailsViewModel.focusRequesterLastName)
                ,
                value = passengerDetailsViewModel.passengerDataList[paxIndex].lastName ?: "",
                label = stringResource(id = R.string.last_name),
                placeholder = stringResource(id = R.string.last_name),
                onValueChange = {
                    if (it.length <= passengerDetailsViewModel.maxChar){
                        passengerDetailsViewModel.setLastName(paxIndex, it)
                    }
                    passengerDetailsViewModel.hasPassengerData.value = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }
    }


    if (passengerDetailsViewModel.namePrivilege.value != stringResource(id = R.string.hide)
        && !passengerDetailsViewModel.isInsuranceChecked.value
    ) {
        TextFieldComponent(context = context,
            isError = passengerDetailsViewModel.namePrivilege.value == stringResource(
                id = R.string.mandatory
            ) && passengerDetailsViewModel.passengerDataList[paxIndex].name?.isEmpty() == true,
            modifier = Modifier
                // .focusRequester(passengerDetailsViewModel.focusRequesterName)
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            value = passengerDetailsViewModel.passengerDataList[paxIndex].name ?: "",
            label = stringResource(id = R.string.name),
            placeholder = stringResource(id = R.string.name),
            onValueChange = {
                passengerDetailsViewModel.apply {
                    if (it.length <= passengerDetailsViewModel.maxChar){
                        setName(paxIndex, it)
                    }
                    hasPassengerData.value = true
                }

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    }

    Row(modifier = Modifier.padding(
        top = if (passengerDetailsViewModel.namePrivilege.value != stringResource(id = R.string.hide)) {
            0.dp
        } else {
            8.dp
        }
    )) {

        if (passengerDetailsViewModel.agePrivilege.value != stringResource(id = R.string.hide)) {
            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.agePrivilege.value == stringResource(
                    id = R.string.mandatory
                ) && passengerDetailsViewModel.passengerDataList[paxIndex].age?.isEmpty() == true,
                modifier = Modifier
                    // .focusRequester(passengerDetailsViewModel.focusRequesterAge)
                    .weight(1.2f)
                    .onFocusChanged {
                        if(!it.hasFocus && !passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)) {
                            passengerDetailsViewModel.isFareBreakupApiCalled = true
                        }
                    },
                value = passengerDetailsViewModel.passengerDataList[paxIndex].age ?: "",
                label = stringResource(id = R.string.age),
                placeholder = stringResource(id = R.string.age),
                onValueChange = {
                    passengerDetailsViewModel.apply {
                        setAge(paxIndex, it.take(2))
                        hasPassengerData.value = true
                    }

                    passengerDetailsViewModel.isPassengerAgeChanged = true

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        if (passengerDetailsViewModel.isBookingTypeValueChanged){
            LaunchedEffect(passengerDetailsViewModel.isBookingTypeValueChanged) {
                passengerDetailsViewModel.isFareBreakupApiCalled = true
                passengerDetailsViewModel.isBookingTypeValueChanged = false
            }
        }

        //        LaunchedEffect additionalFare
        if (passengerDetailsViewModel.isAdditionalFareValueChanged){
            LaunchedEffect(passengerDetailsViewModel.isAdditionalFareValueChanged) {
                delay(2000)
                passengerDetailsViewModel.isFareBreakupApiCalled = true
                passengerDetailsViewModel.isAdditionalFareValueChanged = false
            }
        }
//        ........LaunchedEffect discountAmount.........
        if (passengerDetailsViewModel.isDiscountAmountChanged){
            LaunchedEffect(passengerDetailsViewModel.isDiscountAmountChanged) {
                // this check is optional if we want the value to emit from the start
//                if (passengerDetailsViewModel.passengerDataList[paxIndex].discountAmount.toString().isBlank()) {
//                    return@LaunchedEffect
//                }
                delay(2000)
                passengerDetailsViewModel.isFareBreakupApiCalled = true
                passengerDetailsViewModel.isDiscountAmountChanged = false
            }
        }

        LaunchedEffect(passengerDetailsViewModel.genderPrivilege) {
          if (passengerDetailsViewModel.privilegeResponseModel?.country != null &&
              passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)){
              if (passengerDetailsViewModel.passengerDataList[paxIndex].sex.isNullOrEmpty()) {
                  passengerDetailsViewModel.setGender(paxIndex, "Mr")
              }
          }
        }
        if (passengerDetailsViewModel.genderPrivilege.value != stringResource(id = R.string.hide)) {
            // remove space in case of age option hidden
            if (passengerDetailsViewModel.agePrivilege.value != stringResource(
                    id = R.string.hide
                )){SpaceComponent(modifier = Modifier.width(4.dp))}

            TextBoldRegular(text = stringResource(id = R.string.male_complete),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.notosans_regular)),
                    color = if (passengerDetailsViewModel.passengerDataList[paxIndex].sex == "Mr") {
                        colorResource(id = R.color.white)
                    } else {
                        colorResource(id = R.color.colorBlackShadow)
                    }
                ),
                modifier = Modifier
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (passengerDetailsViewModel.genderPrivilege.value == stringResource(
                                id = R.string.mandatory
                            )
                            && (passengerDetailsViewModel.passengerDataList[paxIndex].sex?.isEmpty() == true)
                        ) {
                            colorResource(id = R.color.colorRed)
                        } else {
                            colorResource(id = R.color.colorBlackShadow)
                        },
                        RoundedCornerShape(2.dp)
                    )
                    .background(
                        if (passengerDetailsViewModel.passengerDataList[paxIndex].sex == "Mr") {
                            colorResource(id = R.color.colorAccent)
                        } else {
                            Color.White
                        }
                    )
                    .weight(0.9f)
                    .clickable {
                        passengerDetailsViewModel.apply {
                            setGender(paxIndex, "Mr")
                        }
                    }
                    .padding(top = 16.dp))

            TextBoldRegular(
                text = stringResource(id = R.string.female_complete),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.notosans_regular)),
                    color = if (passengerDetailsViewModel.passengerDataList[paxIndex].sex == "Ms") {
                        colorResource(id = R.color.white)
                    } else {
                        colorResource(id = R.color.colorBlackShadow)
                    }
                ),
                modifier = Modifier
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = if (passengerDetailsViewModel.genderPrivilege.value == stringResource(
                                id = R.string.mandatory
                            )
                            && (passengerDetailsViewModel.passengerDataList[paxIndex].sex?.isEmpty() == true)
                        ) {
                            colorResource(id = R.color.colorRed)
                        } else {
                            colorResource(id = R.color.colorBlackShadow)
                        },
                        RoundedCornerShape(2.dp)
                    )
                    .background(
                        if (passengerDetailsViewModel.passengerDataList[paxIndex].sex == "Ms") {
                            colorResource(id = R.color.colorAccent)
                        } else {
                            Color.White
                        }
                    )
                    .weight(0.8f)
                    .padding(top = 16.dp)
                    .clickable {
                        passengerDetailsViewModel.apply {
                            setGender(paxIndex, "Ms")
                        }
                    }
            )
        }
    }

    Row(modifier = Modifier.padding(top = 8.dp)) {

        if (passengerDetailsViewModel.idTypePrivilege.value != stringResource(id = R.string.hide)
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .padding(end = 8.dp)
                    .defaultMinSize(minHeight = 52.dp),
                expanded = expandedIdType,
                onExpandedChange = {
                    expandedIdType = !expandedIdType
                }
            ) {
                TextField(
                    isError = passengerDetailsViewModel.idTypePrivilege.value == stringResource(id = R.string.mandatory)
                            && passengerDetailsViewModel.passengerDataList[paxIndex].idCardType?.isEmpty() == true,
                    readOnly = true,
                    value = passengerDetailsViewModel.passengerDataList[paxIndex].idCardType ?: "",
                    onValueChange = { },
                    label = {
                        Text(
                            text = stringResource(id = R.string.id_type),
                            fontSize = 14.sp,
                            style = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                            fontFamily = FontFamily(Font(R.font.notosans_regular))
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedIdType
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = colorResource(id = R.color.colorAccent),
                        cursorColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedIdType,
                    onDismissRequest = {
                        expandedIdType = false
                    }
                ) {
                    passengerDetailsViewModel.idTypeList.forEach { selectionId ->
                        DropdownMenuItem(
                            onClick = {
                                selectedIdTypeText = selectionId.value
                                selectedIdCardTypeId = selectionId.id
                                expandedIdType = false

                                passengerDetailsViewModel.setIdType(paxIndex, selectedIdTypeText)
                                passengerDetailsViewModel.setIdCardTypeId(paxIndex, selectedIdCardTypeId)
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
        }

        if (passengerDetailsViewModel.idNumberPrivilege.value != stringResource(id = R.string.hide)) {
            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.idNumberPrivilege.value == stringResource(id = R.string.mandatory
                ) && passengerDetailsViewModel.passengerDataList[paxIndex].idCardNumber?.isEmpty() == true,
                modifier = Modifier
                    .weight(1.4f)
                    .padding(top = 1.dp),
                value = passengerDetailsViewModel.passengerDataList[paxIndex].idCardNumber ?: "",
                label = stringResource(id = R.string.id_number),
                placeholder = stringResource(id = R.string.id_number),
                onValueChange = {
                   // passengerDetailsViewModel.setIdNumber(paxIndex, it)

                    passengerDetailsViewModel.apply {
                        setIdNumber(paxIndex, it)
                        hasPassengerData.value = true
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }
    }

    if (passengerDetailsViewModel.isMealRequired.value == true
        && !passengerDetailsViewModel.passengerDataList[paxIndex].isExtraSeat
    ) {
        SetMealType(passengerDetailsViewModel, paxIndex, true, onMealChecked = {
            onMealChecked(it)
        })
    }


    if (passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }) {
        passengerDetailsViewModel.isAdditionalFarePrivilege.value = false
        //  passengerDetailsViewModel.isDiscountPrivilege.value = false // it was showing discount even privilege is not visible
    }

    if (passengerDetailsViewModel.isBima==false){
        if (!passengerDetailsViewModel.isExtraSeat) {
            if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true
                || passengerDetailsViewModel.isDiscountPrivilege.value == false) {

                if(passengerDetailsViewModel.promotionCouponCode.isEmpty()){
                    TextBoldSmall(
                        text =
                        if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true
                            && passengerDetailsViewModel.isDiscountPrivilege.value == false
                        ) {
                            stringResource(id = R.string.additional_fare_discount).uppercase()
                        } else if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true) {
                            stringResource(id = R.string.additional_fare).uppercase()
                        } else if (passengerDetailsViewModel.isDiscountPrivilege.value == false) {
                            stringResource(id = R.string.discount).uppercase()
                        } else {
                            ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 52.dp)
                            .padding(top = paddingValues),
                        textAlign = TextAlign.Start
                    )
                }

            }
        }

        if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true) {
            TextFieldComponent(
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .defaultMinSize(minHeight = 52.dp),
                value = if(passengerDetailsViewModel.passengerDataList[paxIndex].additionalFare == "0.0"){
                    "0"
                } else {
                    passengerDetailsViewModel.passengerDataList[paxIndex].additionalFare ?: "0"
                },
                label = stringResource(id = R.string.additional_fare),
                placeholder = stringResource(id = R.string.additional_fare),
                onValueChange = {
                    if (it.isNotEmpty())
                        passengerDetailsViewModel.setAdditionalFare(paxIndex, getValidatedNumber(it))
                    else {
                        passengerDetailsViewModel.setAdditionalFare(paxIndex, "")
                    }
                    passengerDetailsViewModel.isAdditionalFareValueChanged = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        if(passengerDetailsViewModel.promotionCouponCode.isEmpty()){
            SetDiscount(
                context = context,
                paxIndex = paxIndex,
                passengerDetailsViewModel = passengerDetailsViewModel,
                bookingTypeId = passengerDetailsViewModel.selectedBookingTypeId
            )
        }

    }


    if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isNotEmpty()) {

        passengerDetailsViewModel.apply {

            TextFieldComponent(context = context,
                isEnable = (isEnableCampaignPromotions && isEnableCampaignPromotionsChecked),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(52.dp),
                value = passengerDataList[paxIndex].discountAmount ?: "",
                label = stringResource(id = R.string.campaign_and_promotions_discount),
                placeholder = stringResource(id = R.string.campaign_and_promotions_discount),
                onValueChange = {
                    discountAmount = it
                    if (discountAmount.isNotEmpty()) {
                        isSeatWiseDiscountEdit = true

                        val maxDiscountValue = perSeatDiscountList.find { it?.seatNo.equals(passengerDataList[paxIndex].seatNumber) }?.discountValue

                        try {
                            if (discountAmount.toDouble() > (maxDiscountValue ?: 0.0)) {
                                discountAmount = "$maxDiscountValue"
                                setDiscount(paxIndex,discountAmount)
                                context.toast("${context.getString(R.string.discount_validation)} $maxDiscountValue")
                            } else {
                                setDiscount(paxIndex,discountAmount)
                            }
                        } catch (_: Exception) { }
                    }
                    if (it.isEmpty()) {
                        setDiscount(paxIndex, "")
                    }
                    passengerDetailsViewModel.isDiscountAmountChanged = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }
    }

    if (passengerDetailsViewModel.passengerDataList[paxIndex].isExtraSeat) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
                        && passengerDetailsViewModel.passengerDataList[paxIndex].fare?.isEmpty() == true || passengerDetailsViewModel.passengerDataList[paxIndex].fare == "0",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(52.dp),
                value = item?.fare ?: "",
                label = stringResource(id = R.string.fare),
                placeholder = stringResource(id = R.string.fare),
                onValueChange = {
                    passengerDetailsViewModel.setExtraSeatFare(
                        paxIndex,
                        it
                    )
                    passengerDetailsViewModel.isExtraSeatChanged = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            TextFieldComponent(context = context,
                isError = passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
                        && passengerDetailsViewModel.passengerDataList[paxIndex].seatNumber?.isEmpty() == true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(52.dp),
                value = passengerDetailsViewModel.passengerDataList[paxIndex].seatNumber ?: "",
                label = stringResource(id = R.string.seat_no),
                placeholder = stringResource(id = R.string.seat_no),
                onValueChange = {
                    when {
                        it.isNotEmpty() -> {
                            passengerDetailsViewModel.setExtraSeatNo(paxIndex, it.filter{ it.isLetter() || it.isDigit()})  // it.isWhitespace()
                        }
                        else -> {
                            passengerDetailsViewModel.setExtraSeatNo(paxIndex, it.filter{ it.isLetter() || it.isDigit()})
                        }
                    }
                    passengerDetailsViewModel.isExtraSeatChanged = true
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
        }
    }
}

@Composable
fun SetDiscount(
    context: Context,
    paxIndex: Int,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    bookingTypeId:Int,
) {
    passengerDetailsViewModel.apply {
        if (!isExtraSeat) {
            if ((passengerDetailsViewModel.isDiscountPrivilege.value == false
                        && !passengerDetailsViewModel.isAllowedEditFare
                        && !passengerDetailsViewModel.isAgentLogin
                        && passengerDetailsViewModel.roleType != context.getString(R.string.role_field_officer))
                || (passengerDetailsViewModel.roleType == context.getString(R.string.role_field_officer)
                        && passengerDetailsViewModel.privilegeResponseModel?.boLicenses?.allowDiscountForBooking == true
                        && passengerDetailsViewModel.isDiscountPrivilege.value == false)
            ) {

                setDefaultBranchRoleDiscount(
                    paxIndex = paxIndex,
                    bookingTypeId = bookingTypeId,
                    none = context.getString(R.string.none),
                    fixed = context.getString(R.string.fixed).uppercase(),
                    percentage = context.getString(R.string.percentage).uppercase(),
                    roleDiscountType = context.getString(R.string.role_discount_type),
                    branchDiscountType = context.getString(R.string.branch_discount_type)
                )

                TextFieldComponent(context = context,
                    isEnable = !(branchRoleDiscountType.isNotEmpty()
                            && branchRoleDiscountType != context.getString(R.string.none)
                            && applyRoleOrBranchDiscountAtTimeOfBooking
                            && !selectedSeatDetails.any { it.isExtraSeat }  ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    value = if (applyRoleOrBranchDiscountAtTimeOfBooking || isSeatWiseDiscountEdit) {
                        getValidatedNumber("${passengerDataList[paxIndex].discountAmount}")
                    } else {
                        "0"
                    } ?: "",
                    label = if (isAllowToApplyDiscountOnBookingPageWithPercentage
                        && (discountValue.toDouble() != 0.0 || discountValue.toInt() != 0)
                    ) {
                        stringResource(id = R.string.discount_label_percentage)
                    } else {
                        stringResource(id = R.string.discount)
                    },
                    placeholder = stringResource(id = R.string.discount),
                    onValueChange = {
                        discountAmount = it

                        if (discountAmount.isNotEmpty()) {
                            isSeatWiseDiscountEdit = true
                        }
                        setDiscount(paxIndex, getValidatedNumber(discountAmount))



                        if (!selectedSeatDetails.any { it.isExtraSeat }) {
                            setBranchRoleDiscountConfiguration(
                                paxIndex = paxIndex,
                                none = context.getString(R.string.none),
                                fixed = context.getString(R.string.fixed).uppercase(),
                                percentage = context.getString(R.string.percentage).uppercase(),
                                bookingTypeId = bookingTypeId,
                                roleDiscountType = context.getString(R.string.role_discount_type),
                                branchDiscountType = context.getString(R.string.branch_discount_type),
                                allowedMaxDiscountMessage = context.getString(R.string.discount_validation_percentage),
                                allowedBranchRoleMaxDiscountMessage = context.getString(R.string.discount_validation),
                            )
                        }

                        if (it.isEmpty()) {
                            setDiscount(paxIndex, "0")
                        }
                        if(passengerDataList[paxIndex].discountAmount != "0" && passengerDataList[paxIndex].discountAmount != ""){
                            setPromotionCouponVisibility(false)
                        }else{
                            setPromotionCouponVisibility(true)
                        }

                        passengerDetailsViewModel.isDiscountAmountChanged = true




                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetMealType(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    paxIndex: Int,
    isMealSelected: Boolean,
    onMealChecked : (Boolean)->Unit
) {
    var expandedMealType by remember { mutableStateOf(false) }


    Row {
        Checkbox(
            modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
            checked = passengerDetailsViewModel.passengerDataList[paxIndex].isMealSelected
                ?: isMealSelected,
            onCheckedChange = {
                passengerDetailsViewModel.apply {
                    passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType = true
                    passengerDetailsViewModel.hasPassengerData.value = true
                    if (isFreezeMealSelection.value == true) {
                        setIsMealSelected(paxIndex, item = true)
                        onMealChecked(true)
                    } else {
                        if (passengerDataList[paxIndex].isMealSelected == null && !isMealSelected) {
                            setIsMealSelected(paxIndex, item = true)
                        } else if (!passengerDataList[paxIndex].isMealSelected) {
                            setIsMealSelected(paxIndex, item = true)
                        } else {
                            setIsMealSelected(paxIndex, item = false)
                            setSelectedMealTypeText(paxIndex, item = "")
                        }
                        onMealChecked(it)
                    }
                }
            }
        )

        TextBoldSmall(
            text = stringResource(id = R.string.meal_type).uppercase(),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .absoluteOffset((-12).dp, 0.dp),
            textAlign = TextAlign.Start
        )
    }


    if (passengerDetailsViewModel.passengerDataList[paxIndex].isMealSelected != false || isMealSelected) {
        if (passengerDetailsViewModel.isMealNoType.value == false) {
            val passenger = passengerDetailsViewModel.passengerDataList[paxIndex]

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                expanded = expandedMealType,
                onExpandedChange = {
                    expandedMealType = !expandedMealType
                }
            ) {
                TextFieldComponent(context = null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = passenger.selectedMealType ?: "",
                    onValueChange = { },
                    placeholder = stringResource(id = R.string.meal_type),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedMealType
                        )
                    },
                    label = stringResource(id = R.string.meal_type) ,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                    ),
                    readOnly = true
                )

                ExposedDropdownMenu(
                    expanded = expandedMealType,
                    onDismissRequest = {
                        expandedMealType = false
                    }
                ) {
                    passengerDetailsViewModel.mealList.forEach { selection ->
                        DropdownMenuItem(
                            onClick = {
                                passenger.selectedMealType = selection.value
                                expandedMealType = false
                                passengerDetailsViewModel.setSelectedMealTypeId(paxIndex, selection.id)
                            }
                        ) {
                            TextNormalRegular(
                                text = selection.value,
                                modifier = Modifier,
                                textStyle = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                            )
                        }
                    }
                }
            }
        }
    }



}