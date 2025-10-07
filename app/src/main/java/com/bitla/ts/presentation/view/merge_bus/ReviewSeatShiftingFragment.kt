package com.bitla.ts.presentation.view.merge_bus

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.databinding.DialogBlockChartBinding
import com.bitla.ts.databinding.FragmentReviewSeatShiftingBinding
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.response.SeatMappingDetail
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request.Data
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.request.MergeBusShiftPassengerRequest
import com.bitla.ts.domain.pojo.merge_bus_shift_passenger.response.ShiftedSeat
import com.bitla.ts.presentation.view.merge_bus.adapter.ReviewSeatShiftingAdapter
import com.bitla.ts.presentation.viewModel.MergeBusSharedViewModel
import com.bitla.ts.utils.common.getCountryCodes
import com.bitla.ts.utils.common.getPhoneNumber
import com.bitla.ts.utils.dialog.DialogUtils
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import toast
import visible


class ReviewSeatShiftingFragment : Fragment(), DialogSingleButtonListener {

    private lateinit var binding: FragmentReviewSeatShiftingBinding
    private val mergeBusSharedViewModel by sharedViewModel<MergeBusSharedViewModel>()
    private var currency = ""
    private var currencyFormat = ""
    private lateinit var toReceiveAdapter: ReviewSeatShiftingAdapter
    private lateinit var toPayAdapter: ReviewSeatShiftingAdapter
    private lateinit var noPriceDifferenceAdapter: ReviewSeatShiftingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewSeatShiftingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        mergeBusSharedViewModel.privileges = (activity as BaseActivity).getPrivilegeBase()


        currency = mergeBusSharedViewModel.privilegeDetailsModel.value?.currency ?: ""
        currencyFormat =
            mergeBusSharedViewModel.privilegeDetailsModel.value?.currencyFormat
                ?: requireContext().getString(R.string.indian_currency_format)

        binding.toReceiveTV.text = "${requireContext().getString(R.string.to_receive)} (0)"
        binding.toPayTV.text = "${getString(R.string.to_pay)} (0)"
        binding.noDifferenceTV.text =
            "${requireContext().getString(R.string.no_price_difference)} (0)"


        setToolBar()
        initAdapters()
        setUpObserver()

        binding.confirmBtn.setOnClickListener {

            val fromServiceNumber =
                mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.number
            val fromServiceOrigin =
                mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.origin?.name
            val fromServiceDestination =
                mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.destination?.name
            val fromServiceText = "$fromServiceNumber $fromServiceOrigin - $fromServiceDestination"

            val toServiceNumber =
                mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.number
            val toServiceOrigin =
                mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.origin?.name
            val toServiceDestination =
                mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.destination?.name
            val toServiceText = "$toServiceNumber $toServiceOrigin - $toServiceDestination"

            var shiftedSeatsCount = 0
            var totalSeatsCount = 0

            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.forEach {

                //totalSeatsCount += it?.seats?.size ?: 0

                if (it?.isDisabled == null || it.isDisabled == false) {
                    shiftedSeatsCount += it?.seats?.size ?: 0
                }
            }

            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.forEach {
                //totalSeatsCount += it?.seats?.size ?: 0

                if (it?.isDisabled == null || it.isDisabled == false) {
                    shiftedSeatsCount += it?.seats?.size ?: 0
                }
            }

            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.forEach {
                //totalSeatsCount += it?.seats?.size ?: 0

                if (it?.isDisabled == null || it.isDisabled == false) {
                    shiftedSeatsCount += it?.seats?.size ?: 0
                }
            }

            mergeBusSharedViewModel.samePNRSeatModelMediatorLiveData.observe(requireActivity()) {
                it.forEach { item ->
                    totalSeatsCount += item.seatShiftList.size
                }
            }

            if (shiftedSeatsCount == 0) {
                requireContext().toast(getString(R.string.seats_cannot_be_empty))
                return@setOnClickListener
            }

            DialogUtils.shiftPassengerDialog(
                requireContext(),
                getString(R.string.confirmShiftingPassenger),
                getString(R.string.shiftPassengerContent),
                getString(R.string.from_service),
                fromServiceText,
                getString(R.string.to_service),
                toServiceText,
                "",
                "$shiftedSeatsCount/$totalSeatsCount",
                getString(R.string.goBack),
                getString(R.string.confirmShifting),
                this,
                false,
                showMergeBusDisclaimer = true
            )
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.updateToReceiveTV.setOnClickListener {
            val tempList = mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive

            if (tempList != null) {

                tempList.forEach {
                    it?.payStatus = "ignore"
                }

                mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.addAll(
                    tempList
                )
                clearToReceiveAdapter()
                setNoPriceDifferenceAdapter(
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference
                        ?: mutableListOf()
                )
            }
        }

        binding.updateToPayTV.setOnClickListener {
            val tempList = mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay

            if (tempList != null) {

                tempList.forEach {
                    it?.payStatus = "ignore"
                }

                mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.addAll(
                    tempList
                )
                clearToPayAdapter()
                setNoPriceDifferenceAdapter(
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference
                        ?: mutableListOf()
                )
            }
        }

    }

    private fun setToolBar() {
        binding.reviewSeatShiftingToolBar.textHeaderTitle.text =
            getString(R.string.review_seat_shifting)

        binding.reviewSeatShiftingToolBar.toolbarImageLeft.setOnClickListener {
            findNavController().navigateUp()
        }

        val leftCoachServiceDetails = mergeBusSharedViewModel.serviceDetailsLeftCoach.value
        val rightCoachServiceDetails = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value

        binding.reviewSeatShiftingToolBar.tvDateTime.text = SpannableStringBuilder()
            .color(ContextCompat.getColor(requireContext(), R.color.colorDimShadow)) {
                bold {
                    append("${leftCoachServiceDetails?.body?.depTime}, ${leftCoachServiceDetails?.body?.travelDate}")
                }
            }

        binding.reviewSeatShiftingToolBar.tvOriginDestination.text = SpannableStringBuilder()
            .color(ContextCompat.getColor(requireContext(), R.color.colorDimShadow)) {
                append(requireContext().getString(R.string.from)).bold {
                    append(" " + leftCoachServiceDetails?.body?.number + " ")
                }.append(requireContext().getString(R.string.to))
                    .bold {
                        append(" " + rightCoachServiceDetails?.body?.number)
                    }
            }

    }

    private fun initAdapters() {
        setNoPriceDifferenceAdapter(mutableListOf())
        setToPayAdapter(mutableListOf())
        setToReceiveAdapter(mutableListOf())
    }

    private fun setNoPriceDifferenceAdapter(noDifferenceList: MutableList<SeatMappingDetail?>) {

        binding.noDifferenceTV.text =
            "${requireContext().getString(R.string.no_price_difference)} (${noDifferenceList.size})"

        noPriceDifferenceAdapter = ReviewSeatShiftingAdapter(
            context = requireContext(),
            type = "noDifference",
            currency = currency,
            currencyFormat = currencyFormat,
            //seatMappingList = noDifferenceList
            onCallButtonClick = {
                callFunction(it)
            },
            onIgnoreMenuItemClick = { position ->

            },
            onRemovePassengerMenuItemClick = { position ->

                dialogRemovePassenger(
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.get(
                        position
                    ), position, 3
                )

            }
        )

        noPriceDifferenceAdapter.addItemsToList(noDifferenceList)

        binding.noDifferenceRV.adapter = noPriceDifferenceAdapter
    }

    private fun addItemToNoPriceDifferenceAdapter(item: SeatMappingDetail?) {
        mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.add(item)
        noPriceDifferenceAdapter.addItemToList(item)
        Timber.d(mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference.toString())
        binding.noDifferenceTV.text =
            "${requireContext().getString(R.string.no_price_difference)} (${mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.size})"
    }

    private fun setToReceiveAdapter(toReceiveList: MutableList<SeatMappingDetail?>) {

        binding.toReceiveTV.text = "${requireContext().getString(R.string.to_receive)} (${toReceiveList.size})"

        toReceiveAdapter = ReviewSeatShiftingAdapter(
            context = requireContext(),
            type = "toReceive",
            currency = currency,
            currencyFormat = currencyFormat,
            //seatMappingList = toReceiveList
            onCallButtonClick = {
                callFunction(it)
            },
            onIgnoreMenuItemClick = { position ->
                val item =
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.get(position)
                item?.payStatus = "ignore"

                removeItemFromToReceiveAdapter(position)

                addItemToNoPriceDifferenceAdapter(item)

            },
            onRemovePassengerMenuItemClick = { position ->
//                mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.get(position)?.isDisabled = true
//                mergeBusSharedViewModel.removedPassengersList.value?.add(mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.get(position)?.seats?.get(0)?.oldSeat!!)
//                toReceiveAdapter.notifyItemChanged(position,
//                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.get(position)
//                )

                dialogRemovePassenger(
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.get(
                        position
                    ), position, 1
                )

            }
        )

        toReceiveAdapter.addItemsToList(toReceiveList)

        binding.toReceiveRV.adapter = toReceiveAdapter
    }

    private fun removeItemFromToReceiveAdapter(position: Int) {
        mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.removeAt(position)
        binding.toReceiveTV.text =
            "${requireContext().getString(R.string.to_receive)} (${mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.size})"
        toReceiveAdapter.removeItemFromList(position)
        if (mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.isEmpty() == true) {
            binding.updateToReceiveTV.gone()
        } else {
            binding.updateToReceiveTV.visible()
        }
    }

    private fun clearToReceiveAdapter() {
        mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.clear()
        toReceiveAdapter.clearList()
        binding.toReceiveTV.text = "${requireContext().getString(R.string.to_receive)} (0)"
        binding.updateToReceiveTV.gone()
    }


    private fun setToPayAdapter(toPayList: MutableList<SeatMappingDetail?>) {

        binding.toPayTV.text = "${getString(R.string.to_pay)} (${toPayList.size})"

        toPayAdapter = ReviewSeatShiftingAdapter(
            context = requireContext(),
            type = "toPay",
            currency = currency,
            currencyFormat = currencyFormat,
            //seatMappingList = toPayList
            onCallButtonClick = {
                callFunction(it)
            },
            onIgnoreMenuItemClick = { position ->
                val item =
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.get(position)
                item?.payStatus = "ignore"

                removeItemFromToPayAdapter(position)

                addItemToNoPriceDifferenceAdapter(item)

            },
            onRemovePassengerMenuItemClick = { position ->
//                mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.get(position)?.isDisabled = true
//                mergeBusSharedViewModel.removedPassengersList.value?.add(mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.get(position)?.seats?.get(0)?.oldSeat!!)
//                toPayAdapter.notifyItemChanged(position,
//                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.get(position)
//                )

                dialogRemovePassenger(
                    mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.get(position),
                    position,
                    2
                )
            }
        )

        toPayAdapter.addItemsToList(toPayList)
        binding.toPayRV.adapter = toPayAdapter
    }

    private fun removeItemFromToPayAdapter(position: Int) {
        mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.removeAt(position)
        binding.toPayTV.text =
            "${getString(R.string.to_pay)} (${mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.size})"
        toPayAdapter.removeItemFromList(position)

        //mergeBusSharedViewModel.removedPassengersList.value?.add(mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay!![position]?.pnrNumber!!)

        if (mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.isEmpty() == true) {
            binding.updateToPayTV.gone()
        } else {
            binding.updateToPayTV.visible()
        }
    }

    private fun clearToPayAdapter() {
        mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.clear()
        toPayAdapter.clearList()
        binding.toPayTV.text = "${requireContext().getString(R.string.to_pay)} (0)"
        binding.updateToPayTV.gone()
    }

    override fun onSingleButtonClick(str: String) {
        showProgressBar()
        callMergeBusShiftPassengerApi()
    }

    private fun setUpObserver() {
        mergeBusSharedViewModel.mergeBusSeatMapping.observe(viewLifecycleOwner) {

            hideProgressBar()

            if (it != null) {

                if (it.code == 200) {
                    if (it.body?.toReceive != null && it.body.toReceive.isNotEmpty()) {
                        it.body.toReceive.forEach { seatMappingDetail ->
                            seatMappingDetail?.payStatus = "received"
                        }
                        setToReceiveAdapter(it.body.toReceive)
                    } else {
                        binding.updateToReceiveTV.gone()
                    }

                    if (it.body?.toPay != null && it.body.toPay.isNotEmpty()) {
                        it.body.toPay.forEach { seatMappingDetail ->
                            seatMappingDetail?.payStatus = "paid"
                        }
                        setToPayAdapter(it.body.toPay)
                    } else {
                        binding.updateToPayTV.gone()
                    }

                    if (it.body?.noDifference != null && it.body.noDifference.isNotEmpty()) {
                        it.body.noDifference.forEach { seatMappingDetail ->
                            seatMappingDetail?.payStatus = "ignore"
                        }
                        setNoPriceDifferenceAdapter(it.body.noDifference)
                    }
                } else if(it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else {
                    requireContext().toast(it.message)
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        mergeBusSharedViewModel.mergeBusShiftPassenger.observe(viewLifecycleOwner) {

            hideProgressBar()

            if (it != null) {
                if (it.code == 200) {
                    if (it.notShifted?.isNotEmpty() == true) {
                        it.notShifted.split(",").forEach { notShiftedSeat ->
                            val shiftedSeat = ShiftedSeat(
                                from = notShiftedSeat,
                                to = requireContext().getString(R.string.notAvailable)
                            )
                            it.shiftedSeats?.add(shiftedSeat)
                        }
                    }
                    findNavController().navigate(R.id.action_reviewSeatShiftingFragment_to_successSeatShiftingFragment)
                } else if(it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else {
                    requireContext().toast(it.message)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

    }

    private fun callFunction(phoneNumber: String) {
        var countryList = ArrayList<Int>()

        try {
            if (getCountryCodes().isNotEmpty()) countryList = getCountryCodes()
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 200
            )
        } else {
            val telNo = getPhoneNumber(
                passPhone = phoneNumber,
                mergeBusSharedViewModel.privilegeDetailsModel.value?.country
            )
            if (countryList.isNotEmpty()) {
                val finalTelNo = if (phoneNumber.contains("+")) {
                    "+$telNo"
                } else {
                    "+${countryList[0]}$telNo"
                }
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$finalTelNo"))
                startActivity(intent)
            }
        }
    }

    private fun callMergeBusShiftPassengerApi() {
        if (requireContext().isNetworkAvailable()) {
            val dataList = mutableListOf<Data>()

            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toReceive?.forEach {
                if (it?.isDisabled != true) {

                    val oldSeatNumbersList = mutableListOf<String>()
                    val newSeatNumbersList = mutableListOf<String>()
                    it?.seats?.forEach { seats ->
                        oldSeatNumbersList.add(seats?.oldSeat ?: "")
                        newSeatNumbersList.add(seats?.newSeat ?: "")
                    }
                    val item = Data(
                        newSeatNumber = newSeatNumbersList.joinToString(","),
                        oldSeatNumber = oldSeatNumbersList.joinToString(","),
                        payStatus = it?.payStatus ?: "received",
                        ticketNumber = it?.pnrNumber ?: ""
                    )

                    dataList.add(item)
                }
            }
            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.toPay?.forEach {
                if (it?.isDisabled != true) {

                    val oldSeatNumbersList = mutableListOf<String>()
                    val newSeatNumbersList = mutableListOf<String>()
                    it?.seats?.forEach { seats ->
                        oldSeatNumbersList.add(seats?.oldSeat ?: "")
                        newSeatNumbersList.add(seats?.newSeat ?: "")
                    }
                    val item = Data(
                        newSeatNumber = newSeatNumbersList.joinToString(","),
                        oldSeatNumber = oldSeatNumbersList.joinToString(","),
                        payStatus = it?.payStatus ?: "paid",
                        ticketNumber = it?.pnrNumber ?: ""
                    )

                    dataList.add(item)
                }
            }

            mergeBusSharedViewModel.mergeBusSeatMapping.value?.body?.noDifference?.forEach {
                if (it?.isDisabled != true) {

                    val oldSeatNumbersList = mutableListOf<String>()
                    val newSeatNumbersList = mutableListOf<String>()
                    it?.seats?.forEach { seats ->
                        oldSeatNumbersList.add(seats?.oldSeat ?: "")
                        newSeatNumbersList.add(seats?.newSeat ?: "")
                    }
                    val item = Data(
                        newSeatNumber = newSeatNumbersList.joinToString(","),
                        oldSeatNumber = oldSeatNumbersList.joinToString(","),
                        payStatus = it?.payStatus ?: "ignore",
                        ticketNumber = it?.pnrNumber ?: ""
                    )

                    dataList.add(item)
                }
            }

            val mergeBusShiftPassengerRequest = MergeBusShiftPassengerRequest(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                data = dataList,
                isFromMiddleTier = true,
                locale = mergeBusSharedViewModel.locale.value,
                newResId = mergeBusSharedViewModel.reservationIdRightCoach.value,
                oldResId = mergeBusSharedViewModel.reservationIdLeftCoach.value,
                remarks = "",
                shiftToExtraSeats = "",
                toSendSms = ""
            )

            mergeBusSharedViewModel.mergeBusShiftPassenger(
                mergeBusShiftPassengerRequest
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visible()
    }

    private fun hideProgressBar() {
        binding.progressBar.gone()
    }


    private fun dialogRemovePassenger(
        seatDetails: SeatMappingDetail?,
        position: Int,
        adapterType: Int
    ) {
        val builder = AlertDialog.Builder(context).create()
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding: DialogBlockChartBinding =
            DialogBlockChartBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        binding.tvHeader.text =
            getString(R.string.remove_passenger_from) + mergeBusSharedViewModel?.serviceDetailsLeftCoach?.value?.body?.number
                ?: ""

        val subText =
            SpannableStringBuilder().append(getString(R.string.are_you_sure_you_want_to_remove_passenger_with_pnr))
                .bold { append(seatDetails?.pnrNumber) }.append(getString(R.string.from_service_lower_case))
                .bold { append(mergeBusSharedViewModel?.serviceDetailsLeftCoach?.value?.body?.number) }
                .append("?")




        binding.tvSubtitle.text = subText
        binding.tvMessage.text =
            getString(R.string.this_action_cannot_be_reversed_please_confirm_before_proceeding)
        binding.btnLight.text = requireContext().getString(R.string.remove_passenger)
        binding.btnDark.text = requireContext().getString(R.string.goBack)

        binding.tvHeader.textSize = 18f
        binding.tvHeader.setTextColor(Color.BLACK)

        binding.tvSubtitle.textSize = 16f
        binding.tvHeaderText.visibility = View.GONE
        binding.viewMiddle.visibility = View.GONE
        binding.viewTop.visibility = View.GONE
        binding.tvSubtitle.setTextColor(Color.BLACK)
        binding.tvSubtitle.typeface = Typeface.DEFAULT


        binding.btnDark.setBackgroundColor(context?.resources!!.getColor(R.color.light_highlight_color))
        binding.btnDark.setTextColor(context?.resources!!.getColor(R.color.colorPrimary))



        binding.btnLight.setOnClickListener {
            seatDetails?.isDisabled = true
            mergeBusSharedViewModel.removedPassengersList.value?.add(seatDetails?.seats?.get(0)?.oldSeat!!)

            when (adapterType) {
                1 -> {
                    toReceiveAdapter.notifyItemChanged(
                        position,
                        seatDetails
                    )
                }

                2 -> {
                    toPayAdapter.notifyItemChanged(
                        position,
                        seatDetails
                    )
                }

                3 -> {
                    noPriceDifferenceAdapter.notifyItemChanged(
                        position,
                        seatDetails
                    )
                }
            }

            builder.cancel()
        }
        binding.btnDark.setOnClickListener {
            builder.cancel()
        }
        builder.setView(binding.root)
        builder.show()
    }
}