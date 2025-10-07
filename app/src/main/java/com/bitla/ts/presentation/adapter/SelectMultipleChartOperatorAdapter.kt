package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemCheckMultipleItemListener
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.destination_pair.SearchModel


open class SelectMultipleChartOperatorAdapter(

    private val context: Context,
    private var chartOperatorListList: MutableList<SearchModel>,
    checkList: SparseBooleanArray,
    private val onItemCheckListener: OnItemCheckMultipleItemListener,
) :

    RecyclerView.Adapter<SelectMultipleChartOperatorAdapter.ViewHolder>() {

    private var mSelectedItemsIds: SparseBooleanArray = checkList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildSelectMultipleSeatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return chartOperatorListList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchModel: SearchModel = chartOperatorListList[position]
        holder.checkSeatNumber.text = searchModel.name
        holder.checkSeatNumber.isChecked = mSelectedItemsIds[position]

        holder.checkSeatNumber.setOnClickListener {
//            onItemClickListener.onClick(holder.checkSeatNumber, position)

//            holder.checkSeatNumber.isChecked = !holder.checkSeatNumber.isChecked
            if (holder.checkSeatNumber.isChecked) {
                onItemCheckListener.onItemCheck(searchModel)
            } else {
                onItemCheckListener.onItemUncheck(searchModel)
            }
            checkCheckBox(position, !mSelectedItemsIds.get(position))
        }
    }

    class ViewHolder(binding: ChildSelectMultipleSeatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val checkSeatNumber = binding.checkSeatNumber
    }

    /**
     * Check the Checkbox if not checked
     */
    @SuppressLint("NotifyDataSetChanged")
    fun checkCheckBox(position: Int, value: Boolean) {
        if (value) {
            mSelectedItemsIds.delete(position)
            mSelectedItemsIds.put(position, true)
        } else {
            mSelectedItemsIds.delete(position)
            notifyDataSetChanged()
        }
    }
}