package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSelectedSeatsBinding
import com.bitla.ts.domain.pojo.service_details_response.SeatDetail


class SelectedSeatsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var selectedSeatDetails: ArrayList<SeatDetail>,
    private val isCrossIconVisible: Boolean
) :
    RecyclerView.Adapter<SelectedSeatsAdapter.ViewHolder>() {

    companion object {
        var TAG: String = SelectedSeatsAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /* return ViewHolder(
             LayoutInflater.from(context).inflate(
                 R.layout.child_selected_seats,
                 parent,
                 false
             )
         )*/
        val binding = ChildSelectedSeatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return selectedSeatDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seatDetail: SeatDetail = selectedSeatDetails[position]
        holder.title.text = "${seatDetail.number}"

        holder.imageClose.setOnClickListener {
            holder.imageClose.tag = TAG
            onItemClickListener.onClick(holder.imageClose, position)
        }
    }

    class ViewHolder(binding: ChildSelectedSeatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.textTitle
        val imageClose = binding.imageClose
        val container = binding.container
    }


}