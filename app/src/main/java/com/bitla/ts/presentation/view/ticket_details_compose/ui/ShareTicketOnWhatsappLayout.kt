package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldRegular

@Composable
fun ShareTicketOnWhatsappLayout(onClick: (() -> Unit)) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, colorResource(id = R.color.colorPrimary)),
                RoundedCornerShape(
                    topStartPercent = 10,
                    topEndPercent = 10,
                    bottomEndPercent = 10,
                    bottomStartPercent = 10
                )
            )
            .background(colorResource(id = R.color.light_green))
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Image(
            painter = painterResource(id = R.drawable.whatsapp),
            contentDescription = "Whatsapp Icon",
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
                .height(24.dp)
                .width(24.dp),
        )

        TextBoldRegular(
            text = stringResource(R.string.share_ticket_on_whatsapp),
            modifier = Modifier,
            textStyle = TextStyle(
                color = colorResource(id = R.color.button_secondary_bg),
            ),
        )
    }
}