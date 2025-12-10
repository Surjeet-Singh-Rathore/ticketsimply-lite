package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import asString
import com.bitla.ts.R
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.ResourceProvider
import toast

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentOptionsNewFlow(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onPaymentOptionSelection: (String) -> Unit
) {
    // Create payment options list outside of composition
    val paymentOptionsList = remember {
        mutableStateListOf<SearchModel>().apply {
            val cash = SearchModel()
            cash.id = "1"
            cash.paymentType = ResourceProvider.TextResource.fromStringId(R.string.cash)
            add(cash)
        }
    }

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(),
        onClick = {}
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
        ) {
            TextBoldRegular(
                text = stringResource(id = R.string.payment_options),
                modifier = Modifier.wrapContentHeight(),
                textStyle = TextStyle(
                    color = colorResource(id = R.color.colorBlackShadow)
                )
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                paymentOptionsList.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .selectable(
                                selected = (option.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                                onClick = {
                                    passengerDetailsViewModel.selectedPaymentOptionId =
                                        option.id.toString().toInt()
                                    passengerDetailsViewModel.selectedPaymentOption =
                                        option.paymentType!!
                                    onPaymentOptionSelection(
                                        passengerDetailsViewModel.selectedPaymentOption.asString(
                                            context.resources
                                        )
                                    )
                                }
                            )
                    ) {
                        RadioButton(
                            selected = (option.paymentType == passengerDetailsViewModel.selectedPaymentOption),
                            modifier = Modifier
                                .requiredHeight(20.dp)
                                .absoluteOffset((-10).dp, 0.dp),
                            onClick = {
                                passengerDetailsViewModel.selectedPaymentOptionId =
                                    option.id.toString().toInt()
                                passengerDetailsViewModel.selectedPaymentOption =
                                    option.paymentType!!
                                onPaymentOptionSelection(
                                    passengerDetailsViewModel.selectedPaymentOption.asString(
                                        context.resources
                                    )
                                )
                            }
                        )
                        TextNormalSmall(
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp),
                            text = option.paymentType?.asString(context.resources) ?: ""
                        )
                    }
                }
            }
        }
    }
}