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

    // Create Cash payment option
    val cashOption = remember {
        SearchModel().apply {
            id = "1"
            paymentType = ResourceProvider.TextResource.fromStringId(R.string.cash)
        }
    }

    // 🔥 Auto-select Cash when composable is created
    LaunchedEffect(Unit) {
        passengerDetailsViewModel.selectedPaymentOptionId = 1
        passengerDetailsViewModel.selectedPaymentOption = cashOption.paymentType!!

        onPaymentOptionSelection(
            cashOption.paymentType!!.asString(context.resources)
        )
    }

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(),
        onClick = {} // REQUIRED by CardComponent
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {

                    RadioButton(
                        selected = true,
                        onClick = {
                            // Optional manual click support
                            onPaymentOptionSelection(
                                cashOption.paymentType!!.asString(context.resources)
                            )
                        },
                        modifier = Modifier.requiredHeight(20.dp)
                    )

                    TextNormalSmall(
                        modifier = Modifier.padding(start = 4.dp),
                        text = cashOption.paymentType!!.asString(context.resources)
                    )
                }
            }
        }
    }
}