package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildEditPassengersBinding
import com.bitla.ts.domain.pojo.ticket_details.response.PassengerDetail
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import timber.log.Timber
import visible


class EditSinglePassengersAdapter(
    private val context: Context,
    private var menuList: MutableList<PassengerDetail?>?,
    private val onItemClickListener: OnItemClickListener,
) :
    RecyclerView.Adapter<EditSinglePassengersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildEditPassengersBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu: PassengerDetail? = menuList?.get(position)

        holder.tvName.text = menu?.name
        holder.tvSeatNo.text = menu?.seatNumber
        holder.tvMobile.text = menu?.mobile

        val viewPassengerEditVisibility =
            PreferenceUtils.getPreference(
                context.getString(R.string.viewPassengerEditVisibility),
                false
            )

        if (!viewPassengerEditVisibility!!) {
            holder.tvEdit.gone()
        } else {
            holder.tvEdit.visible()
        }

        holder.tvEdit.setOnClickListener {
            Timber.d("adaptertext: ${menu?.seatNumber}")
            onItemClickListener.onClickOfItem(
                "${context.getString(R.string.edit)}&${menu?.seatNumber}",
                position
            )
        }
    }

    class ViewHolder(binding: ChildEditPassengersBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvEdit = binding.tvEdit
        val tvName = binding.tvName
        val tvSeatNo = binding.tvSeatNo
        val tvMobile = binding.tvMobile

    }
}