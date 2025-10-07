package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.bitla.ts.R

@Composable
fun OrTextLayout() {

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.or),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontStyle = FontStyle.Italic,
        )
    )
}