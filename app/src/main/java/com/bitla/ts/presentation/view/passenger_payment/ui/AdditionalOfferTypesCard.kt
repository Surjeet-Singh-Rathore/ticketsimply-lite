package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.*
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.coupon.request.*
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.*
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.PHONE_BOOKING_NOT_SELECTED
import toast

@Composable
fun AdditionalOfferTypesCard(
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
                        painter = if (isExpendAdditionalOffer.value) {
                            painterResource(id = R.drawable.ic_arrow_up)
                        } else {
                            painterResource(id = R.drawable.ic_arrow_down)
                        },
                        contentDescription = "arrow",
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable {
                                isExpendAdditionalOffer.value = !isExpendAdditionalOffer.value
                            }
                    )
                }

                if (isExpendAdditionalOffer.value) {

                    if (isCouponCodeVisible.value)
                        CouponCode(context, passengerDetailsViewModel)

                    if(isAllowPromotionOfferCouponInBookingPage && selectedBookingTypeId == 0 && !isAllowedEditFare && !isAllowedEditFareForOtherRoute && passengerDetailsViewModel.phoneBookingCardColor == PHONE_BOOKING_NOT_SELECTED && isPromotionCouponVisible){
                        PromotionCoupon(context = context, passengerDetailsViewModel = passengerDetailsViewModel)
                    }

                    if (isPrePostponeVisible.value)
                        PrePostponeTicket(context, passengerDetailsViewModel)

                    if (isAllowPrivilegeCardBookings.value)
                        PrivilegeCardNumber(context, passengerDetailsViewModel)

                    if (isGstVisible.value)
                        GSTDetails(context, passengerDetailsViewModel)

                    if (isApplySmartMilesVisible.value)
                        ApplySmartMiles(context, passengerDetailsViewModel)

                    if (isQuotePreviousPnrVisible.value)
                        QuotePreviousPNR(context, passengerDetailsViewModel)

                    if (isDiscountVisible.value)
                        DiscountAmount(context, passengerDetailsViewModel,passengerDetailsViewModel.selectedBookingTypeId)

                    if (!country.equals("india", true)) {
                        if (isVIPTicketVisible.value)
                            VIPTicket(context, passengerDetailsViewModel)

                        if (isFreeTicketVisible.value)
                            FreeTicket(context, passengerDetailsViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponCode(
    context: Context,
    passengerDetailsViewModel:
    PassengerDetailsViewModel<Any?>
) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .clickable(
                    enabled = false,
                    onClick = { },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
                .fillMaxWidth()
                .background(
                    if (!isCouponCodeEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isCouponCodeChecked.value,
                onCheckedChange = {
                    isCouponCodeChecked.value = !isCouponCodeChecked.value

                    if (!isCouponCodeChecked.value){
                        isEditButtonVisible.value = false
                        isPrePostponeTicketEnable.value = true
                        isPromotionCouponEnable.value = true
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.coupon_code))
                    }
                },
                enabled = isCouponCodeEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.coupon_code),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )
            if (couponCode.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isCouponCodeEnable.value){
                CrossIcon()
            }
        }

        if (isCouponCodeChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = couponCode,
                label = stringResource(id = R.string.enter_coupon_code),
                placeholder = stringResource(id = R.string.enter_coupon_code),
                onValueChange = {
                    couponCode = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false


                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (couponCode.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value && couponCode.isNotEmpty()) {
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
                            couponCode = ""
                            isCouponCodeChecked.value = false
                            isPrePostponeTicketEnable.value = true
                            isPromotionCouponEnable.value = true

                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.coupon_code))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            isAppliedCoupon.value = true

//                            couponCode = "OTKH335002HYD"
                            if (couponCode.isNotEmpty()) {
                                isAppliedCoupon.value = true
                                checkedOfferTypeResId =
                                    ResourceProvider.TextResource.fromStringId(R.string.coupon_code)
                                val couponCodeHash = CouponCodeHash()
                                couponCodeHash.couponCode = couponCode
                                couponCodeHash.reservationId = resId.toString()
                                val discountParams = DiscountParams()
                                discountParams.couponCodeHash = couponCodeHash
                                setDiscountParam(discountParams)
                            } else {
                                context.toast(context.getString(R.string.enter_coupon_code))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                couponCode = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun PromotionCoupon(context: Context,passengerDetailsViewModel: PassengerDetailsViewModel<Any?>){
    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .clickable(
                    enabled = false,
                    onClick = { },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
                .fillMaxWidth()
                .background(
                    if (!isPromotionCouponEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isPromotionCouponChecked.value,
                onCheckedChange = {
                    isPromotionCouponChecked.value = !isPromotionCouponChecked.value


                    if (!isPromotionCouponChecked.value){
                        isEditButtonVisible.value = false
                        isPrePostponeTicketEnable.value = true
                        isCouponCodeEnable.value = true
                        isPrivilegeCardEnable.value = true
                        isApplySmartMilesEnable.value = true
                        isQuotePreviousPNREnable.value = true
                        isDiscountAmountEnable.value = true
                        isFreeTicketEnable.value = true
                        isVIPTicketEnable.value = true
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon))
                    }else{
                        if(privilegeResponseModel?.isDiscountOnTotalAmount == false){
                            for (i in 0 until passengerDataList.size){
                                setDiscount(i, "0")
                            }
                            isFareBreakupApiCalled = true
                        }


                    }
                },
                enabled = isPromotionCouponEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.promotion_coupon),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

            if (promotionCouponCode.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isPromotionCouponEnable.value){
                CrossIcon()
            }
        }
        Log.e("hello","${isPromotionCouponChecked.value} ${isEditButtonVisible.value}")
        if (isPromotionCouponChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = promotionCouponCode,
                label = stringResource(id = R.string.enter_coupon_code),
                placeholder = stringResource(id = R.string.enter_coupon_code),
                onValueChange = {
                    promotionCouponCode = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false


                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (promotionCouponCode.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value && promotionCouponCode.isNotEmpty()) {
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
                            promotionCouponCode = ""
                            isPromotionCouponChecked.value = false
                            isPrePostponeTicketEnable.value = true
                            isPrePostponeTicketEnable.value = true
                            isCouponCodeEnable.value = true
                            isPrivilegeCardEnable.value = true
                            isApplySmartMilesEnable.value = true
                            isQuotePreviousPNREnable.value = true
                            isDiscountAmountEnable.value = true
                            isFreeTicketEnable.value = true
                            isVIPTicketEnable.value = true
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            checkedOfferTypeResId = ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon)
                            fareHashData.clear()
                            selectedSeatDetails.forEach{ it ->
                                val obj = FareHash()
                                obj.seatNo = it.number
                                obj.fare = it.fare.toString()
                                fareHashData.add(0,obj)
                            }

                           // isAppliedCoupon.value = true

                            if (promotionCouponCode.isNotEmpty()) {
                                if(!passengerDetailsViewModel.primaryMobileNo.isNullOrEmpty()){
                                    isAppliedCoupon.value = true
                                    promotionOfferTypeResId =
                                        ResourceProvider.TextResource.fromStringId(R.string.promotion_coupon)
                                    val promotionCouponCodeHash = PromotionCouponHash()
                                    promotionCouponCodeHash.promotionCouponCode = promotionCouponCode
                                    promotionCouponCodeHash.reservationId = resId.toString()
                                    promotionCouponCodeHash.journeyDate = travelDate
                                    promotionCouponCodeHash.originId = sourceId
                                    promotionCouponCodeHash.destinationId = destinationId
                                    promotionCouponCodeHash.noOfSeats = noOfSeats
                                    promotionCouponCodeHash.totalFare = passengerDetailsViewModel.totalFareString
                                    promotionCouponCodeHash.mobileNumber = passengerDetailsViewModel.primaryMobileNo
                                    promotionCouponCodeHash.isRoundTrip = false    //for now by default value should be false because we dont have round trip in TS
                                    val discountParams = DiscountParams()
                                    discountParams.promotionCouponCodeHash = promotionCouponCodeHash
                                    discountParams.fareHash = fareHashData

                                    setDiscountParam(discountParams)
                                }else{
                                    context.toast("Enter mobile number first")

                                }

                            } else {
                                context.toast(context.getString(R.string.enter_coupon_code))

                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                promotionCouponCode = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))

    }
}

@Composable
private fun PrePostponeTicket(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isPrePostponeTicketEnable.value) {
                        colorResource(id = R.color.button_color)
                    } else {
                        colorResource(id = R.color.white)
                    }
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isPrePostponeTicketChecked.value,
                onCheckedChange = {
                    isPrePostponeTicketChecked.value = !isPrePostponeTicketChecked.value

                    if (!isPrePostponeTicketChecked.value){
                        isEditButtonVisible.value = false
                        isCouponCodeEnable.value = true
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.pre_postpone_ticket))
                    }
                },
                enabled = isPrePostponeTicketEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.pre_postpone_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

            if (prePostponeTicket.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isPrePostponeTicketEnable.value){
                CrossIcon()
            }
        }

        if (isPrePostponeTicketChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = prePostponeTicket,
                label = stringResource(id = R.string.enter_ticket_number),
                placeholder = stringResource(id = R.string.enter_ticket_number),
                onValueChange = {
                    prePostponeTicket = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {

                    Row {
                        if (prePostponeTicket.isNotEmpty()) {
                            TickIcon()
                        } else if (isTickIconVisible.value && prePostponeTicket.isNotEmpty()) {
                            TickIcon()
                        }
                    }
                }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextBoldRegular(
                    text = stringResource(id = R.string.cancel),
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                        .clickable {
                            isPrePostponeTicketChecked.value = false
                            prePostponeTicket = ""
                            isCouponCodeChecked.value = false
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.pre_postpone_ticket))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.pre_postpone_ticket)

                            if (prePostponeTicket.isNotEmpty()) {
                                isAppliedCoupon.value = true
                                val prePostPoneHash = PrePostPoneHash()
                                prePostPoneHash.pnrNumber = prePostponeTicket
                                prePostPoneHash.agentType = agentType
                                prePostPoneHash.isBimaService = isBima.toString()
                                prePostPoneHash.originId = sourceId
                                prePostPoneHash.destinationId = destinationId
                                prePostPoneHash.noOfSeats = noOfSeats
                                prePostPoneHash.corpCompanyId = corpCompanyId
                                prePostPoneHash.allowPrePostPoneOtherBranch =
                                    allowPrePostPoneOtherBranch

                                val discountParams = DiscountParams()
                                discountParams.prePostPoneHash = prePostPoneHash
                                setDiscountParam(discountParams)
                            } else {
                                context.toast(context.getString(R.string.enter_coupon_code))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                prePostponeTicket = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun PrivilegeCardNumber(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isPrivilegeCardEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isPrivilegeCardChecked.value,
                onCheckedChange = {
                    isPrivilegeCardChecked.value = !isPrivilegeCardChecked.value

                    if (!isPrivilegeCardChecked.value){
                        isEditButtonVisible.value = false
                        isCouponCodeEnable.value = true
                        isQuotePreviousPNREnable.value = true
                        isDiscountAmountEnable.value = true
                        isApplySmartMilesEnable.value = true
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.privilege_card))
                    }
                },
                enabled = isPrivilegeCardEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.privilege_card_number),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

            if (privilegeCardNumber.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isPrivilegeCardEnable.value){
                CrossIcon()
            }
        }



        if (isPrivilegeCardChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = privilegeCardNumber,
                label = stringResource(id = R.string.mobile_privilege_number),
                placeholder = stringResource(id = R.string.mobile_privilege_number),
                onValueChange = {
                    privilegeCardNumber = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (privilegeCardNumber.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && privilegeCardNumber.isNotEmpty()
                    ) {
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
                            isPrivilegeCardChecked.value = false
                            privilegeCardNumber = ""

                            isCouponCodeEnable.value = true
                            isQuotePreviousPNREnable.value = true
                            isDiscountAmountEnable.value = true
                            isApplySmartMilesEnable.value = true
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.privilege_card))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.privilege_card)

                            if (privilegeCardNumber.isNotEmpty()) {
                                isAppliedCoupon.value = true

                                val privilegeCardHash = PrivilegeCardHash()
                                privilegeCardHash.cardNumber = privilegeCardNumber
                                privilegeCardHash.resId = resId.toString()
                                privilegeCardHash.mobileNumber = privilegeMobileNumber
                                privilegeCardHash.selectedSeats = selectedSeatNo
                                privilegeCardHash.returnResId = returnResId
                                privilegeCardHash.isRoundtrip = "$isRoundTrip"
                                privilegeCardHash.returnResSeatsCount = returnResSeatsCount
                                privilegeCardHash.connectingResSeatsCount = connectingResSeatsCount

                                val discountParams = DiscountParams()
                                discountParams.privilegeCardHash = privilegeCardHash
                                setDiscountParam(discountParams)
                            } else {
                                context.toast(context.getString(R.string.validate_card_number))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                privilegeCardNumber = ""

            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun GSTDetails(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isGSTDetailsEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isGSTDetailsChecked.value,
                onCheckedChange = {
                    passengerDetailsViewModel.apply {
                        isGSTDetailsChecked.value = !isGSTDetailsChecked.value

                        if (!isGSTDetailsChecked.value){
                            isEditButtonVisible.value = false
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.gst_details))
                        }
                    }
                },
                enabled = isGSTDetailsEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.gst_details),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

            if (gstNumber.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isGSTDetailsEnable.value){
                CrossIcon()
            }
        }

        if (isGSTDetailsChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = gstNumber,
                label = stringResource(id = R.string.enter_gst_number),
                placeholder = stringResource(id = R.string.enter_gst_number),
                onValueChange = {
                    gstNumber = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                trailingIcon = {
                    if (gstNumber.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && gstNumber.isNotEmpty()
                    ) {
                        TickIcon()
                    }
                }
            )

            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = gstCompanyName,
                label = stringResource(id = R.string.enter_company_name),
                placeholder = stringResource(id = R.string.enter_company_name),
                onValueChange = {
                    gstCompanyName = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (gstCompanyName.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && gstCompanyName.isNotEmpty()
                    ) {
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
                            isGSTDetailsChecked.value = false
                            gstCompanyName = ""
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.gst_details))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.gst_details)

                            if (gstNumber.isEmpty()) {
                                context.toast(context.getString(R.string.enter_gst_number))
                            } else if (gstCompanyName.isEmpty()) {
                                context.toast(context.getString(R.string.enter_company_name))
                            } else {
                                isEditButtonVisible.value = true
                                addAppliedCoupon("$gstNumber-$gstCompanyName")
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                gstNumber = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
private fun ApplySmartMiles(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isApplySmartMilesEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isApplySmartMilesChecked.value,
                onCheckedChange = {
                    isApplySmartMilesChecked.value = !isApplySmartMilesChecked.value

                    if (!isApplySmartMilesChecked.value){
                        isEditButtonVisible.value = false

                        isPrivilegeCardEnable.value = true
                        isDiscountAmountEnable.value = true
                        isQuotePreviousPNREnable.value = true
                        isSmartMilesOtpApi.value = false
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.apply_smart_miles))
                    }
                },
                enabled = isApplySmartMilesEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.apply_smart_miles),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp),
            )

            if (applySmartMilesMobileNo.isNotEmpty() && isEditButtonVisible.value) {
                EditButton(
                    borderStroke = BorderStroke(
                        width = 1.dp,
                        color = colorResource(id = R.color.colorPrimary)
                    ),
                    onClick = {
                        isEditButtonVisible.value = !isEditButtonVisible.value
                        isSmartMilesOtpVisible.value = false
                    }
                )
            }

            if (!isApplySmartMilesEnable.value){
                CrossIcon()
            }
        }

        if (isApplySmartMilesChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = applySmartMilesMobileNo,
                label = stringResource(id = R.string.enter_mobile_number),
                placeholder = stringResource(id = R.string.enter_mobile_number),
                onValueChange = {
                    applySmartMilesMobileNo = it
                    isTickIconVisible.value = it.isNotEmpty()
//                    isSmartMilesOtpApi.value = false
                    isAppliedCoupon.value = false

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (applySmartMilesMobileNo.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && applySmartMilesMobileNo.isNotEmpty()
                    ) {
                        TickIcon()
                    }
                }
            )

            CreateButton(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.colorPrimary).copy(
                        alpha = 1f
                    )
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .height(44.dp)
                    .background(colorResource(id = R.color.colorPrimary))
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sendOtp),
                onClick = {

                    checkedOfferTypeResId = ResourceProvider.TextResource.fromStringId(R.string.apply_smart_miles)

                    if (applySmartMilesMobileNo.isEmpty()) {
                        context.toast(context.getString(R.string.enter_smart_miles_number))
                    } else {
                        isSmartMilesOtpApi.value = true
                    }
                },
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.notosans_bold)),
                    fontSize = 14.sp
                )
            )

            if (isSmartMilesOtpVisible.value){
                TextFieldComponent(context = context,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                    value = smartMilesOtp,
                    label = stringResource(id = R.string.enter_otp),
                    placeholder = stringResource(id = R.string.enter_otp),
                    onValueChange = {
                        smartMilesOtp = it
                        isAppliedCoupon.value = false
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
//                trailingIcon = {
//                    if (applySmartMiles.isNotEmpty()) {
//                        TickIcon()
//                    } else if (isTickIconVisible.value
//                        && applySmartMiles.isNotEmpty()
//                    ) {
//                        TickIcon()
//                    }
//                }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextBoldRegular(
                        text = stringResource(id = R.string.cancel),
                        modifier = Modifier
                            .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                            .clickable {
                                isApplySmartMilesChecked.value = false
                                applySmartMilesMobileNo = ""
                                isSmartMilesOtpApi.value = false
                                removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.apply_smart_miles))
                            },
                        textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                    )
                    TextBoldRegular(
                        text = stringResource(id = R.string.apply),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {

                                checkedOfferTypeResId =
                                    ResourceProvider.TextResource.fromStringId(R.string.apply_smart_miles)

                                when {
                                    applySmartMilesMobileNo.isEmpty() -> {
                                        context.toast(context.getString(R.string.enter_smart_miles_number))
                                    }

                                    smartMilesOtp.isEmpty() -> {
                                        context.toast(context.getString(R.string.enter_otp))
                                    }

                                    smartMilesOtp != smartMilesOtp -> {
                                        context.toast(context.getString(R.string.invalid_otp))

                                    }

                                    else -> {
                                        isAppliedCoupon.value = true

                                        val smartMilesHash = SmartMilesHash()
                                        smartMilesHash.otp = smartMilesOtp
                                        smartMilesHash.phoneNumber = applySmartMilesMobileNo
                                        smartMilesHash.otpKey = smartMilesOtpKey

                                        val discountParams = DiscountParams()
                                        discountParams.smartMilesHash = smartMilesHash
                                        setDiscountParam(discountParams)

                                    }
                                }

                            },
                        textStyle = TextStyle(
                            color = colorResource(
                                id = R.color.colorPrimary
                            )
                        )
                    )
                }
            }

        } else {
            if (!isEditButtonVisible.value){
                applySmartMilesMobileNo = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable

private fun DiscountAmount(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    bookingTypeId: Int
) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isDiscountAmountEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isDiscountAmountChecked.value,
                onCheckedChange = {
                    isDiscountAmountChecked.value = !isDiscountAmountChecked.value

                    if (!isDiscountAmountChecked.value){
                        isEditButtonVisible.value = false
                        isPrivilegeCardEnable.value = true
                        isQuotePreviousPNREnable.value = true
                        isApplySmartMilesEnable.value = true
                        isPromotionCouponEnable.value = true

                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.discount_amount))
                        discountAmount = ""
                    } else {

                        if (applyRoleOrBranchDiscountAtTimeOfBooking || isSeatWiseDiscountEdit) {
                            if (isAllowToApplyDiscountOnBookingPageWithPercentage && bookingTypeId == 0) {
//                            val calculateBranchDiscount = totalFare / 100.0f * branchDiscountValue.toDouble()
                                discountAmount = discountValue
                            } else {
                                if (branchRoleDiscountType.isNotEmpty()) {
                                    if (branchRoleDiscountType != context.getString(R.string.none)) {

                                        if (selectedBookingTypeId == 0) {
                                            if (discountType == context.getString(R.string.percentage).uppercase() && discountValue.isNotEmpty()) {
                                                val calculateDiscountValue = totalFare / 100.0f * discountValue.toDouble()
                                                discountAmount = "$calculateDiscountValue"
                                            } else if (discountType == context.getString(R.string.fixed).uppercase() && discountValue.isNotEmpty()) {
                                                discountAmount = discountValue
                                            }
                                        }
                                        else if (branchRoleDiscountType == context.getString(R.string.branch_discount_type)) {

                                            if (discountType == context.getString(R.string.percentage).uppercase() && branchDiscountValue.isNotEmpty()) {
                                                if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                                    val calculateBranchDiscount = totalFare / 100.0f * branchDiscountValue.toDouble()
                                                    discountAmount = "$calculateBranchDiscount"
                                                } else {
                                                    discountAmount = branchDiscountValue
                                                }

                                            } else if (discountType == context.getString(R.string.fixed).uppercase() && branchDiscountValue.isNotEmpty()) {
//                                            if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                                discountAmount = branchDiscountValue
//                                            } else {
//                                                val calculateBranchDiscount = totalFare / 100.0f * branchDiscountValue.toDouble()
//                                                discountAmount = "$calculateBranchDiscount"
//                                            }
                                                discountAmount = branchDiscountValue

                                            }
                                        }
                                        else if (branchRoleDiscountType == context.getString(R.string.role_discount_type)) {
                                            if (discountType == context.getString(R.string.percentage).uppercase() && roleDiscountValue.isNotEmpty()) {
                                                if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                                    val calculateRoleDiscount = totalFare / 100.0f * roleDiscountValue.toDouble()
                                                    discountAmount = "$calculateRoleDiscount"
                                                } else {
                                                    discountAmount = roleDiscountValue
                                                }

                                            } else if (discountType == context.getString(R.string.fixed).uppercase() && roleDiscountValue.isNotEmpty()) {
//                                            if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                                discountAmount = roleDiscountValue
//                                            } else {
//                                                val calculateRoleDiscount = totalFare / 100.0f * roleDiscountValue.toDouble()
//                                                discountAmount = "$calculateRoleDiscount"
//                                            }
                                                discountAmount = roleDiscountValue

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            discountAmount = "0"
                        }
                    }
                },
                enabled = isDiscountAmountEnable.value
            )

            TextNormalSmall(
                text = if (isAllowToApplyDiscountOnBookingPageWithPercentage) {
                    stringResource(id = R.string.enter_discount_percentage)
                } else {
                    stringResource(id = R.string.enter_discount_amount)
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp),
            )

            if (discountAmount.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isDiscountAmountEnable.value){
                CrossIcon()
            }
        }

        if (isDiscountAmountChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                isEnable = !(branchRoleDiscountType.isNotEmpty()
                        && branchRoleDiscountType != context.getString(R.string.none)
                        && applyRoleOrBranchDiscountAtTimeOfBooking),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = discountAmount,
                label = if (isAllowToApplyDiscountOnBookingPageWithPercentage) {
                    stringResource(id = R.string.enter_discount_percentage)
                } else {
                    stringResource(id = R.string.enter_discount_amount)
                },
                placeholder = if (isAllowToApplyDiscountOnBookingPageWithPercentage) {
                    stringResource(id = R.string.enter_discount_percentage)
                } else {
                    stringResource(id = R.string.enter_discount_amount)
                },
                onValueChange = {

                    discountAmount = it
                    discountAmount =  getValidatedNumber(discountAmount)

                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false

                    if (isAllowToApplyDiscountOnBookingPageWithPercentage && bookingTypeId == 0) {
                        try {
                            if (discountAmount.toDouble() > discountValue.toDouble() && branchRoleDiscountType != context.getString(R.string.none)) {
                                discountAmount = discountValue
                                context.toast("${context.getString(R.string.discount_validation_percentage)} $discountAmount")
                            }
                        } catch (_: Exception) { }
                    } else {
                        if (branchRoleDiscountType.isNotEmpty()) {

                            if (branchRoleDiscountType != context.getString(R.string.none)) {
                                if (selectedBookingTypeId == 0) {
                                    if (discountType == context.getString(R.string.percentage).uppercase() && discountValue.isNotEmpty()) {
                                        val calculateDiscountValue = totalFare / 100.0f * discountValue.toDouble()
                                        try {
                                            if (discountAmount.toDouble() > calculateDiscountValue) {
                                                discountAmount = "$calculateDiscountValue"
                                                context.toast("${context.getString(R.string.discount_validation)} $calculateDiscountValue")
                                            }
                                        } catch (_: Exception) { }

                                    } else if (discountType == context.getString(R.string.fixed).uppercase() && discountValue.isNotEmpty()) {
                                        try {
                                            if (discountAmount.toDouble() > discountValue.toDouble()) {
                                                discountAmount = discountValue
                                                context.toast("${context.getString(R.string.discount_validation)} $discountValue")
                                            }
                                        } catch (_: Exception) { }
                                    }
                                }
                                else if (branchRoleDiscountType == context.getString(R.string.branch_discount_type)) {

                                    if (discountType == context.getString(R.string.percentage).uppercase() && branchDiscountValue.isNotEmpty()) {
                                        if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                            val calculateBranchDiscount = totalFare / 100.0f * branchDiscountValue.toDouble()
                                            try {
                                                if (discountAmount.toDouble() > calculateBranchDiscount) {
                                                    discountAmount = "$calculateBranchDiscount"
                                                    context.toast("${context.getString(R.string.discount_validation)} $calculateBranchDiscount")
                                                }
                                            } catch (_: Exception) { }
                                        } else {
                                            try {
                                                if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
                                                    discountAmount = branchDiscountValue
                                                    context.toast("${context.getString(R.string.discount_validation)} $branchDiscountValue")
                                                }
                                            } catch (_: Exception) { }
                                        }

                                    } else if (discountType == context.getString(R.string.fixed).uppercase() && branchDiscountValue.isNotEmpty()) {
//                                        if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                            try {
//                                                if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
//                                                    discountAmount = branchDiscountValue
//                                                    context.toast("${context.getString(R.string.discount_validation)} $branchDiscountValue")
//                                                }
//                                            } catch (_: Exception) { }
//                                        } else {
//                                            val calculateBranchDiscount = totalFare / 100.0f * branchDiscountValue.toDouble()
//                                            try {
//                                                if (discountAmount.toDouble() > calculateBranchDiscount.toDouble()) {
//                                                    discountAmount = "$calculateBranchDiscount"
//                                                    context.toast("${context.getString(R.string.discount_validation)} $calculateBranchDiscount")
//                                                }
//                                            } catch (_: Exception) { }
//                                        }

                                        try {
                                            if (discountAmount.toDouble() > branchDiscountValue.toDouble()) {
                                                discountAmount = branchDiscountValue
                                                context.toast("${context.getString(R.string.discount_validation)} $branchDiscountValue")
                                            }
                                        } catch (_: Exception) { }

                                    }
                                }
                                else if (branchRoleDiscountType == context.getString(R.string.role_discount_type)) {
                                    if (discountType == context.getString(R.string.percentage).uppercase() && roleDiscountValue.isNotEmpty()) {
                                        if (!isAllowToApplyDiscountOnBookingPageWithPercentage) {
                                            val calculateRoleDiscount = totalFare / 100.0f * roleDiscountValue.toDouble()
                                            try {
                                                if (discountAmount.toDouble() > calculateRoleDiscount) {
                                                    discountAmount = "$calculateRoleDiscount"
                                                    context.toast("${context.getString(R.string.discount_validation)} $calculateRoleDiscount")
                                                }
                                            } catch (_: Exception) { }
                                        } else {
                                            try {
                                                if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
                                                    discountAmount = "$roleDiscountValue"
                                                    context.toast("${context.getString(R.string.discount_validation)} $roleDiscountValue")
                                                }
                                            } catch (_: Exception) { }
                                        }
                                    } else if (discountType == context.getString(R.string.fixed).uppercase() && roleDiscountValue.isNotEmpty()) {
//                                        if (allowToApplyCurrentUserRoleBranchDiscount && !applyRoleOrBranchDiscountAtTimeOfBooking) {
//                                            try {
//                                                if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
//                                                    discountAmount = roleDiscountValue
//                                                    context.toast("${context.getString(R.string.discount_validation)} $roleDiscountValue")
//                                                }
//                                            } catch (_: Exception) { }
//                                        } else {
//                                            val calculateRoleDiscount = totalFare / 100.0f * roleDiscountValue.toDouble()
//                                            try {
//                                                if (discountAmount.toDouble() > calculateRoleDiscount) {
//                                                    discountAmount = "$calculateRoleDiscount"
//                                                    context.toast("${context.getString(R.string.discount_validation)} $calculateRoleDiscount")
//                                                }
//                                            } catch (_: Exception) { }
//                                        }

                                        try {
                                            if (discountAmount.toDouble() > roleDiscountValue.toDouble()) {
                                                discountAmount = roleDiscountValue
                                                context.toast("${context.getString(R.string.discount_validation)} $roleDiscountValue")
                                            }
                                        } catch (_: Exception) { }

                                    }
                                }
                            }
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (discountAmount.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && discountAmount.isNotEmpty()
                    ) {
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
                            isDiscountAmountChecked.value = false
                            discountAmount = ""
//
                            isPrivilegeCardEnable.value = true
                            isQuotePreviousPNREnable.value = true
                            isApplySmartMilesEnable.value = true
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.discount_amount))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.discount_amount)

                            if (discountAmount.isNotEmpty()) {

                                if (isAllowToApplyDiscountOnBookingPageWithPercentage
                                    && passengerDetailsViewModel.branchRoleDiscountType != context.getString(R.string.none)
                                ) {
                                    val calculateDiscountValue = totalFare / 100.0f * discountAmount.toDouble()
                                    addAppliedCoupon("$calculateDiscountValue")
                                } else {
                                    addAppliedCoupon(discountAmount)
                                }

                                isPrivilegeCardEnable.value = false
                                isQuotePreviousPNREnable.value = false
                                isApplySmartMilesEnable.value = false
                                isPromotionCouponEnable.value = false
                                isEditButtonVisible.value = true
                            } else {
                                context.toast(context.getString(R.string.enter_discount_amount))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                discountAmount = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@Composable
fun QuotePreviousPNR(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isQuotePreviousPNREnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isQuotePreviousPNRChecked.value,
                onCheckedChange = {
                    isQuotePreviousPNRChecked.value = !isQuotePreviousPNRChecked.value

                    if (!isQuotePreviousPNRChecked.value){
                        isEditButtonVisible.value = false
                        isPrivilegeCardEnable.value = true
                        isDiscountAmountEnable.value = true
                        isApplySmartMilesEnable.value = true
                        removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.quote_previous_pnr))
                    }
                },
                enabled = isQuotePreviousPNREnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.quote_previous_pnr),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp),
            )

            if (quotePNRNumber.isNotEmpty() && isEditButtonVisible.value) {
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

            if (!isQuotePreviousPNREnable.value){
                CrossIcon()
            }
        }

        if (isQuotePreviousPNRChecked.value && !isEditButtonVisible.value) {
            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = quotePNRNumber,
                label = stringResource(id = R.string.enter_pnr_number),
                placeholder = stringResource(id = R.string.enter_pnr_number),
                onValueChange = {
                    quotePNRNumber = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (quotePNRNumber.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && quotePNRNumber.isNotEmpty()
                    ) {
                        TickIcon()
                    }
                }
            )

            TextFieldComponent(context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                value = quotePhoneNumber,
                label = stringResource(id = R.string.enter_phone_number),
                placeholder = stringResource(id = R.string.enter_phone_number),
                onValueChange = {
                    quotePhoneNumber = it
                    isTickIconVisible.value = it.isNotEmpty()
                    isAppliedCoupon.value = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (quotePhoneNumber.isNotEmpty()) {
                        TickIcon()
                    } else if (isTickIconVisible.value
                        && quotePhoneNumber.isNotEmpty()
                    ) {
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
                            isQuotePreviousPNRChecked.value = false
                            quotePNRNumber = ""
                            quotePhoneNumber = ""
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.quote_previous_pnr))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {

                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.quote_previous_pnr)

                            if (quotePNRNumber.isNotEmpty()) {
                                isAppliedCoupon.value = true

                                val bookingType = if (passengerDetailsViewModel.selectedBookingTypeId.toString() == "4") "0"
                                else passengerDetailsViewModel.selectedBookingTypeId.toString()

                                val previousPnrHash = PreviousPnrHash()
                                previousPnrHash.previousPnr = quotePNRNumber
                                previousPnrHash.phoneNumber = quotePhoneNumber
                                previousPnrHash.origin = sourceId
                                previousPnrHash.destination = destinationId
                                previousPnrHash.selectedSeats = selectedSeatNo.trim()
                                previousPnrHash.resId = resId
                                previousPnrHash.totalFare = totalFare
                                previousPnrHash.bookingType = bookingType
                                previousPnrHash.routeId = routeId

                                val discountParams = DiscountParams()
                                discountParams.previousPnrHash = previousPnrHash
                                setDiscountParam(discountParams)
                            } else {
                                context.toast(context.getString(R.string.validate_card_number))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }
        } else {
            if (!isEditButtonVisible.value){
                quotePNRNumber = ""
                quotePhoneNumber = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun VIPTicket(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isFreeTicketChecked.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                checked = isVIPTicketChecked.value,
                onCheckedChange = {
                    isVIPTicketChecked.value = !isVIPTicketChecked.value
                    isDisableAdditionalOfferCard.value = !isDisableAdditionalOfferCard.value
                    isEnableVIPTicket(passengerDetailsViewModel)
                    selectedVIPTicketId = ""
                    removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.vip_ticket))
                },
                enabled = !isFreeTicketChecked.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.vip_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
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

        if (isVIPTicketChecked.value && !isEditButtonVisible.value) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
                expanded = isExposedVIPTicketDropdown.value,
                onExpandedChange = {
                    isExposedVIPTicketDropdown.value = !isExposedVIPTicketDropdown.value
                }
            ) {
                TextField(
                    modifier = Modifier.fillMaxSize(),
                    readOnly = true,
                    value = selectedVIPTicketId,
                    onValueChange = { },
                    label = {
                        Text(
                            text = stringResource(id = R.string.selectVip),
                            fontSize = 14.sp,
                            style = TextStyle(colorResource(id = R.color.colorBlackShadow)),
                            fontFamily = FontFamily(Font(R.font.notosans_regular))
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextBoldRegular(
                    text = stringResource(id = R.string.cancel),
                    modifier = Modifier
                        .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                        .clickable {
                            isVIPTicketChecked.value = false
                            selectedVIPTicketId = ""
                            isEnableVIPTicket(passengerDetailsViewModel)
                            removeAppliedCoupon(ResourceProvider.TextResource.fromStringId(R.string.vip_ticket))
                        },
                    textStyle = TextStyle(color = colorResource(id = R.color.colorRed2))
                )
                TextBoldRegular(
                    text = stringResource(id = R.string.apply),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            
                            checkedOfferTypeResId =
                                ResourceProvider.TextResource.fromStringId(R.string.vip_ticket)
                            
                            if (selectedVIPTicketId.isNotEmpty()) {
                                addAppliedCoupon(selectedVIPTicketId)
                                isEditButtonVisible.value = true
                            } else {
                                context.toast(context.getString(R.string.selectVip))
                            }
                        },
                    textStyle = TextStyle(
                        color = colorResource(
                            id = R.color.colorPrimary
                        )
                    )
                )
            }

        } else {
            if (!isEditButtonVisible.value){
                selectedVIPTicketId = ""
            }
        }

        DividerLine(modifier = Modifier.padding(top = 0.dp))   
    }
}

@Composable
private fun FreeTicket(context: Context, passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    passengerDetailsViewModel.apply {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (!isFreeTicketEnable.value)
                        colorResource(id = R.color.button_color)
                    else
                        colorResource(id = R.color.white)
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Checkbox(
                modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
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
                },
                enabled = isFreeTicketEnable.value
            )

            TextNormalSmall(
                text = stringResource(id = R.string.free_ticket),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .absoluteOffset((-12).dp, 0.dp)
            )

            if (!isFreeTicketEnable.value){
                CrossIcon()
            }
        }
        DividerLine(modifier = Modifier.padding(top = 0.dp))
    }
}