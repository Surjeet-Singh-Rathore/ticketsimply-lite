package com.bitla.ts.presentation.view.passenger_payment_show_new_flow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.bitla.ts.R
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.*

@Composable
fun RamarksCardNewFlow(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {

    CardComponent(
        shape = RoundedCornerShape(4.dp),
        bgColor = colorResource(id = R.color.white), modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .wrapContentHeight(),
        onClick = {}
    ) {
        Row (
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp)
            
        ) {
           
//            TextBoldLarge(
//                text = stringResource(id = R.string.remarks),
//                modifier = Modifier.padding(top=14.dp),
//                style = TextStyle(
//                    colorResource(id = R.color.black),
//                )
//            )

            TextFieldComponentRounded(
                modifier = Modifier
                    .fillMaxWidth(),
                value = passengerDetailsViewModel.remarks,
                onValueChange = {
                    passengerDetailsViewModel.remarks = it
                },
                label = stringResource(id = R.string.remarks) ,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            )
        }
    }
}