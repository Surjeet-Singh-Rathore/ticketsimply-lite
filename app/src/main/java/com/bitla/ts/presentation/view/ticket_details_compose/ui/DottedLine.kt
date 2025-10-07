package com.bitla.ts.presentation.view.ticket_details_compose.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bitla.ts.R

@Composable
fun DottedLine() {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val dottedLineColor = colorResource(id = R.color.view_color)
    Canvas(
        Modifier
            .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            .height(1.dp)
            .fillMaxWidth()
    ) {

        drawLine(
            color = dottedLineColor,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}