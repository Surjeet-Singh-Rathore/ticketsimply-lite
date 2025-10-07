package com.bitla.ts.presentation.adapter.sellfAuditFormAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.AdapterSeflAuditStageBinding
import com.bitla.ts.domain.pojo.self_audit_question.response.BoardingPoint
import com.bitla.ts.domain.pojo.self_audit_question.response.Option

class SelfAuditStageAdapter(
    private val context: Context,
    private val boardingPointsData: List<BoardingPoint>,
    private val options: List<Option>,
    private val onItemClick:((optionId: String, stageId: String)->Unit)

) : RecyclerView.Adapter<SelfAuditStageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterSeflAuditStageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return boardingPointsData.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = boardingPointsData[position]

        holder.stageNameTV.text = currentItem.title

        options.forEachIndexed { index, option ->
            val radioButton = RadioButton(context)
            radioButton.id = option.id.toInt() // Unique ID for each RadioButton
            radioButton.text = option.title
            radioButton.gravity= Gravity.TOP
            radioButton.setPadding(0,4,0,4)

            radioButton.setOnClickListener {
                // Notify the activity or fragment about the selected option
                onItemClick(option.id, currentItem.id)
            }

            holder.optionRecyclerView.addView(radioButton)
        }


    }

    class ViewHolder(binding: AdapterSeflAuditStageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val stageNameTV = binding.stageNameTV
        val optionRecyclerView = binding.radio
    }
}
