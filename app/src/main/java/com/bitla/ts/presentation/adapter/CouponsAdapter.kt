package com.bitla.ts.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.databinding.ChildBookingAmountBinding
import com.bitla.ts.domain.pojo.custom_applied_coupons.AppliedCoupon
import com.bitla.ts.utils.common.convert

class CouponsAdapter(
    private val context: Context,
    private var appliedCouponList: MutableList<AppliedCoupon>,
    private val currencyFormat: String
) :
    RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ChildBookingAmountBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return appliedCouponList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appliedCoupon: AppliedCoupon = appliedCouponList[position]

        holder.tvLabel.text = appliedCoupon.coupon_type

        if (appliedCoupon.coupon_type == context.getString(R.string.vip_ticket))
            holder.tvValue.text = appliedCoupon.coupon_code?.substringAfter(":")
        else {
            val isDigitOnly = android.text.TextUtils.isDigitsOnly(appliedCoupon.coupon_code)
            if (isDigitOnly && appliedCoupon.coupon_code.isNotEmpty()) {
                holder.tvValue.text =
                    appliedCoupon.coupon_code.toDouble().convert(currencyFormat = currencyFormat)
            } else
                holder.tvValue.text = appliedCoupon.coupon_code
        }

    }

    class ViewHolder(binding: ChildBookingAmountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvLabel = binding.tvLabel
        val tvValue = binding.tvValue
    }
}