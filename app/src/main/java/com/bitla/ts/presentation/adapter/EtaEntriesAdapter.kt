package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R.color
import com.bitla.ts.databinding.ChildEtaCardBinding
import com.bitla.ts.domain.pojo.eta.EtaDetail

class EtaEntriesAdapter(
    private val context: Context,
    private var etaList: MutableList<EtaDetail>
) :
    RecyclerView.Adapter<EtaEntriesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildEtaCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return etaList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val etaDetail = etaList[position]
        holder.name.text = etaList[position].pickup_name
        holder.status.text = etaList[position].status
        if (etaList[position].status == "Delay") {
            context?.resources?.getColor(color.delay_status)?.let { holder.status.setTextColor(it) }
        } else {
            context?.resources?.getColor(color.booked_tickets)
                ?.let { holder.status.setTextColor(it) }
        }
        holder.time.text = etaList[position].time
        holder.deviation.text = etaList[position].variation.toString() + "min"
    }

    class ViewHolder(binding: ChildEtaCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.stageName
        val status = binding.status
        val time = binding.time
        val deviation = binding.deviation
    }
}