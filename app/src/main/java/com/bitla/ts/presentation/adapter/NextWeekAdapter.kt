package com.bitla.ts.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
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
import gone
import timber.log.Timber
import visible
import java.util.*

class NextWeekAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var menuList: List<StageData>,
) :
    RecyclerView.Adapter<NextWeekAdapter.ViewHolder>() {
    private var TAG: String = NextWeekAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildNextDatesBinding.inflate(LayoutInflater.from(context), parent, false)
        return NextWeekAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: StageData = menuList[position]
        if (menu.title != null) {
            val date = menu.title.split(" ")
            holder.tvMonth.text = date[0].uppercase(Locale.getDefault())
            holder.tvDate.text = date[1]
            holder.tvDay.text = date[2].uppercase(Locale.getDefault())
        }

        if (position == 0) {
            holder.layoutCalender.visible()
//            if(menu.title.split(" ")[0] == getTodayDate().split("-")[0]){

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
        } else {
            holder.layoutCalender.gone()
            holder.container.visible()
            holder.layoutToday.gone()
        }


        if (menu.isSelected) {
            holder.container.background =
                context.resources.getDrawable(R.drawable.layout_rounded_shape)
            holder.container.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.light_green
                )
            )
            holder.tvDate.setTextColor(context.resources.getColor(R.color.deep_green))
            holder.tvMonth.setTextColor(context.resources.getColor(R.color.deep_green))
            holder.tvDay.setTextColor(context.resources.getColor(R.color.deep_green))
        } else {
            holder.container.background =
                context.resources.getDrawable(R.drawable.layout_rounded_shape)
            holder.container.backgroundTintList = ColorStateList.valueOf(
                context.resources.getColor(
                    R.color.white
                )
            )
            holder.tvDate.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
            holder.tvMonth.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
            holder.tvDay.setTextColor(context.resources.getColor(R.color.colorBlackShadow))
        }
        holder.container.setOnClickListener {
            if (menuList[position].layoutType == "DATES" || menuList[position].layoutType == "BOOKING") {
                holder.container.tag = menuList[position].layoutType
//                onItemClickListener.onClickOfItem(
//                    "${holder.tvDate.text}",
//                    position
//                )
                Timber.d("dateCheckkk:$position")

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

        holder.layoutCalender.gone()
    }


    class ViewHolder(binding: ChildNextDatesBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDate = binding.tvDate
        val tvMonth = binding.tvMonth
        val tvDay = binding.tvDay
        val layoutToday = binding.layoutToday
        val layoutCalender = binding.layoutCalender
        val container = binding.container

    }

}