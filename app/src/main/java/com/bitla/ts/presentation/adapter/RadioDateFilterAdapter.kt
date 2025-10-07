package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildRadioDateFilterBinding
import com.bitla.ts.domain.pojo.DateFilterRadioItem
import com.bitla.ts.utils.common.*
import com.bitla.ts.utils.constants.DATE_FORMAT_D_M_YY
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D

class RadioDateFilterAdapter(
    private val context: Context,
    private var list: MutableList<DateFilterRadioItem>,
    private val defaultSelection: Int,
    private val todayDate: String,
    private val fromDate: String?,
    private val toDate: String?,
    private val isBeforeFromDateSelection: Boolean,
    private val isAfterFromDateSelection: Boolean,
    private val isAfterToDateSelection: Boolean,
    private val hideCustomDateFilter: Boolean,
    private val hideCustomDateRangeFilter: Boolean,
    private var isCustomDateFilterSelected: Boolean,
    private var isCustomDateRangeFilterSelected: Boolean,
    private val fragmentManager: FragmentManager,
    private val tag: String,
    private val onDatesSelected: ((newSelectedFromDate: String?, newSelectedToDate: String?, lastSelectedItem: Int, isCustomDateFilter: Boolean, isCustomDateRangeFilter: Boolean) -> Unit)
) :
    RecyclerView.Adapter<RadioDateFilterAdapter.ViewHolder>() {
    private var lastSelectedPosition: Int = defaultSelection
    private var lastSelectedId: Int = defaultSelection
    private var newFromDate: String? = null
    private var newToDate: String? = null
    //private var boardingDroppingFiltered = boardingDropping

    companion object {
        var tag: String? = RadioDateFilterAdapter::class.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildRadioDateFilterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        //holder.radioItem.isChecked = lastSelectedPosition == position

        val item = list[position]
        holder.radioHeader.text = item.headerValue
        holder.radioSubHeader.text = item.subHeaderValue

        if (item.id == lastSelectedPosition) {
            holder.radioBtn.isChecked = true
            holder.radioSubHeader.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )

            onDatesSelected.invoke(
                item.fromDate,
                item.toDate,
                lastSelectedPosition,
                isCustomDateFilterSelected,
                isCustomDateRangeFilterSelected
            )

        } else {
            holder.radioBtn.isChecked = false
            holder.radioSubHeader.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        holder.layoutRadioItem.setOnClickListener {

            when (item.id) {

                5 -> {

                    setSingleDateCalendar(
                        context = context,
                        todayDate = item.todayDate,
                        fromDate = item.fromDate,
                        isBeforeFromDateSelection = isBeforeFromDateSelection,
                        isAfterFromDateSelection = isAfterFromDateSelection,
                        fragmentManager = fragmentManager,
                        tag = tag,
                        onDatesSelected = { fromDate ->
                            //context.toast(fromDate)
                            it.tag = tag
                            lastSelectedPosition = item.id
                            lastSelectedId = item.id
                            item.subHeaderValue = getCurrentFormattedDate(
                                fromDate,
                                DATE_FORMAT_Y_M_D,
                                DATE_FORMAT_D_M_YY
                            )
                            holder.radioSubHeader.text = item.subHeaderValue
                            item.fromDate = fromDate
                            item.toDate = fromDate
                            isCustomDateFilterSelected = true
                            isCustomDateRangeFilterSelected = false
                            onDatesSelected.invoke(
                                item.fromDate,
                                null,
                                lastSelectedPosition,
                                isCustomDateFilterSelected,
                                isCustomDateRangeFilterSelected
                            )

                            notifyDataSetChanged()
                        },
                        onCancel = {
                            //context.toast("Cancelled")
                        }

                    )

                }

                6 -> {
                    setDoubleDateCalendar(
                        context = context,
                        todayDate = item.todayDate,
                        fromDate = item.fromDate,
                        toDate = item.toDate,
                        isBeforeFromDateSelection = isBeforeFromDateSelection,
                        isAfterToDateSelection = isAfterToDateSelection,
                        fragmentManager = fragmentManager,
                        tag = "",
                        onDatesSelected = { fromDate, toDate ->

                            //context.toast("${fromDate} ${toDate}")
                            it.tag = tag
                            lastSelectedPosition = item.id
                            lastSelectedId = item.id
                            item.subHeaderValue = "${
                                getCurrentFormattedDate(
                                    fromDate,
                                    DATE_FORMAT_Y_M_D, DATE_FORMAT_D_M_YY
                                )
                            } - ${
                                getCurrentFormattedDate(
                                    toDate,
                                    DATE_FORMAT_Y_M_D,
                                    DATE_FORMAT_D_M_YY
                                )
                            }"
                            holder.radioSubHeader.text = item.subHeaderValue
                            item.fromDate = fromDate
                            item.toDate = toDate
                            isCustomDateFilterSelected = false
                            isCustomDateRangeFilterSelected = true

                            onDatesSelected.invoke(
                                item.fromDate,
                                item.toDate,
                                lastSelectedPosition,
                                isCustomDateFilterSelected,
                                isCustomDateRangeFilterSelected
                            )


                            notifyDataSetChanged()
                        },
                        onCancel = {
                            //context.toast("Cancelled")
                        }

                    )
                }

                else -> {
                    it.tag = tag
                    lastSelectedPosition = item.id
                    lastSelectedId = item.id
                    isCustomDateFilterSelected = false
                    isCustomDateRangeFilterSelected = false
                    onDatesSelected.invoke(
                        item.fromDate,
                        item.toDate,
                        lastSelectedPosition,
                        isCustomDateFilterSelected,
                        isCustomDateRangeFilterSelected
                    )
                    notifyDataSetChanged()
                    //onItemClickListener.onClick(it, stageDetail.id.toInt())

                }

            }

        }
        //}

    }

    inner class ViewHolder(binding: ChildRadioDateFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val layoutRadioItem = binding.layoutRadioItem
        val radioBtn = binding.radioBtn
        val radioHeader = binding.radioHeader
        val radioSubHeader = binding.radioSubHeader

    }
}