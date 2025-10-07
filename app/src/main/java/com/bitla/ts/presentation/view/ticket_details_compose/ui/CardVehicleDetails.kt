package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@Composable
fun CardVehicleDetails(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>) {
    Card(
        modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(
            topStartPercent = 10,
            topEndPercent = 10,
            bottomEndPercent = 10,
            bottomStartPercent = 10
        )
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                .fillMaxWidth()
        ) {
            TextNormalRegular(text = stringResource(id = R.string.vehicle_details), modifier = Modifier)

            TextBoldRegular(
                text = ticketDetailsComposeViewModel.vehicleDetails,
                modifier = Modifier,
                textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
            )

            TextNormalRegular(
                text = stringResource(id = R.string.service_number), modifier = Modifier.padding(top = 16.dp)
            )
            TextBoldRegular(
                text = ticketDetailsComposeViewModel.serviceNumber,
                modifier = Modifier,
                textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
            )
        }
    }
}