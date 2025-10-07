package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.BulkListLeftAdapterBinding

class BulkListleftAdapter(
    private val context: Context,
    private var oldList: ArrayList<String>,
    private var newList: ArrayList<String>,
    private val onItemClickListener: OnItemClickListener

) :
    RecyclerView.Adapter<BulkListleftAdapter.ViewHolder>() {
    //    private var tag: String = ChildStageAdapterBinding::class.java.simpleName
//    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            BulkListLeftAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val old: String = oldList.get(position)
        val new: String = newList.get(position)
        holder.oldSeatNumber.text = old
        holder.newSeatNumber.text = new

    }

    class ViewHolder(binding: BulkListLeftAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val oldSeatNumber = binding.oldSeatnumber
        val newSeatNumber = binding.newSeatnumber
    }
}