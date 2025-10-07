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
import com.bitla.ts.domain.pojo.privilege_details_model.response.child_model.WalletPaymentOption

class EasebuzzOptionAdapter (
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var easebuzzOptionList: List<WalletPaymentOption>,
    lastSelectedPositionPayment: Int,

    ) :
    RecyclerView.Adapter<EasebuzzOptionAdapter.ViewHolder>() {

    companion object {
        var TAG: String = EasebuzzOptionAdapter::class.java.simpleName
    }


    private var lastSelectedPosition: Int = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ChildPaymentOptionsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return easebuzzOptionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val easebuzzPaymentOption: WalletPaymentOption = easebuzzOptionList.get(position)
        holder.radioPayOptions.text = easebuzzPaymentOption.name
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