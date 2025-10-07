package com.example.buscoach

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView


class TooltipWindow(
    private val ctx: Context,
    private val header: String,
    private val description: String,
    private val buttonLabel: String,
    private val onButtonClick: (() -> Unit)
) {
    private val tipWindow: PopupWindow?
    private val contentView: View
    private val inflater: LayoutInflater


    init {
        tipWindow = PopupWindow(ctx)
        inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        contentView = inflater.inflate(R.layout.service_title_walkthrough, null)
    }

    fun showToolTip(anchor: View) {/*contentView.measure(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )*//*val scale: Float = ctx.resources.displayMetrics.density
        val pixels = (200 * scale + 0.5f)

        val layoutParams = LinearLayout.LayoutParams(100, pixels.toInt())
        contentView.layoutParams = layoutParams
        contentView.invalidate()*/

        tipWindow!!.height = LinearLayout.LayoutParams.WRAP_CONTENT
        tipWindow!!.width = LinearLayout.LayoutParams.WRAP_CONTENT

        tipWindow!!.isOutsideTouchable = true
        tipWindow!!.isTouchable = true
        tipWindow!!.isFocusable = true
        tipWindow!!.setBackgroundDrawable(BitmapDrawable())

        tipWindow!!.contentView = contentView

        val screen_pos = IntArray(2)
        // Get location of anchor view on screen
        // Get location of anchor view on screen
        anchor.getLocationOnScreen(screen_pos)

        // Get rect for anchor view

        // Get rect for anchor view
        val anchor_rect = Rect(
            screen_pos[0],
            screen_pos[1],
            screen_pos[0] + anchor.width,
            screen_pos[1] + anchor.height
        )

        // Call view measure to calculate how big your view should be.

        // Call view measure to calculate how big your view should be.
        contentView.measure(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val tvHeader = contentView.findViewById<TextView>(R.id.tvHeader)
        val tvDescription = contentView.findViewById<TextView>(R.id.tvDescription)
        val tvButton = contentView.findViewById<TextView>(R.id.tvNext)

        tvHeader.text = header
        tvDescription.text = description
        tvButton.text = buttonLabel

        tvButton.setOnClickListener {
            onButtonClick.invoke()
            dismissTooltip()
        }

        val contentViewHeight = contentView.measuredHeight
        val contentViewWidth = contentView.measuredWidth
        // In this case , i dont need much calculation for x and y position of
        // tooltip
        // For cases if anchor is near screen border, you need to take care of
        // direction as well
        // to show left, right, above or below of anchor view
        // In this case , i dont need much calculation for x and y position of
        // tooltip
        // For cases if anchor is near screen border, you need to take care of
        // direction as well
        // to show left, right, above or below of anchor view
        val position_x = anchor_rect.centerX() - contentViewWidth / 2
        val position_y = anchor_rect.bottom - anchor_rect.height() / 2

        tipWindow!!.showAtLocation(anchor, Gravity.NO_GRAVITY, position_x, position_y)

        // send message to handler to dismiss tipWindow after X milliseconds

        // send message to handler to dismiss tipWindow after X milliseconds
        //handler.sendEmptyMessageDelayed(MSG_DISMISS_TOOLTIP, 10000)
    }

    val isTooltipShown: Boolean
        get() = tipWindow != null && tipWindow.isShowing

    fun dismissTooltip() {
        if (tipWindow != null && tipWindow.isShowing) tipWindow.dismiss()
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_DISMISS_TOOLTIP -> if (tipWindow != null && tipWindow.isShowing) tipWindow.dismiss()
            }
        }
    }


    companion object {
        private const val MSG_DISMISS_TOOLTIP = 100
    }
}