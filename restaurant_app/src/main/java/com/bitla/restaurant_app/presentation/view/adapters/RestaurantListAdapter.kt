package com.bitla.restaurant_app.presentation.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.restaurant_app.databinding.ListItemBinding
import com.bitla.restaurant_app.presentation.pojo.mealCoupon.RestaurantList


class RestaurantListAdapter(
    val context: Context,
    val onRestaurantClick: (String) -> Unit,
) : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>() {


    var restaurantListFiltered = mutableListOf<RestaurantList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = restaurantListFiltered[position]
        holder.tvServiceName.text=item.restaurantName
        holder.layout.setOnClickListener {
            onRestaurantClick.invoke("${item.id.toString()}@${item.restaurantName}")
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

    fun addData(restaurantList: MutableList<RestaurantList>) {
        restaurantListFiltered = restaurantList
        notifyDataSetChanged()
    }

}