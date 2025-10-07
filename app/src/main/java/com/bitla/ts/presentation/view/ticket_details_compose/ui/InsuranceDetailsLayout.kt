package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel
import com.bitla.ts.utils.common.convert

@Composable
fun InsuranceDetailsLayout(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>,
                           onInfoButtonClick: (() -> Unit)) {
    val bookingAmountText =
        "${ticketDetailsComposeViewModel.currency} ${ticketDetailsComposeViewModel.totalInsuranceAmt.convert(ticketDetailsComposeViewModel.currencyFormat)}"
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        TextNormalRegular(text = stringResource(id = R.string.insurance_amount), modifier = Modifier)

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextBoldRegular(
                text = bookingAmountText,
                modifier = Modifier,
                textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
            )

            if(ticketDetailsComposeViewModel.insuranceTransDetails.value.details?.isNotEmpty() == true) {
                Image(painter = painterResource(id = R.drawable.ic_info_),
                    contentDescription = "Insurance Info Button",
                    modifier = Modifier.clickable {
                        onInfoButtonClick.invoke()
                    }

                )
            }
        }
    }
}