package com.bitla.ts.presentation.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ChildSearchSelectionBinding
import com.bitla.ts.domain.pojo.destination_pair.SearchModel
import com.bitla.ts.utils.common.setEnabledDisableView
import timber.log.Timber
import toast


class SearchAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private var searchList: List<SearchModel>
) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var TAG: String = SearchAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /* return ViewHolder(
             LayoutInflater.from(context).inflate(
                 R.layout.child_search_selection,
                 parent,
                 false
             )
         )*/

        val binding =
            ChildSearchSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchModel: SearchModel = searchList.get(position)
        holder.tvSearch.text = "${searchModel.name}"

        //Timber.d("isAllowMultistationBlockedServiceAdapter: $isAllowMultistationBlockedService}")


        if (searchModel.isAllowMultistationBlockedService == true){
            holder.apply {
                layoutSearchSelection.setEnabledDisableView(context, false)
                layoutSearchSelection.background = ContextCompat.getDrawable(context, R.drawable.layout_rounded_shape_border_gray)
                layoutSearchSelection.background.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.button_color
                    ), PorterDuff.Mode.MULTIPLY
                )

                mainLayout.setOnClickListener {
                    context.toast("Booking Will be Available from ${searchModel.multistationAllowedTime} for this Route")
                }
            }

        } else {
            holder.layoutSearchSelection.setOnClickListener {
                holder.layoutSearchSelection.tag = "serviceSelection|${searchModel.name}|${searchModel.id}"
                Timber.d("clickCheckOnItemSelected: $position , ${holder.tvSearch.text} ${searchModel.name} ${searchModel.id}")
                onItemClickListener.onClick(holder.layoutSearchSelection, position)
            }
        }


    }

    fun filterList(filteredNames: MutableList<SearchModel>) {
        this.searchList = filteredNames
        notifyDataSetChanged()
    }


    class ViewHolder(binding: ChildSearchSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvSearch = binding.tvSearch
        val layoutSearchSelection = binding.layoutSearchSelection
        val mainLayout = binding.mainLayout
    }
}