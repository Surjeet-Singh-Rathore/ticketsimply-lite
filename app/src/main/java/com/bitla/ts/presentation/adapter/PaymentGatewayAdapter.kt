package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterPaymentGatewayBinding
import com.bitla.ts.domain.pojo.instant_recharge.PgData
import timber.log.Timber
import toast
import java.util.ArrayList

class PaymentGatewayAdapter (
    private val context: Context,
    private var list: ArrayList<PgData>,
    val anyDataListener: DialogButtonAnyDataListener
) :
    RecyclerView.Adapter<PaymentGatewayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            AdapterPaymentGatewayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list.get(position)

            holder.titleRB.text = data.pgName



        if(list[position].isSelected){
            holder.titleRB.isChecked = true
            holder.titleRB.isSelected = false
        }else{
            holder.titleRB.isChecked = false
            holder.titleRB.isSelected = false

        }

        holder.titleRB.setOnClickListener {
            holder.titleRB.isChecked = true
            anyDataListener.onDataSend(1,position)

        }

    }

    class ViewHolder(binding: AdapterPaymentGatewayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val titleRB = binding.titleRB

    }

    fun updateChecks(updateList: ArrayList<PgData>){
        list = updateList
        notifyDataSetChanged()


    }

}

