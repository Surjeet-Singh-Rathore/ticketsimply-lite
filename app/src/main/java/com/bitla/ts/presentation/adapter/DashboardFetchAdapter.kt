package com.bitla.ts.presentation.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnItemPinnedListener
import com.bitla.ts.databinding.ChildDashboardFetchBinding
import com.bitla.ts.domain.pojo.dashboard_fetch.response.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.phase2.chartUtils.IntValueFormatter
import com.bitla.ts.phase2.chartUtils.ReportValue
import com.bitla.ts.utils.common.firebaseLogEvent
import com.bitla.ts.utils.common.getDateDMMM
import com.bitla.ts.utils.common.getNextDate
import com.bitla.ts.utils.common.getPreviousDate
import com.bitla.ts.utils.common.roundOffDecimal
import com.bitla.ts.utils.constants.VIEW_DETAILS
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import gone
import timber.log.Timber
import visible
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt


class DashboardFetchAdapter(
    //private val context: Context,
    //private var result: MutableList<Result>
    private val onItemClickListener: OnItemClickListener,
    private val onItemPinnedListener: OnItemPinnedListener,
    private val parentDate: String,
    private val enableNewOwnerDashboardWithBusinessMetrics: Boolean?,
    val privileges: PrivilegeResponseModel?
) : RecyclerView.Adapter<DashboardFetchAdapter.ViewHolder>() {

    private var reportList = ArrayList<ReportValue>()
    private lateinit var context: Context

    private var currency = privileges?.currency ?: ""
    private var country = privileges?.country ?: ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ChildDashboardFetchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val item = result?.get(position)
        val item = differ.currentList[position]


        holder.yesterdayTV.text = getPreviousDate(parentDate)
        holder.todayTV.text = getDateDMMM(parentDate)
        holder.tomorrowTV.text = getNextDate(parentDate)



        if (item.label.equals("service_wise_booking", true)) {
            holder.cardViewOccupancy.gone()
            holder.cardViewSchedulesSummary.gone()
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.rvSeatWiseBooking.layoutManager = layoutManager
            val serviceWiseBookingAdapter =
                ServiceWiseBookingAdapter(context, item.bookingDetails)
            holder.rvSeatWiseBooking.adapter = serviceWiseBookingAdapter

        } else if (item.label.equals("occupancy", true)
            || item.label.equals("revenue", true)
            || item.label.equals("performance", true)
        ) {
            holder.cardViewServiceWise.gone()
            holder.cardViewSchedulesSummary.gone()
            var newLabel = ""
            if (item.label.isEmpty().not()) {
                newLabel += item.label.substring(0, 1).toUpperCase() + item.label.substring(1) + " "
            }
            if (item.label.equals("revenue", true)) {
                newLabel = context.getString(R.string.net_revenue)
            }
            if (item.label.equals("performance", true)) {
                newLabel = context.getString(R.string.booking_trends)
            }
            holder.tvOccupancyHeader.text = newLabel
            holder.tvLastUpdateOccupancy.text = "Last Updated: ${item.lastUpdated}"

            if (item.label.equals("occupancy", true)) {
                newLabel = context.getString(R.string.occupancy)
                holder.tvOccupancyHeader.text = newLabel
                if(enableNewOwnerDashboardWithBusinessMetrics == true) {
                    holder.tvTotalOccupancy.text = item?.todayBySeats
                    val percentage = item.yesterdayBySeats?.substringBefore("/")?.toDouble()?.div(item.yesterdayBySeats.substringAfter("/").toDouble())?.times(100)
                    setColorByPercentage(holder.tvTotalOccupancy, percentage.toString())

                } else {
                    holder.tvTotalOccupancy.text = "${
                        item.totalOccupancy?.substring(
                            0,
                            min(4, item.totalOccupancy.toString().length)
                        )
                    }%"
                    setColorByPercentage(holder.tvTotalOccupancy, item.totalOccupancy)

                }
            } else if (item.label.equals("revenue", true)) {
                var totalRevenueCurrency = item.totalRevenue
                if (totalRevenueCurrency != null) {
                    /*if (abs(totalRevenueCurrency.toDouble().roundToInt() / 1000000) > 1) {
                        (totalRevenueCurrency.toDouble().roundToInt() / 1000000).toString() + "M"
                    } else */
                    if(country == "Indonesia") {
                        totalRevenueCurrency = if (abs(totalRevenueCurrency.toDouble().roundToInt() / 1000000000) > 1) {
                            (totalRevenueCurrency.toDouble().roundToInt() / 1000000000).toString() + "M"
                        } else if (abs(totalRevenueCurrency.toDouble().roundToInt() / 1000000) > 1) {
                            (totalRevenueCurrency.toDouble().roundToInt() / 1000000).toString() + "Jt"
                        }else if (abs(totalRevenueCurrency.toDouble().roundToInt() / 1000) > 1) {
                            (totalRevenueCurrency.toDouble().roundToInt() / 1000).toString() + "Rb"
                        } else {
                            totalRevenueCurrency.toDouble().roundToInt().toString()
                        }
                    } else {
                        totalRevenueCurrency =
                            if (abs(totalRevenueCurrency.toDouble().roundToInt() / 100000) > 1) {
                                (totalRevenueCurrency.toDouble().roundToInt() / 100000).toString() + "L"
                            } else if (abs(totalRevenueCurrency.toDouble().roundToInt() / 1000) > 1) {
                                (totalRevenueCurrency.toDouble().roundToInt() / 1000).toString() + "k"
                            } else {
                                totalRevenueCurrency.toDouble().roundToInt().toString()
                            }
                    }

                }
                holder.tvTotalOccupancy.text = "$currency${totalRevenueCurrency}"
                holder.tvTotalOccupancy.setTextColor(context.resources.getColor(R.color.color_black_revenue))

            } else if (item.label.equals("performance", true)) {
                //holder.tvTotalOccupancy.text = "${item.totalPerformance?.substring(0, min(4, item.totalPerformance.toString().length))}%"
                if (item.totalPerformance?.contains("100.0") == true) {
                    holder.tvTotalOccupancy.text = "100%"
                    setColorByPercentage(holder.tvTotalOccupancy, "100")

                } else {
                    holder.tvTotalOccupancy.text = "${item.totalPerformance}%"
                    setColorByPercentage(holder.tvTotalOccupancy, item.totalPerformance)

                }
            }
            if (item.isPinned == 1) {
                holder.imgPinOccupancy.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }
            if (item.label.equals("occupancy", true)
                || item.label.equals("revenue", true)
            ) {

                if (item.today != null) {
                    val diff = item.today.toDouble().minus(item.yesterday.toString().toDouble())
                    val diffRevenue =
                        item.today.toDouble().minus(item.yesterday.toString().toDouble())
                            .roundToInt()

                    /*if (abs(diffRevenue / 1000000) > 1) {
                        (diffRevenue / 1000000).toString() + "M"
                    }
                    else */
                    val todayMinusYest = if (abs(diffRevenue / 100000) > 1) {
                        (diffRevenue / 100000).toString() + "L"
                    } else if (abs(diffRevenue / 1000) > 1) {
                        (diffRevenue / 1000).toString() + "k"
                    } else {
                        diffRevenue.toString()
                    }

                    if (diff != null) {
                        if (diff <= 0.0) {
                            if (item.label.equals("revenue", true)) {
                                val yesterdayVsToday = "$currency${todayMinusYest}"
                                val spanlength = yesterdayVsToday.length
                                val spannableString = SpannableString(
                                    "$yesterdayVsToday vs ${
                                        getPreviousDate(parentDate)
                                    }"
                                )
                                val red = ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.colorRed2
                                    )
                                )
                                spannableString.setSpan(
                                    red,
                                    0,
                                    spanlength,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                holder.imageArrowOccupancy.visible()
                                holder.tvTodaySumOccupancy.text = spannableString
                                holder.imageArrowOccupancy.setImageResource(R.drawable.ic_down_arrow_new)
                            } else {
                                val yesterdayVsToday = "${
                                    diff.toString().substring(0, min(4, diff.toString().length))
                                }%"
                                val spanlength = yesterdayVsToday.length
                                val spannableString = SpannableString(
                                    "$yesterdayVsToday vs ${
                                        getPreviousDate(parentDate)
                                    }"
                                )
                                val red = ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.colorRed2
                                    )
                                )
                                spannableString.setSpan(
                                    red,
                                    0,
                                    spanlength,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                holder.imageArrowOccupancy.visible()
                                holder.tvTodaySumOccupancy.text = spannableString
                                holder.imageArrowOccupancy.setImageResource(R.drawable.ic_down_arrow_new)
                            }
                        } else {

                            if (item.label.equals("revenue", true)) {
                                val yesterdayVsToday = "$currency+${todayMinusYest}"
                                val spanlength = yesterdayVsToday.length
                                val spannableString = SpannableString(
                                    "$yesterdayVsToday vs ${
                                        getPreviousDate(parentDate)
                                    }"
                                )
                                val red = ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.booked_tickets
                                    )
                                )
                                spannableString.setSpan(
                                    red,
                                    0,
                                    spanlength,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                holder.imageArrowOccupancy.visible()
                                holder.tvTodaySumOccupancy.text = spannableString
                                holder.imageArrowOccupancy.setImageResource(R.drawable.ic_up_arrow_new)
                            } else {
                                val yesterdayVsToday = "+${
                                    diff.toString().substring(0, min(4, diff.toString().length))
                                }%"
                                val spanlength = yesterdayVsToday.length
                                val spannableString = SpannableString(
                                    "$yesterdayVsToday vs ${
                                        getPreviousDate(parentDate)
                                    }"
                                )
                                val red = ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.booked_tickets
                                    )
                                )
                                holder.imageArrowOccupancy.visible()
                                spannableString.setSpan(
                                    red,
                                    0,
                                    spanlength,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                holder.tvTodaySumOccupancy.text = spannableString
                                holder.imageArrowOccupancy.setImageResource(R.drawable.ic_up_arrow_new)
                            }

                        }
                    }
                }
                if (item.label.equals("occupancy", true)) {
                    //changeTextColor(holder.tvTotalOccupancy,item.totalOccupancy)
                } else if (item.label.equals("performance", true)) {
                    //changeTextColor(holder.tvTotalOccupancy,item.totalPerformance)
                } else if (item.label.equals("revenue", true)) {
                    //changeTextColor(holder.tvTotalOccupancy,item.totalRevenue)
                }
            }
            if(enableNewOwnerDashboardWithBusinessMetrics == true && item.label.equals("Occupancy", true)) {
                setOccupancyBySeatBarChart(holder,position)
            } else {
                setOccupancyBarChart(holder, position)
            }
            holder.imgPinOccupancy.setOnClickListener {
                if (item.isPinned == 1) {
                    holder.imgPinOccupancy.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.grey
                        )
                    )
                    item.isPinned = 0
                } else {
                    holder.imgPinOccupancy.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                    item.isPinned = 1
                }
                onItemPinnedListener.onItemClick(item.isPinned.toString(), position, item.label)

            }

        } else if (item.label.equals("schedules_summary_active_cancelled", true)
            || item.label.equals("time_blocked_seats_booked_released", true)
            || item.label.equals("total_pending_quota_seats", true)
        ) {


            val newLabelArray = item.label.split("_")
            var newLabel = ""
            newLabelArray.forEach {
                if (it.isEmpty().not()) {
                    newLabel += it.substring(0, 1).toUpperCase() + it.substring(1) + " "
                }
            }

            if (item.label.equals("total_pending_quota_seats", true)) {
                newLabel = context.getString(R.string.pending_quota)
            }

            if (item.label.equals("schedules_summary_active_cancelled", true)) {
                holder.tvScheduleSummary.text =
                    context.getText(R.string.schedules_summary)
                holder.activeCancelTV.visible()
                holder.activeCancelTV.text = context.getString(R.string.active_cancelled)

            } else if (item.label.equals("time_blocked_seats_booked_released", true)) {
                holder.tvScheduleSummary.text = context.getText(R.string.phone_bookings)
                holder.activeCancelTV.visible()
                holder.activeCancelTV.text = context.getString(R.string.pending_released)
            } else {
                holder.tvScheduleSummary.text = newLabel
                holder.activeCancelTV.gone()
            }

            holder.cardViewServiceWise.gone()
            holder.cardViewOccupancy.gone()
            holder.tvYesterdayValue.text = item.yesterday
            holder.tvTodayValue.text = item.today.toString()
            holder.tvTomorrowValue.text = item.tomorrow.toString()
        }

        holder.tvViewDetails.setOnClickListener {
            onItemClickListener.onClickOfItem(item.label, position)

            firebaseLogEvent(
                context,
                VIEW_DETAILS,
                PreferenceUtils.getLogin().userName,
                PreferenceUtils.getLogin().travels_name,
                PreferenceUtils.getLogin().role,
                VIEW_DETAILS,
                "View Details - ${item.label}"
            )
        }

        holder.tvViewScheduleSummary.setOnClickListener {
            onItemClickListener.onClickOfItem(item.label, position)

            firebaseLogEvent(
                context,
                VIEW_DETAILS,
                PreferenceUtils.getLogin().userName,
                PreferenceUtils.getLogin().travels_name,
                PreferenceUtils.getLogin().role,
                VIEW_DETAILS,
                "View Details"
            )
        }
        holder.tvViewScheduleSummary.setOnClickListener {
            onItemClickListener.onClickOfItem(item.label, position)
        }

        holder.tvViewServiceWiseBooking.setOnClickListener {
            onItemClickListener.onClickOfItem(item.label, position)
        }


    }

    private fun setColorByPercentage(textView: TextView, percentage: String?) {
        val value = percentage?.toDouble() ?: 0.0

        if (value <= 30.0) {

            textView.setTextColor(context.resources.getColor(R.color.colorRed2))

        } else if (value in 30.1..50.0) {

            textView.setTextColor(context.resources.getColor(R.color.lightest_yellow))

        } else if (value in 50.1..70.0) {

            textView.setTextColor(context.resources.getColor(R.color.color_03_review_02_moderate))

        } else if (value >= 70.1) {

            textView.setTextColor(context.resources.getColor(R.color.booked_tickets))

        }
    }

    private fun getOccupancyBarChartReportList(position: Int): ArrayList<ReportValue> {

        val item = differ.currentList[position]

        if (item.label.equals("occupancy", true)
            || item.label.equals("revenue", true)
        ) {

            reportList.add(
                ReportValue(
                    getPreviousDate(parentDate),
                    item.yesterday ?: "0",
                    ContextCompat.getColor(context, R.color.booked_tickets)
                )
            )
            reportList.add(
                ReportValue(
                    getDateDMMM(parentDate),
                    item.today ?: "0",
                    ContextCompat.getColor(context, R.color.cancelled_tickets)
                )
            )
            reportList.add(
                ReportValue(
                    getNextDate(parentDate),
                    item.tomorrow ?: "0",
                    ContextCompat.getColor(context, R.color.colorAvailableLadies)
                )
            )
        } else {
            reportList.add(
                ReportValue(
                    context.getString(R.string.branch), (item.branch ?: "0"),
                    ContextCompat.getColor(context, R.color.booked_tickets)
                )
            )

            reportList.add(
                ReportValue(
                    context.getString(R.string.api),
                    item.api ?: "0",
                    ContextCompat.getColor(context, R.color.cancelled_tickets)
                )
            )

            reportList.add(
                ReportValue(
                    context.getString(R.string.agent), (item.agent ?: "0"),
                    ContextCompat.getColor(context, R.color.colorAvailableLadies)
                )
            )

            reportList.add(
                ReportValue(
                    context.getString(R.string.ebkg),
                    (item.eTicket ?: "0"),
                    ContextCompat.getColor(context, R.color.color_user_red)
                )
            )
        }
        return reportList
    }

    private fun setOccupancyBarChart(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        reportList = getOccupancyBarChartReportList(position)

        initOccupancyBarChart(holder)
        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in reportList.indices) {
            val value = reportList[i]
            try {
                entries.add(BarEntry(i.toDouble().toFloat(), value.value.toDouble().toFloat()))
            } catch (ex: NumberFormatException) {
            }
        }

        val barDataSet = BarDataSet(entries, "")
        val colors: ArrayList<Int> = ArrayList()

        for (i in reportList.indices) {
            val value = reportList[i].color
            colors.add(i, value)
            barDataSet.colors = colors
        }

        if (item.label.equals("revenue", true)) {
            barDataSet.valueFormatter = IntValueFormatter.IntValueFormatCurrency(DecimalFormat(),context)
        } else {
            barDataSet.valueFormatter = IntValueFormatter.IntValueDecimalFormat(DecimalFormat())

        }
//        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.valueTextSize = 10f
        barDataSet.valueTypeface = Typeface.DEFAULT_BOLD
        holder.barChartOccupancy.description.isEnabled = false

        val data = BarData(barDataSet)
        holder.barChartOccupancy.data = data
        data.barWidth = 0.5f
        holder.barChartOccupancy.invalidate()

        reportList.clear()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    fun moveItem(fromPosition: Int, toPosition: Int) {
        //Timber.d("fromPosition $fromPosition toPosition $toPosition")
        val list = differ.currentList.toMutableList()


        val fromItem = list[fromPosition]
        list.removeAt(fromPosition)
        if (toPosition < fromPosition) {
            list.add(toPosition + 1, fromItem)
        } else {
            list.add(toPosition - 1, fromItem)
        }
        differ.submitList(list)
    }

    private fun initOccupancyBarChart(holder: ViewHolder) {
//        hide grid lines
        holder.barChartOccupancy.axisLeft.setDrawGridLines(false)
        holder.barChartOccupancy.axisLeft.spaceBottom = 50f
        holder.barChartOccupancy.axisLeft.maxWidth = 0f
        holder.barChartOccupancy.setTouchEnabled(false)
        holder.barChartOccupancy.setPinchZoom(false)

        holder.barChartOccupancy.axisLeft.axisMinimum = 0.0f

        val xAxis: XAxis = holder.barChartOccupancy.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 9f

//        remove left/right x/y-axis
        holder.barChartOccupancy.axisRight.isEnabled = false
        holder.barChartOccupancy.axisLeft.isEnabled = false

        //remove legend
        holder.barChartOccupancy.legend.isEnabled = false
        //remove description label
        holder.barChartOccupancy.description.isEnabled = false
        //add animation
        holder.barChartOccupancy.animateY(3000)
//        activityChartReportBinding.barChart.setDrawBarShadow(true)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.valueFormatter = MyAxisFormatter()
        var xAxisLabelsList = mutableListOf<String>()
        reportList.forEach {
            xAxisLabelsList.add(it.name)
        }
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabelsList)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
//        xAxis.labelRotationAngle = +30f
    }

    private fun setOccupancyBySeatBarChart(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        reportList = getOccupancyBySeatBarChartReportList(position)

        initOccupancyBarChart(holder)
        //now draw bar chart with dynamic data
        val entries: ArrayList<BarEntry> = ArrayList()

        //you can replace this data object with  your custom object
        for (i in reportList.indices) {
            val value = reportList[i]
            try {
                entries.add(BarEntry(i.toDouble().toFloat(), value.value.toDouble().toFloat()))
            } catch (ex: NumberFormatException) {
            }
        }

        val barDataSet = BarDataSet(entries, "")
        val colors: ArrayList<Int> = ArrayList()

        for (i in reportList.indices) {
            val value = reportList[i].color
            colors.add(i, value)
            barDataSet.colors = colors
        }

        if (item.label.equals("revenue", true)) {
            barDataSet.valueFormatter = IntValueFormatter.IntValueFormatCurrency(
                DecimalFormat(),
                context
            )
        } else {
            barDataSet.valueFormatter = IntValueFormatter.IntValueDecimalFormat(
                DecimalFormat()
            )

        }
//        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.valueTextSize = 10f
        barDataSet.valueTypeface = Typeface.DEFAULT_BOLD
        holder.barChartOccupancy.description.isEnabled = false

        val data = BarData(barDataSet)
        holder.barChartOccupancy.data = data
        data.barWidth = 0.5f
        holder.barChartOccupancy.invalidate()

        reportList.clear()
    }

    private fun getOccupancyBySeatBarChartReportList(position: Int): ArrayList<ReportValue> {

        val item = differ.currentList[position]

        if (item.label.equals("occupancy", true)) {

            val yesterdayBarValue = item.yesterdayBySeats?.substringBefore("/")?.toDouble()?.div(item.yesterdayBySeats.substringAfter("/").toDouble())?.times(100)
            val todayBarValue = item.todayBySeats?.substringBefore("/")?.toDouble()?.div(item.todayBySeats.substringAfter("/").toDouble())?.times(100)
            val tomorrowBarValue = item.tomorrowBySeats?.substringBefore("/")?.toDouble()?.div(item.tomorrowBySeats.substringAfter("/").toDouble())?.times(100)

            Timber.d("Occupancy By Seat Values: Yesterday: $yesterdayBarValue, Today: $todayBarValue, Tomorrow: $tomorrowBarValue")
            reportList.add(
                ReportValue(
                    getPreviousDate(parentDate),
                    "${roundOffDecimal(yesterdayBarValue ?: 0.0) ?: "0"}",
                    ContextCompat.getColor(context, R.color.booked_tickets)
                )
            )
            reportList.add(
                ReportValue(
                    getDateDMMM(parentDate),
                    "${roundOffDecimal(todayBarValue ?: 0.0) ?: "0"}",
                    ContextCompat.getColor(context, R.color.cancelled_tickets)
                )
            )
            reportList.add(
                ReportValue(
                    getNextDate(parentDate),
                    "${roundOffDecimal(tomorrowBarValue ?: 0.0) ?: "0"}",
                    ContextCompat.getColor(context, R.color.colorAvailableLadies)
                )
            )
        }
        return reportList
    }


    class ViewHolder(binding: ChildDashboardFetchBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvOccupancyHeader = binding.tvOccupancyHeader
        val tvLastUpdateOccupancy = binding.tvLastUpdateOccupancy
        val tvTotalOccupancy = binding.tvTotalOccupancy
        val barChartOccupancy = binding.barChartOccupancy
        val cardViewOccupancy = binding.cardViewOccupancy
        val cardViewServiceWise = binding.cardViewServiceWise
        val cardViewSchedulesSummary = binding.cardViewSchedulesSummary
        val rvSeatWiseBooking = binding.rvSeatWiseBooking
        val tvYesterdayValue = binding.tvYesterdayValue
        val tvTodayValue = binding.tvTodayValue
        val tvTomorrowValue = binding.tvTomorrowValue
        val imgPinOccupancy = binding.imgPinOccupancy
        val imageArrowOccupancy = binding.imageArrowOccupancy
        val tvTodaySumOccupancy = binding.tvTodaySumOccupancy
        val tvScheduleSummary = binding.tvScheduleSummary
        val tvViewDetails = binding.tvViewDetails
        val tvViewScheduleSummary = binding.tvViewScheduleSummary
        val tvViewServiceWiseBooking = binding.tvViewServiceWiseBooking
        val yesterdayTV = binding.tvYesterdayHeader
        val todayTV = binding.tvTodayHeader
        val tomorrowTV = binding.tvTomorrowHeader
        val activeCancelTV = binding.activeCancelTV
        //val tvTodayOccupancy = binding.tvTodayOccupancy


    }

//    inner class MyAxisFormatter : IndexAxisValueFormatter() {
//
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            val index = value.toInt()
////            Timber.d(TAG, "getAxisLabel: index $index")
//            return if (index < reportList.size) ({
//                reportList[index].name//
//                //"2"
//            }).toString() else {
//                ""
//            }
//        }
//    }

    private fun changeTextColor(textView: TextView, percent: String?) {


        val floatPercent = percent?.replace(",", "")?.toDouble() ?: 0.0

        if (floatPercent <= 30.0) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))

        } else if (floatPercent in 30.1..50.0) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.lightest_yellow))
        } else if (floatPercent in 50.1..70.0) {
            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_03_review_02_moderate
                )
            )
        } else if (floatPercent >= 70.1) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.booked_tickets))
        }
    }

}