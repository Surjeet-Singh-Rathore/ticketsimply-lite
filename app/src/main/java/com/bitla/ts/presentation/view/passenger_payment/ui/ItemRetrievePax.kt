package com.bitla.ts.presentation.view.passenger_payment.ui

import android.content.Context
import androidx.compose.runtime.*
import com.bitla.ts.presentation.components.*
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import timber.log.Timber

@Composable
fun ItemRetrievePax(
    context: Context,
    passengerDetailsViewModel: PassengerDetailsViewModel<Any?>,
    onCheckedChange: (Int, Boolean) -> Unit,
) {

    RetrievePaxDialog(
        passengerDetailsViewModel = passengerDetailsViewModel,
        onDismiss = {
            passengerDetailsViewModel.showDialog.value = false
            passengerDetailsViewModel.checkedPassengerList.clear()
        },
        onCheckedChange = { index, isChecked ->
            onCheckedChange(index, isChecked)
        },
        onUpdatePax = {
            if (passengerDetailsViewModel.checkedPassengerList.isNotEmpty()) {
                Timber.d("checkedPassengerListPosition ${passengerDetailsViewModel.checkedPassengerList.size}")
                for (i in 0..passengerDetailsViewModel.checkedPassengerList.size.minus(1)) {
                    if (passengerDetailsViewModel.checkedPassengerList[i].isChecked) {
                        passengerDetailsViewModel.apply {
                            if(checkedPassengerList[i].name.contains(" ")) {
                                setFirstName(i, checkedPassengerList[i].name.substringBefore(" "))
                                setLastName(i, checkedPassengerList[i].name.substringAfter(" "))
                            } else {
                                setFirstName(i, checkedPassengerList[i].name)
                                setLastName(i, "")
                            }
                            setName(i, if(checkedPassengerList[i].name != null) checkedPassengerList[i].name else "")
                            setAge(i, if(checkedPassengerList[i].passenger_age != null) checkedPassengerList[i].passenger_age.toString() else "")
                            setGender(i, if(checkedPassengerList[i].passenger_title != null) checkedPassengerList[i].passenger_title else "")
                            if (passengerDetailsViewModel.passengerDataList[i].isExtraSeat) {
                                setExtraSeatFare(i, checkedPassengerList[i].ticket_fare.toString())
                                setExtraSeatNo(i, checkedPassengerList[i].seat_numbers)
                            }
                        }

                    } else {
                        passengerDetailsViewModel.apply {
                            setFirstName(i, "")
                            setName(i, "")
                            setAge(i, "")
                            setGender(i, "")
                            if (passengerDetailsViewModel.passengerDataList[i].isExtraSeat) {
                                setExtraSeatFare(i, "")
                                setExtraSeatNo(i, "")
                            }
                        }
                    }
                }

            } else {
                passengerDetailsViewModel.passengerDataList.forEachIndexed { i, passengerDetailsResult ->
                    passengerDetailsViewModel.apply {
                        setFirstName(i, "")
                        setName(i, "")
                        setAge(i, "")
                        setGender(i, "")
                        if (passengerDetailsViewModel.passengerDataList[i].isExtraSeat) {
                            setExtraSeatFare(i, "")
                            setExtraSeatNo(i, "")
                        }
                    }
                }
            }
            passengerDetailsViewModel.showDialog.value = false
            passengerDetailsViewModel.isRetrieveClicked.value = false
            passengerDetailsViewModel.checkedPassengerList.clear()
        }
    )
}