package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.TextFieldComponent
import com.bitla.ts.presentation.components.DividerLine
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.utils.ResourceProvider
import com.bitla.ts.utils.common.isEmailValid
import com.bitla.ts.utils.constants.PHONE_VALIDATION_COUNT
import com.bitla.ts.utils.showToast
import timber.log.Timber

@Composable
fun ContactDetailsCard(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>
) {

    val context = LocalContext.current
    val validateInput: (String) -> Boolean = { input ->
        input.all { it.isDigit() }
    }

    // Cache privilege values to prevent flicker during scroll/recomposition
    val initialAlternateNoPrivilege = remember { passengerDetailsViewModel.alternateNoPrivilege.value }
    val initialMobileNoPrivilege = remember { passengerDetailsViewModel.mobileNoPrivilege.value }
    val initialEmailPrivilege = remember { passengerDetailsViewModel.emailPrivilege.value }

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .fillMaxWidth()
            .heightIn(min = 200.dp), // Set a stable minimum height to reduce flicker
        onClick = {}
    )
    {
        Column(modifier = Modifier.padding(16.dp)) {
            TextBoldSmall(
                text = stringResource(id = R.string.contact_details).uppercase(),
                modifier = Modifier,
                textAlign = TextAlign.Start
            )

            DividerLine(modifier = Modifier.padding(top = 8.dp))

            if (initialMobileNoPrivilege != stringResource(id = R.string.hide)
                || initialAlternateNoPrivilege != stringResource(id = R.string.hide)
            ) {
                TextBoldSmall(
                    text = stringResource(id = R.string.mobile).uppercase(),
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Start
                )
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {

                if (initialMobileNoPrivilege != stringResource(id = R.string.hide)) {

                    Row() {
                        TextFieldComponent(context = context,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 56.dp)
                                .widthIn(max = 80.dp),
                            value = (if (passengerDetailsViewModel.countryList.isNotEmpty()) {
                                passengerDetailsViewModel.countryList[0]
                            } else {
                                ""
                            }).toString(),
                            label = stringResource(id = R.string.code),
                            placeholder = stringResource(id = R.string.code),
                            onValueChange = {
                                passengerDetailsViewModel.primaryCountryCode = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isEnable = false
                        )

                        ConstraintLayout(
                            modifier = Modifier
                                .weight(1.6f)
                                .defaultMinSize(minHeight = 10.dp)
                                .padding(start = 8.dp, bottom = 8.dp),
                        ) {
                            val (countryCode, primaryMobileNo, retrieve) = createRefs()

                            TextFieldComponent(context = context,
                                isError = if (passengerDetailsViewModel.phoneValidationCountPrivilege.value != null) passengerDetailsViewModel.mobileNoPrivilege.value == stringResource(
                                    id = R.string.mandatory
                                ) && passengerDetailsViewModel.primaryMobileNo.length != passengerDetailsViewModel.phoneValidationCountPrivilege.value
                                else {
                                    passengerDetailsViewModel.mobileNoPrivilege.value == stringResource(
                                        id = R.string.mandatory
                                    ) && passengerDetailsViewModel.primaryMobileNo.isEmpty()
                                },
                                modifier = Modifier
                                    .focusable(true)
                                    .focusRequester(passengerDetailsViewModel.focusRequesterPrimaryMobile)
                                    .constrainAs(primaryMobileNo) {
                                        top.linkTo(parent.top)
                                        start.linkTo(countryCode.end, 8.dp)
                                        end.linkTo(parent.end)
                                        width = Dimension.fillToConstraints
                                    },
                                value = passengerDetailsViewModel.primaryMobileNo,
                                label = stringResource(id = R.string.primary_mobile_number),
                                onValueChange = {
                                    passengerDetailsViewModel.apply {
                                        if (!privilegeResponseModel?.country.equals(
                                                "Indonesia",
                                                true
                                            )
                                        ) {
                                            if (validateInput(it)) {
                                                if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                                    passengerDetailsViewModel.primaryMobileNo =
                                                        it.take(
                                                            phoneValidationCountPrivilege.value
                                                                ?: PHONE_VALIDATION_COUNT
                                                        )
                                                } else {
                                                    passengerDetailsViewModel.primaryMobileNo =
                                                        it
                                                }
                                            }
                                        } else {
                                            if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                                passengerDetailsViewModel.primaryMobileNo =
                                                    it.take(
                                                        phoneValidationCountPrivilege.value
                                                            ?: PHONE_VALIDATION_COUNT
                                                    )
                                            } else {
                                                passengerDetailsViewModel.primaryMobileNo = it
                                            }
                                        }

                                        if (passengerDetailsViewModel.promotionCouponCode != "") {
                                            promotionCouponCode = ""
                                            isPromotionCouponChecked.value = false
                                            isEditButtonVisible.value = false
                                            isPrePostponeTicketEnable.value = true
                                            isPrePostponeTicketEnable.value = true
                                            isCouponCodeEnable.value = true
                                            isPrivilegeCardEnable.value = true
                                            isApplySmartMilesEnable.value = true
                                            isQuotePreviousPNREnable.value = true
                                            removeAppliedCoupon(
                                                ResourceProvider.TextResource.fromStringId(
                                                    R.string.promotion_coupon
                                                )
                                            )
                                        }

                                        isRetrieveClicked.value = false
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                )
                            )

                            if (!passengerDetailsViewModel.selectedSeatDetails.any { it.isExtraSeat }) {
                                TextBoldSmall(
                                    text = stringResource(id = R.string.retrieve),
                                    modifier = Modifier
                                        .clickable {
                                            if (passengerDetailsViewModel.primaryMobileNo.isNotEmpty()) {
                                                passengerDetailsViewModel.isRetrieveClicked.value =
                                                    true
                                            } else {
                                                context.showToast(context.getString(R.string.Please_enter_primary_mobile_no))
                                            }
                                        }
                                        .constrainAs(ref = retrieve) {
                                            top.linkTo(primaryMobileNo.top)
                                            end.linkTo(parent.end, 8.dp)
                                            bottom.linkTo(primaryMobileNo.bottom)
                                        },
                                    style = TextStyle(
                                        color = colorResource(id = R.color.colorPrimary),
                                        fontSize = 12.sp, fontFamily = FontFamily(
                                            Font(R.font.notosans_bold)
                                        )
                                    ),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {

                if (initialAlternateNoPrivilege != stringResource(id = R.string.hide)) {

                    TextFieldComponent(context = context,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 56.dp)
                            .widthIn(max = 80.dp),
                        value = (if (passengerDetailsViewModel.countryList.isNotEmpty()) {
                            passengerDetailsViewModel.countryList[0]
                        } else {
                            ""
                        }).toString(),
                        label = stringResource(id = R.string.code),
                        placeholder = stringResource(id = R.string.code),
                        onValueChange = {
                            passengerDetailsViewModel.alternateCountryCode = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isEnable = false
                    )

                    TextFieldComponent(context = context,
                        isError = if (passengerDetailsViewModel.phoneValidationCountPrivilege.value != null) passengerDetailsViewModel.alternateNoPrivilege.value == stringResource(id = R.string.mandatory
                        ) && passengerDetailsViewModel.alternateMobileNo.length != passengerDetailsViewModel.phoneValidationCountPrivilege.value else passengerDetailsViewModel.alternateNoPrivilege.value == stringResource(id = R.string.mandatory
                        ) && passengerDetailsViewModel.alternateMobileNo.isEmpty(),
                        modifier = Modifier
                            .weight(1.6f)
                            .padding(start = 16.dp, bottom = 16.dp),
                        value = passengerDetailsViewModel.alternateMobileNo,
                        label = stringResource(id = R.string.alternative_mobile_number),
                        placeholder = stringResource(id = R.string.alternative_mobile_number),
                        onValueChange = {
                            passengerDetailsViewModel.apply {
                                if (!privilegeResponseModel?.country.equals("Indonesia", true)) {
                                    if (validateInput(it)) {
                                        passengerDetailsViewModel.apply {
                                            alternateMobileNo = it

                                            if (phoneValidationCountPrivilege.value != null && phoneValidationCountPrivilege.value != 0) {
                                                alternateMobileNo = it.take(phoneValidationCountPrivilege.value ?: PHONE_VALIDATION_COUNT)
                                            } else {
                                                alternateMobileNo = it
                                            }

                                        }
                                    }
                                } else {
                                    if (phoneValidationCountPrivilege.value != 0 && phoneValidationCountPrivilege.value != null) {
                                        passengerDetailsViewModel.alternateMobileNo = it.take(phoneValidationCountPrivilege.value ?: PHONE_VALIDATION_COUNT)
                                    } else {
                                        passengerDetailsViewModel.alternateMobileNo = it
                                    }
                                }

                                isRetrieveClicked.value = false
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        )
                    )
                }
            }

            if (initialEmailPrivilege != stringResource(id = R.string.hide)) {
                TextBoldSmall(
                    text = stringResource(id = R.string.email_id).uppercase(),
                    modifier = Modifier,
                    textAlign = TextAlign.Start
                )

                TextFieldComponent(context = context,
                    isError = passengerDetailsViewModel.emailPrivilege.value == stringResource(id = R.string.mandatory
                    ) && !isEmailValid(passengerDetailsViewModel.emailId),
                    modifier = Modifier
                        // .focusRequester(passengerDetailsViewModel.focusRequesterEmailId)
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = passengerDetailsViewModel.emailId,
                    label = stringResource(id = R.string.email_id),
                    placeholder = stringResource(id = R.string.email_id),
                    onValueChange = {

                        passengerDetailsViewModel.emailId = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }
    }
}