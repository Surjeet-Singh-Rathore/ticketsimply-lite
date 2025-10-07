package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.AdapterManageAccountBinding
import com.bitla.ts.domain.pojo.manage_account_view.manage_transaction_search.response.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.constants.STATUS
import visible

class ManageTransactionSearchAdapter(
    private val context: Context?,
    private var manageAccountList: MutableList<Result>,
    private val onItemPassData: OnItemPassData,
    val privileges: PrivilegeResponseModel?

) :
    RecyclerView.Adapter<ManageTransactionSearchAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterManageAccountBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return manageAccountList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val manageAccountModel: Result = manageAccountList[position]

        val transactionNoSpannable = SpannableString(manageAccountModel.transactionNo)
        transactionNoSpannable.setSpan(UnderlineSpan(), 0, transactionNoSpannable.length, 0)

        var currency = ""
        var currencyFormat = ""

        if (privileges != null) {


            currency = if (!privileges.currency.isNullOrEmpty())
                privileges.currency.toString()
            else
                context?.getString(R.string.rupeeSybbol).toString()

            currencyFormat = (privileges.currencyFormat ?: context?.getString(R.string.indian_currency_format)).toString()
        }

        holder.apply {
           agentNameTV.text = manageAccountModel.agentName
            if (manageAccountModel.type.equals("recharge",true)){
                transNoValueTV.text = transactionNoSpannable

                transNoValueTV.setOnClickListener {
                    onItemPassData.onItemData(
                        holder.transNoValueTV,
                        manageAccountModel.transactionNo,
                        ""
                    )
                }
            } else {
                transNoValueTV.text = manageAccountModel.transactionNo
            }
           typeValueTV.text = manageAccountModel.transactionType
           paymentTypeValueTV.text = manageAccountModel.paymentType
           chequeNoValueTV.text = manageAccountModel.ddChequeNo
           updatedOnValueTV.text = manageAccountModel.updatedOn
           updatedByValueTV.text = manageAccountModel.updatedBy
           createdOnValueTV.text = manageAccountModel.createdOn

            try {
                if (manageAccountModel.balance == null) {
                    balance.text = "$currency 0"
                } else {
                    val transBalance = manageAccountModel.balance.convert(currencyFormat)
                    balance.text = "$currency $transBalance"
                }

                if (manageAccountModel.amount == null) {
                    amount.text = "$currency 0"
                } else {
                    val transAmount = manageAccountModel.amount.convert(currencyFormat)
                    amount.text = "$currency $transAmount"
                }
            } catch (e: Exception) {

                if (manageAccountModel.balance == null) {
                    balance.text = "$currency 0"
                } else {
                    balance.text = "$currency ${manageAccountModel.balance}"
                }

                if (manageAccountModel.amount == null) {
                    amount.text = "$currency 0"
                } else {
                    amount.text = "$currency ${manageAccountModel.amount}"
                }
            }

            if (manageAccountModel.status.equals(context!!.getString(R.string.completed), true)) {
                statusValueTV.setTextColor(ContextCompat.getColor(context, R.color.booked_tickets))
                statusValueTV.text = manageAccountModel.status
            } else if (manageAccountModel.status.equals(context.getString(R.string.failed), true)) {
                statusValueTV.setTextColor(ContextCompat.getColor(context, R.color.light_red))
                statusValueTV.text = manageAccountModel.status
            } else {
                val spannableEdit = SpannableString(context.getString(R.string.change_to_completed))
                spannableEdit.setSpan( UnderlineSpan(),
                    0, // start
                    context.getString(R.string.change_to_completed).length, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                statusValueTV.text = spannableEdit

                statusValueLight.visible()
                statusValueLight.text = manageAccountModel.status

                statusValueTV.setOnClickListener {
                    holder.statusValueTV.tag = STATUS
                    onItemPassData.onItemData(
                        holder.statusValueTV,
                        manageAccountModel.transactionNo,
                        manageAccountModel.status
                    )
                }
            }
        }
    }

    class ViewHolder(binding: AdapterManageAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        val mainLayout = binding.mainLayout
        val agentNameTV = binding.agentNameValueTV
        val transNoValueTV = binding.transNoValueTV
        val typeValueTV = binding.typeValueTV
        val paymentTypeValueTV = binding.paymentTypeValueTV
        val chequeNoValueTV = binding.chequeNoValueTV
        val updatedOnValueTV = binding.updatedOnValueTV
        val updatedByValueTV = binding.updatedByValueTV
        val createdOnValueTV = binding.createdOnValueTV
        val amount = binding.tvAmountValue
        val balance = binding.tvBalanceValue
        val statusValueTV = binding.statusValueTV
        val statusValueLight = binding.statusValueLightTv
    }
}