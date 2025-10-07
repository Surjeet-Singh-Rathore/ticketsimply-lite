package com.bitla.ts.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.AdapterManageAccountBinding
import com.bitla.ts.domain.pojo.manage_account_view.show_transaction_list.response.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.utils.common.convert
import com.bitla.ts.utils.constants.STATUS
import gone
import visible

class ManageAccountAdapter(
    private val context: Context?,
    private var manageAccountList: MutableList<Result>,
    private val onItemPassData: OnItemPassData,
    val privileges: PrivilegeResponseModel?

) :
    RecyclerView.Adapter<ManageAccountAdapter.ViewHolder>() {

    private var isBranch = false
    private var branchOrAgentName: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterManageAccountBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return manageAccountList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var currency = ""
        var currencyFormat = ""

        if (privileges!= null) {


            currency = if (!privileges.currency.isNullOrEmpty())
                privileges.currency.toString()
            else
                context?.getString(R.string.rupeeSybbol).toString()

            currencyFormat = (privileges.currencyFormat ?: context?.getString(R.string.indian_currency_format)).toString()
        }


        val manageAccountModel = manageAccountList[position]

        val transactionNoSpannable = SpannableString(manageAccountModel.transactionNo)
        transactionNoSpannable.setSpan(UnderlineSpan(), 0, transactionNoSpannable.length, 0)

        holder.apply {

            if (manageAccountModel.branchName?.isNotEmpty() == true){
                branchOrAgentName = manageAccountModel.branchName.toString()
                isBranch = true
            } else {
                branchOrAgentName =  manageAccountModel.agentName.toString()
                isBranch  = false
            }

            if (isBranch) {
                agentNameTitle.text = context?.getString(R.string.branch_name)
            } else {
                agentNameTitle.text = context?.getString(R.string.agent_name)
            }

            agentNameTV.text = branchOrAgentName

            if (manageAccountModel.type.equals("recharge", true)) {
                transNoValueTV.text = transactionNoSpannable
            } else {
                transNoValueTV.text = manageAccountModel.transactionNo
            }

            transNoValueTV.setOnClickListener {
                if (manageAccountModel.type.equals("recharge", true)) {
                    onItemPassData.onItemData(
                        view = holder.transNoValueTV,
                        str1 = manageAccountModel.transactionNo,
                        str2 = ""
                    )
                } else {
                    transNoValueTV.text = manageAccountModel.transactionNo
                }
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

            statusValueTV.text = manageAccountModel.status

            if (manageAccountModel.status.equals(context?.getString(R.string.completed), true)) {
                statusValueTV.setTextColor(ContextCompat.getColor(context!!, R.color.booked_tickets))
                statusValueLight.gone()
            }
            else if (manageAccountModel.status.equals(context?.getString(R.string.failed), true)) {
                statusValueTV.setTextColor(ContextCompat.getColor(context!!, R.color.light_red))
                statusValueLight.gone()
            }
            else if (manageAccountModel.status.equals(context?.getString(R.string.pending), true)
                || manageAccountModel.status.equals(context?.getString(R.string.under_clearance), true)) {

                statusValueLight.setTextColor(ContextCompat.getColor(context!!, R.color.yellow_color))
                statusValueLight.visible()
                statusValueLight.text = manageAccountModel.status

                val spannableEdit = SpannableString(context.getString(R.string.change_to_completed))
                spannableEdit.setSpan(
                    /* what = */ UnderlineSpan(),
                    /* start = */ 0, // start
                    /* end = */ context.getString(R.string.change_to_completed).length, // end
                    /* flags = */ Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                statusValueTV.text = spannableEdit
                statusValueTV.setTextColor(ContextCompat.getColor(context, R.color.link_blue))


            } else {
                statusValueTV.setTextColor(ContextCompat.getColor(context!!, R.color.link_blue))
                val spannableEdit = SpannableString(context.getString(R.string.change_to_completed))
                spannableEdit.setSpan(
                    /* what = */ UnderlineSpan(),
                    /* start = */ 0, // start
                    /* end = */ context.getString(R.string.change_to_completed).length ?: 20, // end
                    /* flags = */ Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                statusValueTV.text = spannableEdit
                statusValueLight.visible()
                statusValueLight.text = manageAccountModel.status
            }

            statusValueTV.setOnClickListener {

                if (statusValueTV.text.toString().equals(context?.getString(R.string.change_to_completed), true)
                ) {
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
        val agentNameTitle = binding.agentNameTV
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