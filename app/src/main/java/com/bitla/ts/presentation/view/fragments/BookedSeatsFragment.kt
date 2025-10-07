package com.bitla.ts.presentation.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseUpdateCancelTicket
import com.bitla.ts.app.base.SingleTicketUpdateSheet
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.FragmentBookedSeatsBinding
import com.bitla.ts.databinding.SheetModifyDetailsBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.PassengerDetails
import com.bitla.ts.presentation.adapter.BookedSeatAdapter
import com.bitla.ts.utils.common.getPassengerDetails
import com.google.android.material.bottomsheet.BottomSheetDialog
import gone
import timber.log.Timber
import visible


class BookedSeatsFragment : BaseUpdateCancelTicket(), OnItemClickListener,
    DialogSingleButtonListener {

    private var privileges: PrivilegeResponseModel? = null
    lateinit var binding: FragmentBookedSeatsBinding

    private lateinit var bookedSeatAdapter: BookedSeatAdapter
    private var passengerDetailsList = mutableListOf<PassengerDetails>()
    private lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookedSeatsBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    private fun initUi() {
        privileges = (activity as BaseActivity).getPrivilegeBase()
        setBookedSeatAdapter()
    }

    private fun setBookedSeatAdapter() {
        Timber.d("editchartBookFragment : 0 ${getPassengerDetails()}")
        passengerDetailsList = getPassengerDetails()
        if (passengerDetailsList.isEmpty()) {
            binding.nestedScrollView.gone()
            binding.NoResult.visible()
        } else {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.rvBookedSeats.layoutManager = layoutManager
            bookedSeatAdapter =
                BookedSeatAdapter(requireActivity(), this, passengerDetailsList,privileges)
            binding.rvBookedSeats.adapter = bookedSeatAdapter
        }
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        // showModifyPassengersSheet()
        Timber.d("editchartBookFragment : 0 ${view.tag}")
        var ticketNo = passengerDetailsList[position].ticketNo
        var seatNumber = passengerDetailsList[position].seatNo
        if (ticketNo != null && ticketNo.contains(" ")) {
            ticketNo = ticketNo.substringBefore(" ").trim()
        }
        try {
            Timber.d("editchartBookFragment : 1${ticketNo} $seatNumber")

            /*val isDigitOnly = android.text.TextUtils.isDigitsOnly(ticketNo)
            Timber.d("isDigitOnly $isDigitOnly")
            if (isDigitOnly)*/
            if (!seatNumber.isNullOrEmpty())
                ticketNo?.let {
                    //showSingleTicketUpdateSheet(ticketNo, seatNumber)
                    val singleTicketUpdateSheet = SingleTicketUpdateSheet(this)
                    singleTicketUpdateSheet.showSingleTicketUpdateSheet(ticketNo,seatNumber)
                }
            /* else
             {
                 ticketNo = ticketNo?.replace("[^\\d.]", "")
                 ticketNo?.let { showSingleTicketUpdateSheet(ticketNo.toInt()) }
             }*/
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onSingleButtonClick(str: String) {

    }

    private fun showModifyPassengersSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        val binding = SheetModifyDetailsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        binding.autoCompleteGender.setAdapter(
            ArrayAdapter(
                requireActivity(),
                R.layout.spinner_dropdown_item,
                R.id.tvItem,
                resources.getStringArray(R.array.genderArray)
            )
        )

        binding.tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
