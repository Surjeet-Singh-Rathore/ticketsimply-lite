package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildPaymentOptionsBinding
import com.bitla.ts.domain.pojo.booking.PayGayType


class PaymentOptionsAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var paymentOptionList: List<PayGayType>
) :
    RecyclerView.Adapter<PaymentOptionsAdapter.ViewHolder>() {

    companion object {
        var TAG: String = PaymentOptionsAdapter::class.java.simpleName
    }


    private var lastSelectedPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ChildPaymentOptionsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return paymentOptionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentOptions: PayGayType = paymentOptionList.get(position)
        holder.radioPayOptions.text = "${paymentOptions.payGayTypeName}"
        holder.radioPayOptions.isChecked = lastSelectedPosition == position
    }


    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(binding: ChildPaymentOptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val radioPayOptions: RadioButton = binding.radioPayOptions
        private val layoutPayment: LinearLayout = binding.layoutPayment

        init {
            radioPayOptions.setOnClickListener {
                radioPayOptions.tag = TAG
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioPayOptions, lastSelectedPosition)
            }

            layoutPayment.setOnClickListener {
                radioPayOptions.tag = TAG
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()

                onItemClickListener.onClick(radioPayOptions, lastSelectedPosition)
            }
        }
    }
}