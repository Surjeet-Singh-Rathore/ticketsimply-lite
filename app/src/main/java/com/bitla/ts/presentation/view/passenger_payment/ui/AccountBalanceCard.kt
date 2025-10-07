package com.bitla.ts.presentation.view.passenger_payment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.viewModel.PassengerDetailsViewModel
import com.bitla.ts.presentation.components.CardComponent
import com.bitla.ts.presentation.components.TextBoldSmall
import com.bitla.ts.presentation.components.TextNormalSmall
import com.bitla.ts.utils.common.convert

@Composable
fun AccountBalanceCard(passengerDetailsViewModel: PassengerDetailsViewModel<Any?>) {
        CardComponent(
            shape = RoundedCornerShape(0.dp),
            bgColor = colorResource(id = R.color.white), modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {}
        )
        {
            Row(
                Modifier.height(46.dp), horizontalArrangement = Arrangement.End
            ) {
                TextBoldSmall(
                    text = stringResource(id = R.string.balance_amount ),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = TextStyle(
                        colorResource(id = R.color.colorBlackShadow),
                        fontFamily = FontFamily(Font(R.font.notosans_regular))
                    ),
                    textAlign = TextAlign.Start,
                )

                TextNormalSmall(
                    text = passengerDetailsViewModel.getAvailableBalance,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
                    textStyle = TextStyle(
                        colorResource(id = R.color.colorPrimary),
                        fontFamily = FontFamily(Font(R.font.notosans_regular))
                    )
                )
            }
        }
}