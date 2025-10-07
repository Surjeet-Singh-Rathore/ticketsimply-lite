package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.CrewAndVehicleExpenseListItemBinding
import com.bitla.ts.domain.pojo.expenses_details.response.CrewExpense

class CrewExpenseAdapter(
    private val context: Context,
    private val crewExpenseList: List<CrewExpense>,
    private val onTextChangedListener: OnTextChangedListener
) : RecyclerView.Adapter<CrewExpenseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CrewAndVehicleExpenseListItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var expenses = crewExpenseList[position]
        onTextChangedListener.onTextChanged(position, expenses)
        holder.editText.setText(expenses.value)
        holder.textInputLayout.setHint(expenses.label)
        holder.editText.tag = expenses.key
        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                expenses.value = p0.toString()
                onTextChangedListener.onTextChanged(position, expenses)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    override fun getItemCount(): Int {
        return crewExpenseList.size
    }

    inner class ViewHolder(binding: CrewAndVehicleExpenseListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val editText = binding.editText
        val textInputLayout = binding.textInputLayout

    }

    interface OnTextChangedListener {
        fun onTextChanged(position: Int, expenses: CrewExpense)
    }
}