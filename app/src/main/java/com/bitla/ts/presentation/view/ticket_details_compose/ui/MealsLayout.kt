package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealsLayout(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>) {
    if (ticketDetailsComposeViewModel.mealCouponList.isNotEmpty()) {
        TextNormalRegular(
            text = stringResource(id = R.string.meal_coupons),
            modifier = Modifier.padding(top = 16.dp)
        )

        FlowRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ticketDetailsComposeViewModel.mealCouponList.forEach {
                Text(
                    text = it, style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    if (ticketDetailsComposeViewModel.mealTypeList.isNotEmpty()) {
        TextNormalRegular(
            text = stringResource(id = R.string.meal_type), modifier = Modifier.padding(top = 16.dp)
        )

        FlowRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            ticketDetailsComposeViewModel.mealTypeList.forEach {
                TextBoldRegular(
                    text = it,
                    modifier = Modifier,
                    textStyle = TextStyle(color = colorResource(id = R.color.colorBlackShadow))
                )
            }
        }
    }
}
