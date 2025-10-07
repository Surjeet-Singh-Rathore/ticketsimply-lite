package com.example.buscoach

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.buscoach.line_coordinates.Coordinates
import com.example.buscoach.line_coordinates.LineCoordinates
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.utils.Const
import java.util.Locale

class CustomGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var samePNRChildren = mutableListOf<View>()
    private var seatDetailsMap = hashMapOf<SeatDetail, View>()
    private var currentPNR = ""
    private var coachType = Const.LOWER_COACH
    private var isDrawBorder = false
    private var seatDetail: SeatDetail? = null

    fun drawBorder(coachType: Int, pnr: String?, seatDetailsMap: HashMap<SeatDetail, View>, isDrawBorder: Boolean, seatDetail: SeatDetail?) {
        currentPNR = pnr ?: ""
        this.coachType = coachType
        this.seatDetailsMap = seatDetailsMap
        this.isDrawBorder = isDrawBorder
        this.seatDetail = seatDetail
        generateSamePNRChildrenListNew()

        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (samePNRChildren.isNotEmpty() && isDrawBorder) {
            drawDashedBorder(canvas)
        }
    }

    private fun drawDashedBorder(canvas: Canvas) {
        val paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            style = Paint.Style.STROKE
            strokeWidth = 5f
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }

        val borderLineCoordinates = HashMap<LineCoordinates, Int>() // Store drawn lines as pairs of start and end points along with their counts

        samePNRChildren.forEach {

            val startX = it.left.toFloat()
            val startY = it.top.toFloat()
            val endX = it.right.toFloat()
            val endY = it.bottom.toFloat()

            val topLeft = Coordinates(startX,startY)
            val topRight = Coordinates(endX,startY)
            val bottomLeft = Coordinates(startX,endY)
            val bottomRight = Coordinates(endX,endY)

            val topLeftToTopRight = LineCoordinates(topLeft, topRight)
            val topRightToBottomRight = LineCoordinates(topRight, bottomRight)
            val bottomRightToBottomLeft = LineCoordinates(bottomRight, bottomLeft)
            val bottomLeftToTopLeft = LineCoordinates(bottomLeft, topLeft)

            val topRightToTopLeft = LineCoordinates(topRight, topLeft)
            val bottomRightToTopRight = LineCoordinates(bottomRight, topRight)
            val bottomLeftToBottomRight = LineCoordinates( bottomLeft, bottomRight)
            val topLeftToBottomLeft = LineCoordinates(topLeft, bottomLeft)

            if(borderLineCoordinates.containsKey(topLeftToTopRight)) {
                borderLineCoordinates[topLeftToTopRight] = (borderLineCoordinates[topLeftToTopRight] ?: 0)+1
            } else {
                borderLineCoordinates[topLeftToTopRight] = 1
            }

            if(borderLineCoordinates.containsKey(topRightToBottomRight)) {
                borderLineCoordinates[topRightToBottomRight] = (borderLineCoordinates[topRightToBottomRight] ?: 0)+1
            } else {
                borderLineCoordinates[topRightToBottomRight] = 1
            }

            if(borderLineCoordinates.containsKey(bottomRightToBottomLeft)) {
                borderLineCoordinates[bottomRightToBottomLeft] = (borderLineCoordinates[bottomRightToBottomLeft] ?: 0)+1
            } else {
                borderLineCoordinates[bottomRightToBottomLeft] = 1
            }

            if(borderLineCoordinates.containsKey(bottomLeftToTopLeft)) {
                borderLineCoordinates[bottomLeftToTopLeft] = (borderLineCoordinates[bottomLeftToTopLeft] ?: 0)+1
            } else {
                borderLineCoordinates[bottomLeftToTopLeft] = 1
            }

            if(borderLineCoordinates.containsKey(topRightToTopLeft)) {
                borderLineCoordinates[topRightToTopLeft] = (borderLineCoordinates[topRightToTopLeft] ?: 0)+1
            } else {
                borderLineCoordinates[topRightToTopLeft] = 1
            }

            if(borderLineCoordinates.containsKey(bottomRightToTopRight)) {
                borderLineCoordinates[bottomRightToTopRight] = (borderLineCoordinates[bottomRightToTopRight] ?: 0)+1
            } else {
                borderLineCoordinates[bottomRightToTopRight] = 1
            }

            if(borderLineCoordinates.containsKey(bottomLeftToBottomRight)) {
                borderLineCoordinates[bottomLeftToBottomRight] = (borderLineCoordinates[bottomLeftToBottomRight] ?: 0)+1
            } else {
                borderLineCoordinates[bottomLeftToBottomRight] = 1
            }

            if(borderLineCoordinates.containsKey(topLeftToBottomLeft)) {
                borderLineCoordinates[topLeftToBottomLeft] = (borderLineCoordinates[topLeftToBottomLeft] ?: 0)+1
            } else {
                borderLineCoordinates[topLeftToBottomLeft] = 1
            }
        }

        borderLineCoordinates.forEach {
            if(it.value == 1 ) {
                val path = Path()
                path.moveTo(it.key.lineStartCoordinates.xAxis, it.key.lineStartCoordinates.yAxis)
                path.lineTo(it.key.lineEndCoordinates.xAxis, it.key.lineEndCoordinates.yAxis)
                canvas.drawPath(path, paint)
            }
        }

    }

    private fun generateSamePNRChildrenList() {

        samePNRChildren.clear()
        if(currentPNR.isNotEmpty()) {
            seatDetailsMap.forEach {
                if (it.key.passengerDetails?.ticketNo.equals(currentPNR)) {
                    if(coachType == Const.UPPER_COACH) {
                        if (it.key.type?.lowercase(Locale.getDefault())
                                ?.contains("upper") == true || it.key.type?.lowercase(Locale.getDefault()) == "ub" || (!it.key.floorType.isNullOrBlank() && it.key.floorType?.contains(
                                "2"
                            ) == true)
                        ) {
                            val samePNRChild = it.value
                            samePNRChildren.add(samePNRChild)

                            Log.d("CustomGridLayout", "Upper_Coach_Selected"+it.key.number)

                        }
                    } else {
                        if (it.key.type?.lowercase(Locale.getDefault())
                                ?.contains("upper") == true || it.key.type?.lowercase(Locale.getDefault()) == "ub" || (!it.key.floorType.isNullOrBlank() && it.key.floorType?.contains(
                                "2"
                            ) == true)
                        ) {} else {
                            val samePNRChild = it.value
                            samePNRChildren.add(samePNRChild)
                        }
                    }
                } /*else {
                    if(it.key.isSelected) {
                        it.value.performClick()
                    }
                }*/
            }
        }
    }
    private fun generateSamePNRChildrenListNew() {

        samePNRChildren.clear()

        if(currentPNR.isNotEmpty() && seatDetail != null) {
            seatDetailsMap.forEach {
                if(it.key.passengerDetails?.ticketNo?.equals(currentPNR) == true || it.key.otherPnrNumber?.contains(currentPNR) == true)
                    if (coachType == Const.UPPER_COACH) {
                        if (it.key.type?.lowercase(Locale.getDefault())
                                ?.contains("upper") == true || it.key.type?.lowercase(Locale.getDefault()) == "ub" || (!it.key.floorType.isNullOrBlank() && it.key.floorType?.contains(
                                "2"
                            ) == true)
                        ) {
                            val samePNRChild = it.value
                            samePNRChildren.add(samePNRChild)

                            Log.d(
                                "CustomGridLayout",
                                "Upper_Coach_Selected" + it.key.number
                            )

                        }
                    } else {
                        if (it.key.type?.lowercase(Locale.getDefault())
                                ?.contains("upper") == true || it.key.type?.lowercase(Locale.getDefault()) == "ub" || (!it.key.floorType.isNullOrBlank() && it.key.floorType?.contains(
                                "2"
                            ) == true)
                        ) {
                        } else {
                            val samePNRChild = it.value
                            samePNRChildren.add(samePNRChild)
                        }
                    }

            }

        }
    }
}