package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.CrewAndVehicleExpenseListItemNewFlowBinding
import com.bitla.ts.domain.pojo.expenses_details.response.CrewExpense
import onChange

class CrewExpenseNewFlowAdapter(
    private val context: Context,
    private val crewExpenseList: List<CrewExpense>,
    private val onTextChangedListener: OnTextChangedListener,
) : RecyclerView.Adapter<CrewExpenseNewFlowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CrewAndVehicleExpenseListItemNewFlowBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return crewExpenseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expenses = crewExpenseList[position]

        holder.textInputLayout.hint = expenses.label
        holder.editText.setText(expenses.value)

        holder.remarksTIL.hint = expenses.label + " " + context.getString(R.string.remarks)
        holder.etRemarks.setText(expenses.remarks)

        holder.editText.onChange {
            expenses.value = it
            onTextChangedListener.onTextChanged(holder.bindingAdapterPosition, expenses)
        }

        holder.etRemarks.onChange {
            expenses.remarks = it
            onTextChangedListener.onTextChanged(holder.bindingAdapterPosition, expenses)
        }

        holder.tvShowHideRemarks.setOnClickListener {
            holder.remarksTIL.isVisible = !holder.remarksTIL.isVisible
        }
    }


    inner class ViewHolder(binding: CrewAndVehicleExpenseListItemNewFlowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val editText = binding.editText
        val textInputLayout = binding.textInputLayout
        val tvShowHideRemarks = binding.tvShowHideRemarks
        val remarksTIL = binding.remarksTIL
        val etRemarks = binding.etRemarks
    }


    interface OnTextChangedListener {
        fun onTextChanged(position: Int, expenses: CrewExpense)
    }
}