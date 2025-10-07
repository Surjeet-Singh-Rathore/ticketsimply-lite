package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonAnyDataListener
import com.bitla.ts.databinding.AdapterPagenationNumberBinding
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.PagenationData
import java.util.Collections

class PagenationNumberAdapter(
    private val context: Context,
    private var pagenationList: ArrayList<PagenationData>,
    private var listener: DialogButtonAnyDataListener
) :
    RecyclerView.Adapter<PagenationNumberAdapter.ViewHolder>() {
    private var TAG: String = PagenationNumberAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = AdapterPagenationNumberBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pagenationList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.pageNumberTV.text = (position + 1).toString()

        if (pagenationList[position].isSelected) {
            holder.pageNumberTV.background = ContextCompat.getDrawable(context, R.drawable.bg_little_round_blue)
            holder.pageNumberTV.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.pageNumberTV.background = ContextCompat.getDrawable(context, R.drawable.bg_round_color_primary_stroke)
            holder.pageNumberTV.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        holder.pageNumberTV.setOnClickListener {
            listener.onDataSend(1, position)

        }
    }

    class ViewHolder(binding: AdapterPagenationNumberBinding) : RecyclerView.ViewHolder(binding.root) {
        val pageNumberTV = binding.numberTV
    }

    fun changeItemPosition(fromPosition: Int, toPosition: Int) {
        // Implement logic to change the item's position in your data list
        Collections.swap(pagenationList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        listener.onDataSend(1,toPosition)

    }
}