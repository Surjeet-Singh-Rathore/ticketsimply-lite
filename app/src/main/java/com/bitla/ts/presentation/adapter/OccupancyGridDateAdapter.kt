package com.bitla.ts.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildOccupancyGridDateBinding
import com.bitla.ts.domain.pojo.occupancy_datewise.response.DateWiseSummary
import com.bitla.ts.utils.common.getDateMMMDD
import com.bitla.ts.utils.common.getDayPrefixFromDate
import com.bitla.ts.utils.constants.DATE_FORMAT_Y_M_D


class OccupancyGridDateAdapter(
    private var onClick: ((item: DateWiseSummary?) -> Unit)
) : RecyclerView.Adapter<OccupancyGridDateAdapter.ViewHolder>() {
    private var oldList: MutableList<DateWiseSummary?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildOccupancyGridDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = oldList.size

    fun updateList(newList: MutableList<DateWiseSummary?>) {
        oldList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[position]
        if (item?.date?.isNotEmpty() == true) {
            val date = getDateMMMDD(item.date)
            holder.tvDate.text = date

            holder.tvDay.text = getDayPrefixFromDate(item.date, DATE_FORMAT_Y_M_D)
        } else {
            holder.tvDate.text = ""
            holder.tvDay.text = ""
        }
        holder.layoutDate.setOnClickListener {
            onClick.invoke(item)
        }
    }

    class ViewHolder(binding: ChildOccupancyGridDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val layoutDate = binding.layoutDate
        val tvDay = binding.tvDay
        val tvDate = binding.tvDate
    }
}