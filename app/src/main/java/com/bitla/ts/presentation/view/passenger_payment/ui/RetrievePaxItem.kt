package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel

@Composable
fun RetrievePaxItem(
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    paxIndex: Int
) {

    Row(
        Modifier.fillMaxWidth()
    ) {

        Checkbox(
            checked = if (passengerDetailsViewModel.checkedPassengerList.size != 0) {
                passengerDetailsViewModel.checkedPassengerList[paxIndex].isChecked
            } else {
                false
            },
            onCheckedChange = {

                val checkedPassenger = passengerDetailsViewModel.passengerHistoryList[paxIndex]

                passengerDetailsViewModel.apply {
                    if (checkedPassengerList.size != 0) {
                        checkedPassengerList[paxIndex].isChecked = false
                        checkedPassengerList.remove(checkedPassenger)
                    } else {
                        checkedPassengerList.add(checkedPassenger)
                        checkedPassengerList[paxIndex].isChecked = true
                    }
                }
            }
        )

        TextBoldSmall(
            text = passengerDetailsViewModel.passengerHistoryList[paxIndex].name,
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Start
        )
    }
}
