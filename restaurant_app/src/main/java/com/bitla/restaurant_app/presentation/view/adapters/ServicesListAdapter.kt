package com.bitla.restaurant_app.presentation.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.databinding.ListItemBinding
import com.bitla.restaurant_app.presentation.pojo.allotedServiceDirect.AllotedDirctResponse.Service
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantList


class ServicesListAdapter(
    val context: Context,
    val setClickedResult: (String) -> Unit,
) : RecyclerView.Adapter<ServicesListAdapter.ViewHolder>() {


    var restaurantListFiltered = mutableListOf<Service>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = restaurantListFiltered[position]
        holder.tvServiceName.text=item.number
        holder.layout.setOnClickListener {
            setClickedResult.invoke("${item.routeId.toString()}@${item.number}")
        }
    }

    override fun getItemCount(): Int {
        return restaurantListFiltered.size
    }



    class ViewHolder(binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layout = binding.layout
        val tvServiceName = binding.tvServiceName
    }

    fun addData(restaurantList: MutableList<Service>) {
        restaurantListFiltered = restaurantList
        notifyDataSetChanged()
    }

}