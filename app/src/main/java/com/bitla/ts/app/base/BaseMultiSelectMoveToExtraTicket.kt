package com.bitla.ts.app.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bitla.ts.R
import com.bitla.ts.data.listener.DialogButtonMoveSeatExtraListener
import com.bitla.ts.data.listener.MoveToExtraOnItemClickListener
import com.bitla.ts.databinding.SheetTicketMoveToExtraMultiSeatBinding
import com.bitla.ts.presentation.adapter.SelectMultiSeatAdapterForMultiSelection
import com.bitla.ts.utils.showToast
import gone
import timber.log.Timber
import visible

open class BaseMultiSelectMoveToExtraTicket : BaseFragment(), MoveToExtraOnItemClickListener {

    private lateinit var dialogBinding: SheetTicketMoveToExtraMultiSeatBinding
    private lateinit var selectMultiSeatAdapterForMultiSelection: SelectMultiSeatAdapterForMultiSelection
    private var neededSeatNumbers: MutableList<String> = mutableListOf()
    private var isSeatClick = 0
    private val selectedSeatNumber = StringBuilder()
    private val currentCheckedItem = mutableListOf<String?>()

    fun showMultiExtraSeatSelectionDialog(
        context: Context,
        title: String,
        message: String,
        buttonLeftText: String,
        buttonRightText: String,
        seatNumber: String,
        extraSeatNumber: String,
        isEditable: Boolean,
        neededSeatNumbers: MutableList<String> = mutableListOf(),
        dialogButtonMoveSeatExtraListener: DialogButtonMoveSeatExtraListener
    ) {
        currentCheckedItem.clear()
        selectedSeatNumber.clear()
        this.neededSeatNumbers = neededSeatNumbers
        val builder = AlertDialog.Builder(context).create()
        val inflater = LayoutInflater.from(context)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding = SheetTicketMoveToExtraMultiSeatBinding.inflate(inflater)
        builder.setView(dialogBinding.root)
        builder.setCancelable(false)

        val recyclerViewLayoutParams = dialogBinding.rvSelectMultipleSeats.layoutParams as ConstraintLayout.LayoutParams

        if (neededSeatNumbers.size > 4) {
            val fixedHeightInDp = 200
            val scale = context.resources.displayMetrics.density
            recyclerViewLayoutParams.height = (fixedHeightInDp * scale).toInt() // Convert dp to pixels
        } else {
            recyclerViewLayoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        }

        dialogBinding.rvSelectMultipleSeats.layoutParams = recyclerViewLayoutParams

        dialogBinding.apply {
            tvTitle.text = title
            tvContent.text = message
            btnLeft.text = buttonLeftText
            btnRight.text = buttonRightText
            etSeats.setText("")

            etSeats.setOnClickListener {
                if (isSeatClick == 0) {
                    isSeatClick++
                    rvSelectMultipleSeats.visible()
                    txtSelectAll.gone()
                    selectAllHeader.gone()
                    selectedSeatNumber.clear()
                } else {
                    rvSelectMultipleSeats.gone()
                    txtSelectAll.gone()
                    selectAllHeader.gone()
                    isSeatClick = 0

                    if (currentCheckedItem.isNotEmpty()) {
                        updateSelectedSeats()
                    } else {
                        etSeats.setText("")
                    }
                }
            }

            btnLeft.setOnClickListener {
                builder.dismiss()
            }

            btnRight.setOnClickListener {
                if (etRemarks.text.toString().isEmpty()) {
                    context.showToast(context.getString(R.string.enter_remarks))
                } else {
                    dialogButtonMoveSeatExtraListener.onRightButtonClick(
                        etRemarks.text.toString(),
                        selectedSeatNumber.toString(),
                        selectedSeatNumber.toString(),
                        checkboxSendSms.isChecked
                    )
                    builder.dismiss()
                }
            }

            txtSelectAll.setOnClickListener {
                selectAllSeats()
            }

        }

        setSeatNoListAdapter(context)
        builder.show()
    }

    private fun setSeatNoListAdapter(context: Context) {
        dialogBinding.rvSelectMultipleSeats.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        Timber.d("Needed seat numbers: $neededSeatNumbers")
        selectMultiSeatAdapterForMultiSelection = SelectMultiSeatAdapterForMultiSelection(
            context,
            neededSeatNumbers,
            this
        )
        dialogBinding.rvSelectMultipleSeats.adapter = selectMultiSeatAdapterForMultiSelection
    }

    private fun updateSelectedSeats() {
        selectedSeatNumber.clear()
        if (currentCheckedItem.isNotEmpty()) {
            selectedSeatNumber.append(currentCheckedItem.joinToString(","))
            dialogBinding.etSeats.setText(selectedSeatNumber.toString())
        } else {
            dialogBinding.etSeats.setText("")
        }
    }

    private fun selectAllSeats() {
        currentCheckedItem.clear()
        currentCheckedItem.addAll(neededSeatNumbers)
        for (i in neededSeatNumbers.indices) {
            selectMultiSeatAdapterForMultiSelection.checkCheckBox(i, true)
        }
        updateSelectedSeats()
    }

    override fun onSeatCheck(seatNumber: String) {
        if (!currentCheckedItem.contains(seatNumber)) {
            currentCheckedItem.add(seatNumber)
            updateSelectedSeats()
        }
    }

    override fun onSeatUncheck(seatNumber: String) {
        currentCheckedItem.remove(seatNumber)
        updateSelectedSeats()
    }

    override fun isInternetOnCallApisAndInitUI() {}

    override fun isNetworkOff() {}

    override fun onButtonClick(view: Any, dialog: Dialog) {}
}
