package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextFieldComponent
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel

@Composable
fun RemarksCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .wrapContentHeight(),
        onClick = {}
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .imePadding()
        ) {
            TextBoldRegular(
                text = stringResource(id = R.string.remarks),
                modifier = Modifier,
                textStyle = TextStyle(
                    color = colorResource(
                        id = R.color.colorBlackShadow
                    )
                )
            )

            TextFieldComponent(context = null,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                value = passengerDetailsViewModel.remarks,
                label = stringResource(id = R.string.enter_remarks),
                onValueChange = {
                    passengerDetailsViewModel.remarks = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            )
        }
    }
}