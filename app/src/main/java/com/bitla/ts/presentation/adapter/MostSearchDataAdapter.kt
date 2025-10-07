package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ItemSearchDataListBinding
import com.bitla.ts.domain.pojo.dashboard_model.response.MostSearched
import gone
import visible

class MostSearchDataAdapter(
    private val context: Context,
    private var mostSearchedList: MutableList<MostSearched>,
    private var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<MostSearchDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemSearchDataListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mostSearchedList.size
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mostSearchedData: MostSearched = mostSearchedList[position]
        val originName = mostSearchedData.originName
        val destName = mostSearchedData.destName

        holder.tvUserTitle.text = "$originName - $destName"

        if (mostSearchedList.size.minus(1) == position)
            holder.viewUnderline.gone()
        else
            holder.viewUnderline.visible()

        holder.mostSearchContainer.tag = context.getString(R.string.most_searched)
        holder.mostSearchContainer.setOnClickListener {
            onItemClickListener.onClick(holder.mostSearchContainer, position)
        }
    }

    class ViewHolder(binding: ItemSearchDataListBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvUserTitle = binding.tvUserTitle
        val viewUnderline = binding.viewUnderline
        val mostSearchContainer = binding.mostSearchContainer
    }
}