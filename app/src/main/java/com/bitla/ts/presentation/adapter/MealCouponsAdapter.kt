package com.bitla.ts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.databinding.ChildMealCouponsBinding

class MealCouponsAdapter(
    private val context: Context,
    private var mealCoupons: List<String>
) :
    RecyclerView.Adapter<MealCouponsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildMealCouponsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mealCoupons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mealCoupons: String = mealCoupons[position]
        holder.tvMealCoupons.text = mealCoupons
    }

    class ViewHolder(binding: ChildMealCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvMealCoupons = binding.tvMealCoupons
    }
}