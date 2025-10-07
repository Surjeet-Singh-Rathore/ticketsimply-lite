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
import visible

class DashboardPendingApiTicketsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onPnrListener: OnPnrListener,
    private var pendingApiTicketsList: MutableList<Data>,
    private var isAllowToReleaseApiTentativeBlockedTickets: Boolean
) :
    RecyclerView.Adapter<DashboardPendingApiTicketsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildPendingTicketsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pendingApiTicketsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pendingETicketsModel: Data = pendingApiTicketsList[position]

        if (isAllowToReleaseApiTentativeBlockedTickets) {
            holder.tvRelease.visible()
        } else {
            holder.tvRelease.gone()
        }

        val pnrNumber = pendingETicketsModel.pnrNumber
        val ticketDoj = pendingETicketsModel.doj
        val expiryTime = pendingETicketsModel.expiryTime
        val tktCount = pendingETicketsModel.tktCount

        holder.tvPnr.text = context.getString(R.string.pnr) + " " + pnrNumber
        holder.tvDoj.text = context.getString(R.string.doj) + " " + ticketDoj
        holder.tvExpireIn.text =
            context.getString(R.string.expires_in) + " " + expiryTime + " " + context.getString(R.string.min)
        holder.tvTickets.text = "$tktCount ${context.getString(R.string.seats_s)}"

        holder.tvRelease.tag = context.getString(R.string.api_release_ticket)

        holder.tvRelease.setOnClickListener {
            if (pnrNumber != null) {
                onPnrListener.onPnrSelection(holder.tvRelease.tag.toString(), pnrNumber, ticketDoj)
            }
        }
    }

    class ViewHolder(binding: ChildPendingTicketsBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvRelease = binding.tvRelease
        val tvPnr = binding.tvPnr
        val tvDoj = binding.tvDoj
        val tvExpireIn = binding.tvExpireIn
        val tvTickets = binding.tvTickets
    }
}