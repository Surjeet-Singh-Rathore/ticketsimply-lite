package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.data.listener.OnPnrListener
import com.bitla.ts.databinding.ChildPendingTicketsBinding
import com.bitla.ts.domain.pojo.dashboard_model.response.Data
import gone

class DashboardPendingETicketsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onPnrListener: OnPnrListener,
    private var pendingETicketsList: MutableList<Data>
) :
    RecyclerView.Adapter<DashboardPendingETicketsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildPendingTicketsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pendingETicketsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pendingETicketsModel: Data = pendingETicketsList[position]

        val pnrNumber = pendingETicketsModel.pnrNumber
        val ticketDoj = pendingETicketsModel.doj
        val expiryTime = pendingETicketsModel.expiryTime
        val tktCount = pendingETicketsModel.tktCount

        holder.tvPnr.text = context.getString(R.string.pnr) + " " + pnrNumber
        holder.tvDoj.text = context.getString(R.string.doj) + " " + ticketDoj
        holder.tvExpireIn.text = ""
        holder.tvTickets.text = "$tktCount ${context.getString(R.string.seats_s)}"

        holder.tvRelease.tag = context.getString(R.string.e_release_ticket)

        holder.tvRelease.setOnClickListener {
            if (pnrNumber != null) {
                onPnrListener.onPnrSelection(holder.tvRelease.tag.toString(), pnrNumber, ticketDoj)
            }
        }
        holder.tvExpireIn.gone()
    }

    class ViewHolder(binding: ChildPendingTicketsBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvRelease = binding.tvRelease
        val tvPnr = binding.tvPnr
        val tvDoj = binding.tvDoj
        val tvExpireIn = binding.tvExpireIn
        val tvTickets = binding.tvTickets
    }
}