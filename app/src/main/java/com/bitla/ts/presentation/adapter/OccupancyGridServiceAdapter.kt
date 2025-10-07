package com.bitla.ts.presentation.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildOccupancyGridServiceBinding
import com.bitla.ts.domain.pojo.occupancy_datewise.response.Service


class OccupancyGridServiceAdapter(
    private var onClick: ((item: Service?) -> Unit)
) : RecyclerView.Adapter<OccupancyGridServiceAdapter.ViewHolder>() {
    private var oldList: MutableList<Service?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildOccupancyGridServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = oldList.size

    fun updateList(newList: MutableList<Service?>) {
        oldList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[position]

        holder.tvServiceName.text = item?.name.toString()
        holder.tvServiceName.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        holder.llServiceDetailsLayout.setOnClickListener {
            onClick.invoke(item)
        }

        holder.tvCoachType.text = item?.coachType ?: ""
        holder.tvSeats.text = "${item?.totalSeats ?: 0}"
        holder.tvDepTime.text = item?.deptTime ?: ""

    }

    class ViewHolder(binding: ChildOccupancyGridServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val llServiceDetailsLayout = binding.llServiceDetailsLayout
        val tvServiceName = binding.tvServiceName
        val tvCoachType = binding.tvCoachType
        val tvSeats = binding.tvSeats
        val tvDepTime = binding.tvDepTime
    }
}