package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSeatTypesBinding
import com.bitla.ts.domain.pojo.seat_types.SeatTypes
import firstLetterWord
import onChange
import setMaxValueWithDecimal

class SeatTypesAdapter(
    private val context: Context,
    private val seatTypesList: MutableList<SeatTypes>,
    private var amountType: Int,
    private val onAmountChange: (position: Int, amount: String) -> Unit
) : RecyclerView.Adapter<SeatTypesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterSeatTypesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return seatTypesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        val item = seatTypesList[position]

        holder.amountTIL.hint = when (item.seatType) {
            "Sleeper" -> "SL"
            "Seater" -> "ST"
            else -> firstLetterWord(item.seatType.toString())
        }

        holder.etAmount.setText(item.amount)
        if (amountType == 1) {
            holder.etAmount.setMaxValueWithDecimal(100.00, 2)
        } else {
            holder.etAmount.setMaxValueWithDecimal(999999.99, 2)
        }

        holder.etAmount.onChange {
            if (amountType == 1) {
                holder.etAmount.setMaxValueWithDecimal(100.00, 2)
            } else {
                holder.etAmount.setMaxValueWithDecimal(999999.99, 2)
            }

            onAmountChange.invoke(position, it)
        }
    }


    class ViewHolder(binding: AdapterSeatTypesBinding) : RecyclerView.ViewHolder(binding.root) {
        val amountTIL = binding.amountTIL
        val etAmount = binding.etAmount
    }


    fun updateAmountType(type: Int) {
        amountType = type
        notifyDataSetChanged()
    }
}