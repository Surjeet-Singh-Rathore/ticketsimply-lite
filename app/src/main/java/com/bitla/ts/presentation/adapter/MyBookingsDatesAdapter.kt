package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildNextDatesBinding
import com.bitla.ts.domain.pojo.booking.StageData
import com.bitla.ts.utils.common.getCurrentYear
import com.bitla.ts.utils.common.getDateDMY
import com.bitla.ts.utils.common.getTodayDate
import com.bitla.ts.utils.common.inputFormatToOutput
import com.bitla.ts.utils.constants.DATE_FORMAT_MMM_DD_EEE_YYYY
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import visible
import java.util.Locale


class MyBookingsDatesAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var menuList: List<StageData>,
    private var isShowCalendar: Boolean,
    private var selectedDate: String? = null,
) : RecyclerView.Adapter<MyBookingsDatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildNextDatesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: StageData = menuList[position]
        if (menu.title != null) {
            val date = menu.title.split(" ")
            if (PreferenceUtils.getlang() == "vi") {
                holder.tvMonth.text = "${date[0].uppercase(Locale.getDefault())}-${date[1]}"
                holder.tvDate.text = date[2]
                if (date[3].equals("CN", true)) {
                    holder.tvDay.text = date[3].uppercase(Locale.getDefault())
                } else {
                    holder.tvDay.text = "${date[3].uppercase(Locale.getDefault())}-${date[4]}"
                }
            } else {
                holder.tvMonth.text = date[0].uppercase(Locale.getDefault())
                holder.tvDate.text = date[1]
                holder.tvDay.text = date[2].uppercase(Locale.getDefault())
            }
        }

        if (position == 0) {
            if (isShowCalendar){
                holder.layoutCalender.visible()
            } else {
                holder.layoutCalender.gone()
            }

            val ymdDate = inputFormatToOutput(
                menu.title,
                DATE_FORMAT_MMM_DD_EEE_YYYY,
                DATE_FORMAT_Y_M_D
            ).replace("1970", getCurrentYear())
            val dmyDate = getDateDMY(ymdDate)!!

            if (menu.isSelected && dmyDate == getTodayDate()) {
                holder.layoutToday.gone()
                holder.container.visible()
            } else {
                holder.layoutToday.gone()
                holder.container.visible()
            }
        }
        else {
            holder.layoutCalender.gone()
            holder.container.visible()
            holder.layoutToday.gone()
        }

        holder.apply {

            if (!isShowCalendar){
                val ymdDate = inputFormatToOutput(
                    menu.title,
                    DATE_FORMAT_MMM_DD_EEE_YYYY,
                    DATE_FORMAT_Y_M_D
                ).replace("1970", getCurrentYear())
                val dmyDate = getDateDMY(ymdDate)!!

                if (selectedDate.toString().isNotEmpty()){
                    if (dmyDate == selectedDate) {
                        setColorPrimary(context)
                    } else {
                        setColorBlackShadow(context)
                    }
                } else {
                    if (menu.isSelected) {
                        setColorPrimary(context)
                    } else {
                        setColorBlackShadow(context)
                    }
                }
            } else{
                if (menu.isSelected) {
                    setColorPrimary(context)
                } else {
                    setColorBlackShadow(context)
                }
            }
        }

        holder.container.setOnClickListener {
            if (menuList[position].layoutType == "DATES" || menuList[position].layoutType == "BOOKING") {
                holder.container.tag = menuList[position].layoutType
                onItemClickListener.onClick(
                    holder.container,
                    position
                )

            }
            for (i in 0..menuList.size.minus(1)) {
                menuList[i].isSelected = false
            }
            menuList[position].isSelected = true
            notifyDataSetChanged()
        }



        holder.layoutCalender.setOnClickListener {
            holder.layoutCalender.tag = context.getString(R.string.open_calender)
            onItemClickListener.onClick(
                holder.layoutCalender,
                position
            )
        }
    }

    class ViewHolder(binding: ChildNextDatesBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDate = binding.tvDate
        val tvMonth = binding.tvMonth
        val tvDay = binding.tvDay
        val layoutToday = binding.layoutToday
        val layoutCalender = binding.layoutCalender
        val container = binding.container

        fun setColorPrimary(context: Context){
            container.background = context.resources.getDrawable(R.drawable.layout_rounded_shape_border_primary_light_color_2dp)
            tvDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvMonth.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        fun setColorBlackShadow(context: Context){
            container.background = context.resources.getDrawable(R.drawable.layout_rounded_shape)
            tvDate.setTextColor(ContextCompat.getColor(context, R.color.colorBlackShadow))
            tvMonth.setTextColor(ContextCompat.getColor(context, R.color.colorBlackShadow))
            tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorBlackShadow))
        }
    }
}