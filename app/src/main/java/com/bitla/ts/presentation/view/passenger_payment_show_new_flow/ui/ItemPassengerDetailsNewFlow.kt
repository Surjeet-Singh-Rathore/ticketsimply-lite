package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import android.content.*
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.bitla.ts.utils.common.*
import kotlinx.coroutines.*
import timber.log.*
import toast

@Composable
fun ItemPassengerDetailsNewFlow(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    item: PassengerDetailsResult?,
    paxIndex: Int,
    onMealChecked: (Boolean) -> Unit,
    originalFares: MutableList<String>,
    originalSeatList: MutableList<String>,
    onFareChange: (String, String, Boolean) -> Unit
) {

    var currentFare by remember { mutableStateOf(item?.fare ?: "") }
    var previousFare by remember { mutableStateOf(item?.fare ?: "") }
    var textFieldFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun hasFareChanged(seat: String, newFare: String): Boolean {
        val seatIndex = originalSeatList.indexOf(seat)

        if (seatIndex != -1) {
            val originalFare = originalFares[seatIndex].toDoubleOrNull() ?: 0.0
            val modifiedFare = newFare.toDoubleOrNull() ?: 0.0

            return originalFare != modifiedFare
        }
        return false
    }

    fun handleFareChange() {
        if (currentFare != previousFare) {
            val seatNumber = item?.seatNumber.toString()
            val fareChanged = if ( passengerDetailsViewModel.country.equals("india", true) &&
                passengerDetailsViewModel.editFareMandatoryForAgentUser &&
                passengerDetailsViewModel.isAllowedEditFare )
            {
                hasFareChanged(seatNumber, currentFare)
            } else {
                true
            }
            onFareChange(seatNumber, currentFare, fareChanged)
            previousFare = currentFare
        }
    }

    BackHandler(enabled = textFieldFocused) {
        handleFareChange()
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    passengerDetailsViewModel.apply {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp)
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
                shape = RoundedCornerShape(8.dp),
                bgColor = colorResource(id = R.color.white),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
                    .wrapContentHeight(),
                onClick = {}
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (passengerDataList[paxIndex].expand == false) {
                                isExpand(paxIndex, item = true)
                            } else {
                                isExpand(paxIndex, item = false)
                            }
                        }
                        .wrapContentHeight()
                        .padding(start = 14.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        
                        TextBoldSmall(
                            text = "Seat:",
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )
//
                        Box(modifier = Modifier.padding(end = 2.dp)) {
                            TextBoldSmall(
                                text = item?.seatNumber.toString(),
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        
                        if (passengerDetailsViewModel.genderPrivilege.value != stringResource(id = R.string.hide) && rapidBookingType != 0) {
//                           Preselect Male Radio option code
                            LaunchedEffect(passengerDetailsViewModel.genderPrivilege) {
                                if (passengerDetailsViewModel.privilegeResponseModel?.country != null &&
                                    passengerDetailsViewModel.privilegeResponseModel?.country.equals("India", true)){
                                    if (!passengerDataList[paxIndex].isSelectedGenderMale &&
                                        !passengerDataList[paxIndex].isSelectedGenderFemale) {
                                        isSelectedGenderMale(paxIndex, true)
                                        isSelectedGenderFemale(paxIndex, false)
                                        setGender(paxIndex, "Mr")
                                    }
                                }
                            }

                            // male
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
//                                    .selectable(
//                                        selected = passengerDetailsViewModel.selectedGenderMale,
//                                        onClick = {
//                                            setGender(paxIndex, "Mr")
//                                        }
//                                    )
                            ) {
                                RadioButton(
                                    selected = passengerDataList[paxIndex].isSelectedGenderMale,
                                    modifier = Modifier
                                        .requiredHeight(20.dp)
                                        .absoluteOffset((-10).dp, 0.dp),
                                    onClick = {
                                        if (rapidBookingType != 0) {
                                            setGender(paxIndex, "Mr")
                                            isSelectedGenderMale(paxIndex, true)
                                            isSelectedGenderFemale(paxIndex, false)
//                                            passengerDetailsViewModel.selectedGenderMale = true
//                                            passengerDetailsViewModel.selectedGenderFeMale = false
                                        }
                                    }
                                )
                                TextNormalSmall(
                                    modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                                    text = stringResource(id = R.string.male_complete)
                                )
                            }
                            
                            // female
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(end = 16.dp)
//                                    .selectable(
//                                        selected = passengerDetailsViewModel.selectedGenderFeMale,
//                                        onClick = {
//                                            setGender(paxIndex, "Ms")
//                                            passengerDetailsViewModel.selectedGenderMale = true
//                                            passengerDetailsViewModel.selectedGenderFeMale = false
//                                        }
//                                    )
                            ) {
                                RadioButton(
                                    selected = passengerDataList[paxIndex].isSelectedGenderFemale,
                                    modifier = Modifier
                                        .requiredHeight(20.dp)
                                        .absoluteOffset((-4).dp, 0.dp),
                                    onClick = {
                                        if (rapidBookingType != 0) {
                                            setGender(paxIndex, "Ms")
                                            isSelectedGenderFemale(paxIndex, true)
                                            isSelectedGenderMale(paxIndex, false)
//                                            passengerDetailsViewModel.selectedGenderFeMale = true
//                                            passengerDetailsViewModel.selectedGenderMale = false
                                        }
                                    }
                                )
                                TextNormalSmall(
                                    modifier = Modifier.absoluteOffset((-8).dp, 0.dp),
                                    text = stringResource(id = R.string.female_complete)
                                )
                            }
                        }
                        
                        Box(modifier = Modifier.wrapContentWidth()
                        ) {
                            
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                    horizontalArrangement = Arrangement.Center
                            ) {
//                            BasicTextField(
//                                value = "Hello",
//                                onValueChange = {},
//                                singleLine = true,
//                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
//                            )
                                if(!rapidBookingSkip){
                                    if(passengerDetailsViewModel.isAllowedEditFare && passengerDetailsViewModel.isAllowedEditFareForOtherRoute){
                                        TextBoldLarge(
                                            text = "$amountCurrency",
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .wrapContentWidth()
                                                .align(Alignment.CenterVertically)
                                        )


                                        BasicFareTextFieldComponentRounded(
                                            value = currentFare,
                                            modifier = Modifier
                                                .requiredWidth(60.dp)
                                                .padding(end = 1.dp)
                                                .onFocusChanged { focusState ->
                                                    if (passengerDetailsViewModel.country.equals(
                                                            "india",
                                                            true
                                                        ) &&
                                                        passengerDetailsViewModel.editFareMandatoryForAgentUser &&
                                                        passengerDetailsViewModel.isAllowedEditFare
                                                    ) {
                                                        if (focusState.isFocused) {
                                                            textFieldFocused = true
                                                        } else if (textFieldFocused) {
                                                            handleFareChange()
                                                            textFieldFocused = false
                                                        }
                                                    }
                                                },
                                            onValueChange = { newValue ->
                                                passengerDetailsViewModel.apply {
                                                    if (passengerDetailsViewModel.isAllowedEditFare) {
                                                        currentFare = newValue
                                                        setFare(paxIndex, currentFare)
                                                        hasPassengerData.value = true
                                                    }
                                                }
//                                     passengerDetailsViewModel.isPassengerAgeChanged = true
                                                handleFareChange()
                                            },
                                            cursorBrush = SolidColor(Color.Transparent),
//                                    label = stringResource(id = R.string.fare),
                                            keyboardOptions = KeyboardOptions(
                                                capitalization = KeyboardCapitalization.None,
                                                autoCorrect = true,
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            ),
                                            textStyle = TextStyle(
                                                color = colorResource(id = R.color.colorBlackShadow),
                                                fontFamily = FontFamily(Font(R.font.notosans_regular)),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        )
                                    }

                                }

                            }

                        }
                      
                        if (paxIndex != 0) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete_red),
                                contentDescription = "Delete Icon",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .wrapContentWidth(Alignment.End)
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
                                                    totalFare += it.fare?.toDouble() ?: 0.0
                                                    individualDiscountAmount += if(it.discountAmount.equals("")) { 0 } else {it.discountAmount?.toInt()}
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
                        if (passengerDataList[paxIndex].expand == true && paxIndex >= paxPosition) {
                            PassengerExpandCollapseView(
                                context = context,
                                paxIndex = paxIndex,
                                passengerDetailsViewModel = passengerDetailsViewModel,
                                item = item,
                                onMealChecked = { onMealChecked(it) }
                            )
                        }

                        if (paxIndex == 0 && passengerDataList.size > 1 && passengerDetailsViewModel.isEnableCopyPassengerCheckbox) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .padding(top = 8.dp)
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
//                                                            && isInsuranceChecked.value
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
                    
                    LaunchedEffect(rapidBookingType == 0 || isInsuranceChecked.value) {
                        mobileNoPrivilege.value = phoneNumber?.option ?: ""
                        alternateNoPrivilege.value = alternateNo?.option ?: ""
                        emailPrivilege.value = email?.option ?: ""
                        firstNamePrivilege.value = firstName?.option ?: ""
                        lastNamePrivilege.value = lastName?.option ?: ""
                    }

                    if (selectedSeatDetails.any { it.isExtraSeat }) {
                        namePrivilege.value = stringResource(id = R.string.mandatory)
                        agePrivilege.value = stringResource(id = R.string.mandatory)
                        genderPrivilege.value = stringResource(id = R.string.mandatory)
                    } else {
                        LaunchedEffect(rapidBookingType == 0 || isInsuranceChecked.value) {
                            namePrivilege.value = name?.option ?: ""
                            agePrivilege.value = age?.option ?: ""
                            genderPrivilege.value = title?.option ?: ""
                        }
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

                Timber.d("mandatoryFieldsFilled =$isAllMandatoryFieldsFilled --${passengerDataList[paxIndex].paxMandatoryMap.size} - ${mandatoryMap.size}")

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

//        if (passengerDetailsViewModel.firstNamePrivilege.value != stringResource(id = R.string.hide))     this part is not required as per ticket no. A-2-I38(Bug_tracker)
//        {
//            TextFieldComponentRounded(
//                isError = passengerDetailsViewModel.firstNamePrivilege.value == stringResource(id = R.string.mandatory)
//                        && passengerDetailsViewModel.passengerDataList[paxIndex].firstName?.isEmpty() == true,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 8.dp)
//                // .focusRequester(focusRequester = passengerDetailsViewModel.focusRequesterFirstName)
//                ,
//                value = passengerDetailsViewModel.passengerDataList[paxIndex].firstName ?: "",
//                onValueChange = {
//                    if (it.length <= passengerDetailsViewModel.maxChar) {
//                        passengerDetailsViewModel.setFirstName(paxIndex, it)
//                    }
//                    passengerDetailsViewModel.hasPassengerData.value = true
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next
//                )
//            )
//        }

//        if (passengerDetailsViewModel.lastNamePrivilege.value != stringResource(id = R.string.hide)    //this part is not required as per ticket no. A-2-I38(Bug_tracker)
//         {
//            TextFieldComponentRounded(
//                isError = passengerDetailsViewModel.lastNamePrivilege.value == stringResource(id = R.string.mandatory)
//                        && passengerDetailsViewModel.passengerDataList[paxIndex].lastName?.isEmpty() == true,
//                modifier = Modifier
//                    .weight(1f)
//                //.focusRequester(focusRequester = passengerDetailsViewModel.focusRequesterLastName)
//                ,
//                value = passengerDetailsViewModel.passengerDataList[paxIndex].lastName ?: "",
//                onValueChange = {
//                    if (it.length <= passengerDetailsViewModel.maxChar) {
//                        passengerDetailsViewModel.setLastName(paxIndex, it)
//                    }
//                    passengerDetailsViewModel.hasPassengerData.value = true
//                },
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next
//                )
//            )
//        }
    }

    if (passengerDetailsViewModel.namePrivilege.value != stringResource(id = R.string.hide)
        && !passengerDetailsViewModel.isInsuranceChecked.value
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            TextBoldLarge(
//                text = stringResource(id = R.string.name),
//                modifier = Modifier
//                    .requiredWidth(100.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )
            
            TextFieldComponentRounded(
                isError = passengerDetailsViewModel.namePrivilege.value == stringResource(
                    id = R.string.mandatory
                ) && passengerDetailsViewModel.passengerDataList[paxIndex].name?.isEmpty() == true,
                modifier = Modifier
                    // .focusRequester(passengerDetailsViewModel.focusRequesterName)
                    .fillMaxWidth(),
                value = passengerDetailsViewModel.passengerDataList[paxIndex].name ?: "",
                onValueChange = {
                    passengerDetailsViewModel.apply {
                        if (it.length <= passengerDetailsViewModel.maxChar) {
                            setName(paxIndex, it)
                        }
                        hasPassengerData.value = true
                    }
                    
                },
                label = stringResource(id = R.string.name) ,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                )
            )
        }
    }
    
    if (passengerDetailsViewModel.agePrivilege.value != stringResource(id = R.string.hide)) {
        
        Row (modifier = Modifier.padding(top = spaceBetweenTextField),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
//            TextBoldLarge(
//                text = stringResource(id = R.string.age),
//                modifier = Modifier
//                    .requiredWidth(100.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )
            
            TextFieldComponentRounded(
                isError = passengerDetailsViewModel.agePrivilege.value == stringResource(
                    id = R.string.mandatory
                ) && passengerDetailsViewModel.passengerDataList[paxIndex].age?.isEmpty() == true,
                modifier = Modifier
                    .fillMaxWidth(),
                value = passengerDetailsViewModel.passengerDataList[paxIndex].age ?: "",
                onValueChange = {
                    passengerDetailsViewModel.apply {
                        setAge(paxIndex, it.take(2))
                        hasPassengerData.value = true
                    }
                    
                    passengerDetailsViewModel.isPassengerAgeChanged = true
                    
                },
                label = stringResource(id = R.string.age) ,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.NumberPassword
                ),
                visualTransformation = VisualTransformation.None
            )
        }
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
    
    if (passengerDetailsViewModel.idTypePrivilege.value != stringResource(id = R.string.hide)) {
        
        Row(
            modifier = Modifier.padding(top = spaceBetweenTextField),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            TextBoldLarge(
//                text = stringResource(id = R.string.id_type),
//                modifier = Modifier
//                    .requiredWidth(100.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = expandedIdType,
                onExpandedChange = {
                    expandedIdType = !expandedIdType
                }
            ) {
                TextFieldComponentRounded(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = passengerDetailsViewModel.idTypePrivilege.value == stringResource(id = R.string.mandatory)
                            && passengerDetailsViewModel.passengerDataList[paxIndex].idCardType?.isEmpty() == true,
                    readOnly = true,
                    value = passengerDetailsViewModel.passengerDataList[paxIndex].idCardType ?: "",
                    onValueChange = { },
                    placeholder = stringResource(id = R.string.id_type),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedIdType
                        )
                    },
                    label = stringResource(id = R.string.id_type) ,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
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
        
    }
    
    if (passengerDetailsViewModel.idNumberPrivilege.value != stringResource(id = R.string.hide)) {
        
        Row(modifier = Modifier.padding(top = spaceBetweenTextField),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            TextBoldLarge(
//                text = stringResource(id = R.string.id_number),
//                modifier = Modifier
//                    .requiredWidth(100.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )
            TextFieldComponentRounded(
                isError = passengerDetailsViewModel.idNumberPrivilege.value == stringResource(id = R.string.mandatory
                ) && passengerDetailsViewModel.passengerDataList[paxIndex].idCardNumber?.isEmpty() == true,
                modifier = Modifier
                    .fillMaxWidth(),
                value = passengerDetailsViewModel.passengerDataList[paxIndex].idCardNumber ?: "",
                onValueChange = {
                    passengerDetailsViewModel.setIdNumber(paxIndex, it)
                },
                label = stringResource(id = R.string.id_number) ,
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

//    if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == false
//        || passengerDetailsViewModel.isDiscountPrivilege.value == true) {
//
//        TextBoldLarge(
//            text =
//            if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true
//                && passengerDetailsViewModel.isDiscountPrivilege.value == false
//            ) {
//                stringResource(id = R.string.additional_fare_discount).uppercase()
//            } else if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true) {
//                stringResource(id = R.string.additional_fare).uppercase()
//            } else if (passengerDetailsViewModel.isDiscountPrivilege.value == false) {
//                stringResource(id = R.string.discount).uppercase()
//            } else {
//                ""
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = paddingValues),
//            style = TextStyle(
//                colorResource(id = R.color.black),
//            )
//        )
//    }

    if (passengerDetailsViewModel.isAdditionalFarePrivilege.value == true) {
        
        Row(
            modifier = Modifier.padding(top = spaceBetweenTextField),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            TextBoldLarge(
//                text = stringResource(id = R.string.additional_fare),
//                modifier = Modifier
//                    .requiredWidth(100.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )
            TextFieldComponentRounded(
                modifier = Modifier
                    .fillMaxWidth(),
                value = if(passengerDetailsViewModel.passengerDataList[paxIndex].additionalFare == "0.0"){
                    "0"
                } else {
                    passengerDetailsViewModel.passengerDataList[paxIndex].additionalFare ?: "0"
                },
                onValueChange = {
                    if (it.isNotEmpty())
                        passengerDetailsViewModel.setAdditionalFare(paxIndex, getValidatedNumber(it))
                    else {
                        passengerDetailsViewModel.setAdditionalFare(paxIndex, "")
                    }
                    passengerDetailsViewModel.isAdditionalFareValueChanged = true
                },
                label = stringResource(id = R.string.additional_fare) ,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }
    }

    if(passengerDetailsViewModel.promotionCouponCode.isEmpty()) {
        SetDiscount(
            context = context,
            paxIndex = paxIndex,
            passengerDetailsViewModel = passengerDetailsViewModel,
            bookingTypeId = passengerDetailsViewModel.selectedBookingTypeId
        )
    }

    if (passengerDetailsViewModel.isEnableCampaignPromotions && passengerDetailsViewModel.isEnableCampaignPromotionsChecked && passengerDetailsViewModel.perSeatDiscountList.isNotEmpty()) {

        passengerDetailsViewModel.apply {

            TextFieldComponentRounded(
                isEnable = (isEnableCampaignPromotions && isEnableCampaignPromotionsChecked),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spaceBetweenTextField)
                    .height(52.dp),
                value = getValidatedNumber("${passengerDataList[paxIndex].discountAmount}"),
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
        Row(modifier = Modifier.padding(top = spaceBetweenTextField),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextFieldComponentRounded(
                isError = passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
                        && passengerDetailsViewModel.passengerDataList[paxIndex].fare?.isEmpty() == true || passengerDetailsViewModel.passengerDataList[paxIndex].fare == "0",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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

            TextFieldComponentRounded(
                isError = passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }
                        && passengerDetailsViewModel.passengerDataList[paxIndex].seatNumber?.isEmpty() == true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp),
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
                Row (verticalAlignment = Alignment.CenterVertically) {
                    if (!rapidBookingSkip) {
                        if (applyRoleOrBranchDiscountAtTimeOfBooking || isSeatWiseDiscountEdit) {
                            if (!selectedSeatDetails.any { it.isExtraSeat }) {

                                // Timber.d("checkAutoDiscountValue==" +
                                //         "$selectedBookingTypeId " +
                                //         "$branchRoleDiscountType " +
                                //         "$branchDiscountValue " +
                                //         "$discountType "+
                                //         "${passengerDataList[paxIndex].discountAmount}")

                                if (isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                    if (discountValue.isNotEmpty() && discountValue != "null"
                                    ) {
                                        if (!isSeatWiseDiscountEdit) {
                                            setDiscount(paxIndex, discountValue)
                                        }
                                    }
                                }
                                else {
                                    if (branchRoleDiscountType.isNotEmpty()) {
                                        if (branchRoleDiscountType != context.getString(R.string.none)) {

                                            if (bookingTypeId == 0) {
                                                if (discountType == context.getString(R.string.percentage).uppercase()
                                                    && discountValue.isNotEmpty()
                                                    && discountValue != "null"
                                                ) {
                                                    val calculateDiscountValue = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * discountValue.toDouble()
                                                    discountAmount = "$calculateDiscountValue"
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                } else if (discountType == context.getString(R.string.fixed).uppercase()
                                                    && discountValue.isNotEmpty()
                                                    && discountValue != "null"
                                                ) {
                                                    discountAmount = discountValue
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                }
                                            }
                                            else if (bookingTypeId == 1 || bookingTypeId == 2) {
                                                discountAmount = "0"
                                                if (!isSeatWiseDiscountEdit) {
                                                    setDiscount(paxIndex, discountAmount)
                                                }
                                            }
                                            else if (branchRoleDiscountType == context.getString(R.string.branch_discount_type)) {

                                                if (discountType == context.getString(R.string.percentage).uppercase() && branchDiscountValue.isNotEmpty()) {
                                                    val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
                                                    discountAmount = "$calculateBranchDiscount"
//                                if (passengerDataList[paxIndex].discountAmount?.isEmpty() == true && !isSeatWiseDiscountEdit) {
//                                    setDiscount(paxIndex, discountAmount)
//                                }
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                } else if (discountType == context.getString(R.string.fixed).uppercase() && branchDiscountValue.isNotEmpty()) {
                                                    discountAmount = branchDiscountValue
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                }
                                            }
                                            else if (branchRoleDiscountType == context.getString(R.string.role_discount_type)) {
                                                if (discountType == context.getString(R.string.percentage).uppercase() && roleDiscountValue.isNotEmpty()) {
                                                    val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
                                                    discountAmount = "$calculateRoleDiscount"
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                } else if (discountType == context.getString(R.string.fixed).uppercase() && roleDiscountValue.isNotEmpty()) {
                                                    discountAmount = roleDiscountValue
                                                    if (!isSeatWiseDiscountEdit) {
                                                        setDiscount(paxIndex, discountAmount)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }

                        } else {
                            discountAmount = "0"
                        }

                    }
                    else {
                        discountAmount = "0"
                        if (!isSeatWiseDiscountEdit){
                            setDiscount(paxIndex, discountAmount)
                        }
                    }

//                TextBoldLarge(
//                    text = stringResource(id = R.string.discount),
//                    modifier = Modifier
//                        .requiredWidth(100.dp),
//                    style = TextStyle(
//                        colorResource(id = R.color.black),
//                    )
//                )

                    val isEnabled = !(branchRoleDiscountType.isNotEmpty()
                            && branchRoleDiscountType != context.getString(R.string.none)
                            && applyRoleOrBranchDiscountAtTimeOfBooking
                            && !selectedSeatDetails.any { it.isExtraSeat })
                    TextFieldComponentRounded(
                        isEnable = isEnabled,
                        modifier = Modifier
                            .padding(top = spaceBetweenTextField)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable(enabled = isEnabled) {},
                        value = if (applyRoleOrBranchDiscountAtTimeOfBooking || isSeatWiseDiscountEdit) {
                            getValidatedNumber("${passengerDataList[paxIndex].discountAmount}")
                        } else {
                            "0"
                        } ?: "",
                        label = if (isAllowToApplyDiscountOnBookingPageWithPercentage
                            && (discountValue.toDouble() != 0.0 || discountValue.toInt() != 0)) {
                            stringResource(id = R.string.discount_label_percentage)
                        } else {
                            stringResource(id = R.string.discount)
                        },
//                    placeholder = stringResource(id = R.string.discount),
                        onValueChange = {
                            discountAmount = it

                            if (discountAmount.isNotEmpty()) {
                                isSeatWiseDiscountEdit = true
                            }

                            if(discountAmount != "0" && discountAmount != ""){
                                setPromotionCouponVisibility(false)
                            }else{
                                setPromotionCouponVisibility(true)
                            }

                            setDiscount(paxIndex, getValidatedNumber(discountAmount))

                            if (!selectedSeatDetails.any { it.isExtraSeat }) {

                                if (isAllowToApplyDiscountOnBookingPageWithPercentage) {

                                    try {
                                        if (discountAmount.toDouble() > discountValue.toDouble() && branchRoleDiscountType != context.getString(R.string.none)) {
                                            discountAmount = discountValue
                                            setDiscount(paxIndex, discountAmount)
                                            context.toast("${context.getString(R.string.discount_validation_percentage)} $discountValue")
                                        }
                                    } catch (_: Exception) { }
                                }
                                else {
                                    if (branchRoleDiscountType.isNotEmpty()) {
                                        if (branchRoleDiscountType != context.getString(R.string.none)) {
                                            if (selectedBookingTypeId == 0) {
                                                if (discountType == context.getString(R.string.percentage).uppercase() && discountValue.isNotEmpty()) {
                                                    val calculateDiscountValue = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * discountValue.toDouble()
                                                    try {
                                                        if (discountAmount.toDouble() > calculateDiscountValue) {
                                                            discountAmount = "$calculateDiscountValue"
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $calculateDiscountValue")
                                                        }
                                                    } catch (_: Exception) { }

                                                } else if (discountType == context.getString(R.string.fixed).uppercase() && discountValue.isNotEmpty()) {
                                                    try {
                                                        if (discountAmount.toDouble() > discountValue.toDouble()) {
                                                            discountAmount = discountValue
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $discountValue")
                                                        }
                                                    } catch (_: Exception) { }
                                                }
                                            }
                                            else if (branchRoleDiscountType == context.getString(R.string.branch_discount_type)) {

                                                if (discountType == context.getString(R.string.percentage).uppercase() && branchDiscountValue.isNotEmpty()) {
                                                    val calculateBranchDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * branchDiscountValue.toDouble()
                                                    try {
                                                        if (discountAmount.toDouble() > calculateBranchDiscount) {
                                                            discountAmount = "$calculateBranchDiscount"
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $calculateBranchDiscount")
                                                        }
                                                    } catch (_: Exception) { }
                                                } else if (discountType == context.getString(R.string.fixed).uppercase() && branchDiscountValue.isNotEmpty()) {
                                                    try {
                                                        if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
                                                            discountAmount = branchDiscountValue
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $branchDiscountValue")
                                                        }
                                                    } catch (_: Exception) { }
                                                }
                                            } else if (branchRoleDiscountType == context.getString(R.string.role_discount_type)) {
                                                if (discountType == context.getString(R.string.percentage).uppercase() && roleDiscountValue.isNotEmpty()) {
                                                    val calculateRoleDiscount = passengerDataList[paxIndex].fare.toString().toDouble() / 100.0f * roleDiscountValue.toDouble()
                                                    try {
                                                        if (discountAmount.toDouble() > calculateRoleDiscount) {
                                                            discountAmount = "$calculateRoleDiscount"
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $calculateRoleDiscount")
                                                        }
                                                    } catch (_: Exception) { }

                                                } else if (discountType == context.getString(R.string.fixed).uppercase() && roleDiscountValue.isNotEmpty()) {
                                                    try {
                                                        if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
                                                            discountAmount = roleDiscountValue
                                                            setDiscount(paxIndex,discountAmount)
                                                            context.toast("${context.getString(R.string.discount_validation)} $roleDiscountValue")
                                                        }
                                                    } catch (_: Exception) { }
                                                }
                                            }
                                        }
                                    }

                                }
                            }

                            if (it.isEmpty()) {
                                setDiscount(paxIndex, "0")
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetMealType(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    paxIndex: Int,
    isMealSelected: Boolean,
    onMealChecked : (Boolean)->Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
            checked = passengerDetailsViewModel.passengerDataList[paxIndex].isMealSelected
                ?: isMealSelected,
            onCheckedChange = {
                passengerDetailsViewModel.apply {
                    onMealChecked(it)
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


    if (passengerDetailsViewModel.passengerDataList[paxIndex].isMealSelected != null || isMealSelected) {
        if (passengerDetailsViewModel.isMealNoType.value == false
            && passengerDetailsViewModel.passengerDataList[paxIndex].isMealSelected != false
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                expanded = passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType,
                onExpandedChange = {
                    passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType = !passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType
                }
            ) {
                TextFieldComponentRounded(
                    modifier = Modifier.fillMaxSize(),
                    readOnly = true,
                    value = passengerDetailsViewModel.passengerDataList[paxIndex].selectedMealType
                        ?: "",
                    onValueChange = { },
//                    label = {
//                        TextNormalRegular(
//                            text = stringResource(id = R.string.selectMealType),
//                            modifier = Modifier,
//                            textStyle = TextStyle(colorResource(id = R.color.colorBlackShadow))
//                        )
//                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType
                        )
                    },
                    
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )
                ExposedDropdownMenu(
                    expanded = passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType,
                    onDismissRequest = {
                        passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType = false
                    }
                ) {
                    passengerDetailsViewModel.mealList.forEach { selectionId ->
                        DropdownMenuItem(
                            onClick = {
                                passengerDetailsViewModel.selectedMealTypeText = selectionId.value
                                passengerDetailsViewModel.passengerDataList[paxIndex].expandedMealType = false
                                passengerDetailsViewModel.setSelectedMealTypeText(
                                    paxIndex,
                                    passengerDetailsViewModel.selectedMealTypeText
                                )
                                passengerDetailsViewModel.setSelectedMealTypeId(
                                    paxIndex,
                                    selectionId.id
                                )
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
    }
}
