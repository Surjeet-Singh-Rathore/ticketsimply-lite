package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bitla.ts.R
import com.bitla.ts.presentation.components.TextBoldLarge
import com.bitla.ts.presentation.components.TextBoldRegular
import com.bitla.ts.presentation.components.TextNormalRegular
import com.bitla.ts.presentation.viewModel.TicketDetailsComposeViewModel

@Composable
fun PNRLayout(ticketDetailsComposeViewModel: TicketDetailsComposeViewModel<Any?>) {
    ConstraintLayout {

        val horizontalGuideline = createGuidelineFromTop(0.5f)

        val spacerTop = createRef()
        val spacerBottom = createRef()

        Spacer(modifier = Modifier
            .constrainAs(spacerTop) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(horizontalGuideline)
                height = Dimension.fillToConstraints
            }
            .fillMaxWidth()
            .background(
                color = ticketDetailsComposeViewModel.headerBgColor
                    ?: colorResource(R.color.colorPrimary)
            ))

        Spacer(modifier = Modifier
            .constrainAs(spacerBottom) {
                start.linkTo(parent.start)
                top.linkTo(horizontalGuideline)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            .background(
                color = colorResource(R.color.flash_white_bg)
            )

        )

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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TextBoldLarge(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "${stringResource(id = R.string.pnr)} - ${ticketDetailsComposeViewModel.pnrNumber}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(
                            id = R.color.colorBlackShadow
                        )
                    )
                )
                if(ticketDetailsComposeViewModel.country.equals("indonesia", true) && !ticketDetailsComposeViewModel.terminalRefNo.isNullOrEmpty()) {
                    TextBoldRegular(
                        text = "${stringResource(id = R.string.terminal_ticket_id)} - ${ticketDetailsComposeViewModel.terminalRefNo}",
                        modifier = Modifier,
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.colorBlackShadow),
                            fontSize = 12.sp
                        )
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    text = "${stringResource(id = R.string.issuedBy)} ${ticketDetailsComposeViewModel.ticketBookedBy}",
                    textAlign = TextAlign.Center
                )

            }
        }
    }
}