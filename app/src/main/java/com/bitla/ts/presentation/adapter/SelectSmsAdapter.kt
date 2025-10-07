package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSelectSmsBinding
import com.bitla.ts.domain.pojo.sms_types.SmsTemplate
import gone
import visible


class SelectSmsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var smsTemplatesList: List<SmsTemplate>,
    private var lastSelectedSms: Int
) :
    RecyclerView.Adapter<SelectSmsAdapter.ViewHolder>() {

    companion object {
        var TAG: String = SelectSmsAdapter::class.java.simpleName
    }

    private var lastSelectedPosition: Int = lastSelectedSms


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChildSelectSmsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return smsTemplatesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val smsTemplate: SmsTemplate = smsTemplatesList[position]

        holder.radioSmsTitle.isChecked = lastSelectedPosition == position
        /* if(position == 0)
             holder.radioSmsTitle.isChecked = true*/
        holder.radioSmsTitle.text = smsTemplate.sms_type
        if (smsTemplate.sms_content.isNotEmpty()) {
            holder.tvSms.visible()
            holder.tvSms.text = smsTemplate.sms_content
        } else
            holder.tvSms.gone()
    }


    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(binding: ChildSelectSmsBinding) : RecyclerView.ViewHolder(binding.root) {
        val radioSmsTitle: RadioButton = binding.radioSmsTitle
        val tvSms = binding.tvSms
        val layoutCard = binding.layoutCard

        init {
            radioSmsTitle.setOnClickListener {
                radioSmsTitle.tag = TAG
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioSmsTitle, lastSelectedPosition)
            }

            layoutCard.setOnClickListener {
                radioSmsTitle.tag = TAG
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioSmsTitle, lastSelectedPosition)
            }

        }
    }
}