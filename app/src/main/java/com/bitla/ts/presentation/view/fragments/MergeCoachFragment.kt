package com.bitla.ts.presentation.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import areAllSeatsNotShiftedOfGivenPNR
import areAllSeatsShiftedOfGivenPNR
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.app.base.BaseFragment
import com.bitla.ts.data.multistation_seat_details_api
import com.bitla.ts.data.operator_api_key
import com.bitla.ts.databinding.FragmentMergeCoachBinding
import com.bitla.ts.domain.pojo.add_bp_dp_to_service.request.AddBpDpToServiceRequest
import com.bitla.ts.domain.pojo.merge_bus_seat_mapping.request.MergeBusSeatMappingRequest
import com.bitla.ts.domain.pojo.merge_service_details.request.MergeServiceDetailsRequest
import com.bitla.ts.domain.pojo.recommended_seats.request.RecommendedSeatsRequest
import com.bitla.ts.presentation.view.merge_bus.TicketShiftedToAdapter
import com.bitla.ts.presentation.view.merge_bus.pojo.ExactRouteService
import com.bitla.ts.presentation.viewModel.MergeBusSharedViewModel
import com.bitla.ts.utils.dialog.DialogUtils
import com.example.buscoach.CoachFragment
import com.example.buscoach.listeners.SeatSelectListener
import com.example.buscoach.service_details_response.SeatDetail
import com.example.buscoach.service_details_response.ServiceDetailsModel
import com.example.buscoach.utils.Const
import com.google.android.material.bottomsheet.BottomSheetBehavior
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import toast
import visible


class MergeCoachFragment : BaseFragment(), SeatSelectListener {

    private lateinit var binding: FragmentMergeCoachBinding
    private val mergeBusSharedViewModel by sharedViewModel<MergeBusSharedViewModel>()
    private lateinit var leftCoachFragment: CoachFragment
    private lateinit var rightCoachFragment: CoachFragment
    private lateinit var rightCoachExactRouteService: ExactRouteService
    private var reservationId = ""
    private var originId = ""
    private var destinationId = ""
    private var excludePassengerDetails = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        mergeBusSharedViewModel.privileges = (activity as BaseActivity).getPrivilegeBase()

        if (this::binding.isInitialized.not()) {
            binding = FragmentMergeCoachBinding.inflate(inflater, container, false)

            if (requireActivity().intent.hasExtra("rightCoachExactRouteService")) {
                rightCoachExactRouteService =
                    requireActivity().intent.getSerializableExtra("rightCoachExactRouteService") as ExactRouteService

                if (rightCoachExactRouteService.reservationId.isNullOrEmpty()) {
                    requireContext().toast(getString(R.string.right_coach_reservation_id_not_found))
                    requireActivity().finish()

                } else {
                    mergeBusSharedViewModel.setRightCoachReservationId(
                        rightCoachExactRouteService.reservationId ?: "0"
                    )
                }
            } else {
                requireContext().toast(getString(R.string.right_coach_details_not_found))
                requireActivity().finish()
            }

            if (requireActivity().intent.hasExtra("leftCoachReservationId")
                && requireActivity().intent.getStringExtra("leftCoachReservationId") != null
            ) {
                reservationId =
                    requireActivity().intent.getStringExtra("leftCoachReservationId") ?: ""

                mergeBusSharedViewModel.setLeftCoachReservationId(reservationId)
            } else {
                requireContext().toast(getString(R.string.left_coach_reservation_id_not_found))
                requireActivity().finish()
            }

            if (requireActivity().intent.hasExtra("leftCoachOriginId")
                && requireActivity().intent.getStringExtra("leftCoachOriginId") != null
            ) {

                originId = requireActivity().intent.getStringExtra("leftCoachOriginId") ?: ""

            } else {
                requireContext().toast(getString(R.string.left_coach_origin_id_not_found))
                requireActivity().finish()
            }

            if (requireActivity().intent.hasExtra("leftCoachDestinationId")
                && requireActivity().intent.getStringExtra("leftCoachDestinationId") != null
            ) {

                destinationId =
                    requireActivity().intent.getStringExtra("leftCoachDestinationId") ?: ""

            } else {
                requireContext().toast(getString(R.string.left_coach_destination_id_not_found))
                requireActivity().finish()
            }

            if (mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.code == null) {
                if (requireContext().isNetworkAvailable()) {
                    callLeftCoachServiceDetailsApi()
                    //fetchAPIDataLocally()
                } else {
                    requireContext().noNetworkToast()
                }
            }

            setUpObserver()
            setUpBottomSheets()
            setClickOnListeners()
        }

        return binding.root
    }

    private fun setupToolbar() {
        binding.mergeBusCoachToolBar.selectedTV.visible()
        binding.mergeBusCoachToolBar.textHeaderTitle.text = getString(R.string.shift_passengers)
        //binding.mergeBusCoachToolBar.headerTitleDesc.text = ""
        binding.mergeBusCoachToolBar.toolbarImageLeft.setOnClickListener {
            activity?.finish()
        }

        val leftCoachServiceDetails = mergeBusSharedViewModel.serviceDetailsLeftCoach.value
        val rightCoachServiceDetails = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value

        binding.mergeBusCoachToolBar.tvDateTime.text = SpannableStringBuilder()
            .color(ContextCompat.getColor(requireContext(), R.color.colorDimShadow)) {
                bold {
                    append("${leftCoachServiceDetails?.body?.depTime}, ${leftCoachServiceDetails?.body?.travelDate}")
                }
            }

        binding.mergeBusCoachToolBar.tvOriginDestination.text = SpannableStringBuilder()
            .color(ContextCompat.getColor(requireContext(), R.color.colorDimShadow)) {
                append(requireContext().getString(R.string.from)).bold {
                    append(" " + leftCoachServiceDetails?.body?.number + " ")
                }.append(requireContext().getString(R.string.to))
                    .bold {
                        append(" " + rightCoachServiceDetails?.body?.number)
                    }
            }

    }

    private fun setClickOnListeners() {
        binding.confirmBtn.setOnClickListener {
            if (requireContext().isNetworkAvailable()) {
                callSeatMappingApi()
            } else {
                requireContext().noNetworkToast()
            }
        }

        binding.cancelBtn.setOnClickListener {
            mergeBusSharedViewModel.deSelectAllSeats()
            rightCoachFragment.clearAnimationForAllSeats()
            leftCoachFragment.setMultiHopAdapter(mutableListOf())
            leftCoachFragment.hideMultiHopLayout()
            requireContext().toast("All selected seats will be deselected")
        }
    }


    private fun setUpBottomSheets() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.bottomSheet.bottomSheet.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.bottomSheet.bottomSheetHeader.text =
                        getString(R.string.hide_seat_details)
                    binding.bottomSheet.bottomSheetHeader.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down,
                        0
                    )
                } else {
                    binding.bottomSheet.bottomSheetHeader.text =
                        getString(R.string.view_seat_details)
                    binding.bottomSheet.bottomSheetHeader.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_up,
                        0
                    )
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


    }

    override fun isInternetOnCallApisAndInitUI() {

    }

    override fun isNetworkOff() {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    private fun addFragment(coachFragment: CoachFragment, fragmentId: Int) {
        childFragmentManager.beginTransaction().replace(fragmentId, coachFragment)
            .commit()
    }

    override fun onSeatClick(pnr: String, seatDetail: SeatDetail, fragmentPosition: Int) {
        when (fragmentPosition) {
            Const.FRAGMENT_LEFT -> {

                val previousPNR = mergeBusSharedViewModel.currentSelectedPNR.value
                val previousSeat = mergeBusSharedViewModel.currentSelectedSeat.value

                if (seatDetail.isMultiHop == true) {

                    if(seatDetail.isSelected) {

                        val otherPnr = mergeBusSharedViewModel.getPNROfNonMultiHopSeatIfMultiHopSeatIsSelected(seatDetail)
                        val nonMultiHopSeat = mergeBusSharedViewModel.getNonMultiHopSeatIfMultiHopSeatIsSelected(seatDetail)

                        if(otherPnr.equals(previousPNR)) {
                            mergeBusSharedViewModel.deSelectAllSeatsWithSamePNR(
                                otherPnr
                            )
                        } else {
                            if(nonMultiHopSeat != null) {
                                mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(
                                    nonMultiHopSeat
                                )
                                mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                                    otherPnr,
                                    nonMultiHopSeat
                                )
                            }
                        }

                        return
                    }
                    callMultiStationSeatDetailApi(
                        reservationId = mergeBusSharedViewModel.reservationIdLeftCoach.value ?: "",
                        seatNumber = seatDetail.number ?: ""
                    )

                    /*mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        null,
                        null
                    )*/

                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    return

                }


                if (mergeBusSharedViewModel.currentSelectedPNR.value.equals(pnr, true)) {

                    if (mergeBusSharedViewModel.shiftSeatsHashMapLiveData.value?.containsKey(
                            seatDetail
                        ) == false
                    ) {
                        if (requireContext().isNetworkAvailable()) {
                            callRecommendedSeatsRightCoachApi(seatDetail)
                        } else {
                            requireContext().toast(getString(R.string.no_network_msg))
                            return
                        }

                    }

                    mergeBusSharedViewModel.selectWholePNRGroupOrDeSelectWholePNRGroupWhenClickedOnLeftCoach(
                        seatDetail
                    )
                } else {
                    if (mergeBusSharedViewModel.isPartialShiftingDone()) {
                        requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
                    } else if (mergeBusSharedViewModel.hasNoneSeatsBeenShiftedOfPreviousPNR()) {

                        if (::rightCoachFragment.isInitialized) {
                            rightCoachFragment.clearAnimationForAllSeats()
                        }

                        if (requireContext().isNetworkAvailable()) {
                            callRecommendedSeatsRightCoachApi(seatDetail)
                        } else {
                            requireContext().toast(getString(R.string.no_network_msg))
                        }

                        mergeBusSharedViewModel.deSelectAllSeatsWithSamePNR(previousPNR)

                        if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {
                            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                                pnr,
                                seatDetail
                            )

                            mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(seatDetail)
                        } else {

                            if (::rightCoachFragment.isInitialized) {
                                rightCoachFragment.clearAnimationForAllSeats()
                            }

                            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                                pnr,
                                seatDetail
                            )

                            mergeBusSharedViewModel.deSelectSeatIfPNRGroupIsAlreadyShifted()
                            mergeBusSharedViewModel.selectAllSeatsWithSamePNRNew()
                            mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(seatDetail)

                        }
                    } else {

                        if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {
                            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                                pnr,
                                seatDetail
                            )

                            mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(seatDetail)
                        } else {

                            if (requireContext().isNetworkAvailable()) {
                                callRecommendedSeatsRightCoachApi(seatDetail)
                            } else {
                                requireContext().toast(getString(R.string.no_network_msg))
                            }
                            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                                pnr,
                                seatDetail
                            )

                            mergeBusSharedViewModel.deSelectSeatIfPNRGroupIsAlreadyShifted()
                            mergeBusSharedViewModel.selectAllSeatsWithSamePNRNew()
                            mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(seatDetail)

                        }
                    }
                }
            }

            Const.FRAGMENT_RIGHT -> {
                mergeBusSharedViewModel.isSeatAlreadySelectedInRightCoach(seatDetail, leftCoachFragment.isMultiHopLayoutVisible())
            }
        }
    }

    override fun selectPreviouslySelectedSeats(pnr: String, fragmentPosition: Int) {
        when(fragmentPosition) {
            Const.FRAGMENT_RIGHT -> {
                if(leftCoachFragment.isMultiHopLayoutVisible()) {
                    mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUIMultiHop()
                } else {
                    mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
                }

                //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()

            }
        }
    }

    override fun onMultiHopSeatClickNew(
        parentPosition: Int,
        childPosition: Int,
        seatDetail: SeatDetail
    ) {
        val previousPNR = mergeBusSharedViewModel.currentSelectedPNR.value
        val previousSeat = mergeBusSharedViewModel.currentSelectedSeat.value

        if (previousPNR.equals(seatDetail.passengerDetails?.ticketNo, true)) {

            /*if (mergeBusSharedViewModel.shiftSeatsHashMapLiveData.value?.containsLeftSideSeatIfLeftSideSeatIsPassedInParam(
                    seatDetail
                ) == false
            ) {
                mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(seatDetail)
                return
            }*/

            if (seatDetail.isSelected) {
                mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(seatDetail)
            } else {
                if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {
                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    mergeBusSharedViewModel.selectOnlyBorderOfParticularPNRGroup(
                        seatDetail.passengerDetails?.ticketNo ?: ""
                    )
                } else {

                    if (::rightCoachFragment.isInitialized) {
                        rightCoachFragment.clearAnimationForAllSeats()
                    }

                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    mergeBusSharedViewModel.selectAllSeatsWithSamePNRMultiHop(seatDetail)

                    callRecommendedSeatsRightCoachApi(seatDetail)

                }
            }

        } else {
            if (mergeBusSharedViewModel.isPartialShiftingDone()) {
                requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
                return
            } else if (mergeBusSharedViewModel.hasNoneSeatsBeenShiftedOfPreviousPNR()) {

                if (::rightCoachFragment.isInitialized) {
                    rightCoachFragment.clearAnimationForAllSeats()
                }

                if (requireContext().isNetworkAvailable()) {
                    callRecommendedSeatsRightCoachApi(seatDetail)

                    /*if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                        callMergeServiceDetailsRightCoachApi(
                            originId = seatDetail.passengerDetails?.originId.toString() ?: "",
                            destinationId = seatDetail.passengerDetails?.destinationId.toString() ?: ""
                        )
                    }*/
                } else {
                    requireContext().toast(getString(R.string.no_network_msg))
                }

                if (previousSeat != null) {
                    mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(previousSeat)
                }

                if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {
                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    mergeBusSharedViewModel.selectOnlyBorderOfParticularPNRGroup(
                        seatDetail.passengerDetails?.ticketNo ?: ""
                    )
                } else {

                    if (::rightCoachFragment.isInitialized) {
                        rightCoachFragment.clearAnimationForAllSeats()
                    }

                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    mergeBusSharedViewModel.deSelectSeatIfPNRGroupIsAlreadyShiftedMultiHop(
                        seatDetail
                    )
                    mergeBusSharedViewModel.selectAllSeatsWithSamePNRMultiHop(seatDetail)
                }
            } else {

                if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {

                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo,
                        seatDetail
                    )

                    mergeBusSharedViewModel.selectOnlyBorderOfParticularPNRGroup(
                        seatDetail.passengerDetails?.ticketNo ?: ""
                    )

                    if(mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                        callMergeServiceDetailsRightCoachApi(
                            originId = seatDetail.passengerDetails?.originId.toString(),
                            destinationId = seatDetail.passengerDetails?.destinationId.toString(),
                        )
                    }
                } else {

                    if (requireContext().isNetworkAvailable()) {
                        callRecommendedSeatsRightCoachApi(
                            seatDetail
                        )

                        /*if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                            callMergeServiceDetailsRightCoachApi(
                                originId = seatDetail.passengerDetails?.originId.toString(),
                                destinationId = seatDetail.passengerDetails?.destinationId.toString()
                            )
                        }*/
                    } else {
                        requireContext().toast(getString(R.string.no_network_msg))
                    }
                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        seatDetail.passengerDetails?.ticketNo ?: "",
                        seatDetail
                    )

                    mergeBusSharedViewModel.deSelectSeatIfPNRGroupIsAlreadyShiftedMultiHop(
                        seatDetail
                    )
                    mergeBusSharedViewModel.selectAllSeatsWithSamePNRMultiHop(seatDetail)
                }
            }
        }

        leftCoachFragment.selectOrDeselectAllSeatsOfSpecificPNRGroupInMultiHopAdapter(
            parentPosition,
            mergeBusSharedViewModel.currentMultiHopArrayLiveData.value?.get(parentPosition)?.seat_details!!
        )
    }

    override fun onMultiHopSeatClick(
        parentPosition: Int,
        childPosition: Int,
        seatDetail: SeatDetail
    ) {
        //leftCoachFragment.selectSpecificSeatInMultiHopAdapter(parentPosition, childPosition, seatDetail)
        val previousPNR = mergeBusSharedViewModel.currentSelectedPNR.value
        val previousSeat = mergeBusSharedViewModel.currentSelectedSeat.value

        var areAllSeatsShifted = false
        var areAllSeatsNotShifted = false

        if (previousPNR != null) {
            if (!seatDetail.passengerDetails?.ticketNo.equals(previousPNR, true)) {

                if (mergeBusSharedViewModel.isPartialShiftingDone()) {
                    requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
                } else if (mergeBusSharedViewModel.hasNoneSeatsBeenShiftedOfPreviousPNR()) {

                    if (::rightCoachFragment.isInitialized) {
                        rightCoachFragment.clearAnimationForAllSeats()
                    }

                    if (requireContext().isNetworkAvailable()) {
                        callRecommendedSeatsRightCoachApi(
                            seatDetail
                        )
                    } else {
                        requireContext().toast(getString(R.string.no_network_msg))
                    }

                    mergeBusSharedViewModel.deSelectAllSeatsWithSamePNR(previousPNR)


                    if (mergeBusSharedViewModel.hasAllSeatsBeenShiftedOfParticularPNR(seatDetail)) {
                        mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                            seatDetail.passengerDetails?.ticketNo,
                            seatDetail
                        )

                        mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(
                            parentPosition
                        )?.seat_details?.forEach {
                            it.isSelected = true
                        }

                        mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.forEachIndexed { index, item ->
                            if (item.pnr.equals(seatDetail.passengerDetails?.ticketNo)) {
                                item.isPNRGroupSelected = true
                                item.seat_details?.forEach {
                                    it.isSelected = true
                                }
                            } else {
                                item.isPNRGroupSelected = false
                                item.seat_details?.forEach {
                                    it.isSelected = false
                                }
                            }
                        }

                    } else {

                        if (::rightCoachFragment.isInitialized) {
                            rightCoachFragment.clearAnimationForAllSeats()
                        }

                        mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                            seatDetail.passengerDetails?.ticketNo,
                            seatDetail
                        )

                        mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(seatDetail)
                        mergeBusSharedViewModel.selectAllSeatsWithSamePNRMultiHop(seatDetail)
                    }
                }

                callRecommendedSeatsRightCoachApi(seatDetail)

                /*if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                    callMergeServiceDetailsRightCoachApi(
                        originId = seatDetail.passengerDetails?.originId.toString(),
                        destinationId = seatDetail.passengerDetails?.destinationId.toString()
                    )
                }*/

                areAllSeatsShifted =
                    mergeBusSharedViewModel.shiftSeatsHashMapLiveData.value?.areAllSeatsShiftedOfGivenPNR(
                        previousPNR
                    ) ?: false

                areAllSeatsNotShifted =
                    mergeBusSharedViewModel.shiftSeatsHashMapLiveData.value?.areAllSeatsNotShiftedOfGivenPNR(
                        previousPNR
                    ) ?: false

            } else {
                areAllSeatsNotShifted = true
            }
        } else {
            areAllSeatsNotShifted = true
        }



        if (seatDetail.isSelected) {

            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(null, null)
            mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(
                parentPosition
            )?.seat_details?.forEach {
                it.isSelected = false
            }
            mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(seatDetail)


        } else {

            /*if(!seatDetail.passengerDetails?.ticketNo.equals(previousPNR, true)) {
                if(areAllSeatsNotShifted && previousSeat != null) {
                    val index = mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.indexOfFirst {
                        it.pnr.equals(previousPNR)
                    } ?: -1

                    if(index != -1) {

                        mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(index)?.seat_details?.forEach {
                            it.isSelected = false
                        }

                        mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(previousSeat)

                        leftCoachFragment.selectOrDeselectAllSeatsOfSpecificPNRGroupInMultiHopAdapter(
                            index,
                            mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(index)?.seat_details!!
                        )
                    }
                }
            }*/


            if (areAllSeatsShifted || areAllSeatsNotShifted) {

                if (areAllSeatsNotShifted) {
                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(null, null)
                    mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(
                        parentPosition
                    )?.seat_details?.forEach {
                        it.isSelected = false
                    }
                    mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(seatDetail)
                }
                mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                    seatDetail.passengerDetails?.ticketNo,
                    seatDetail
                )

                mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(
                    parentPosition
                )?.seat_details?.forEach {
                    it.isSelected = true
                }

                mergeBusSharedViewModel.selectAllSeatsWithSamePNRMultiHop(seatDetail)
            }

        }

        leftCoachFragment.selectOrDeselectAllSeatsOfSpecificPNRGroupInMultiHopAdapter(
            parentPosition,
            mergeBusSharedViewModel.multistationSeatData.value?.body?.multi_hop_seat_detail?.get(
                parentPosition
            )?.seat_details!!
        )
    }

    override fun onToolTipForServiceNameClick(fragmentPosition: Int) {
        when (fragmentPosition) {
            Const.FRAGMENT_LEFT -> {
                if (::rightCoachFragment.isInitialized) {
                    rightCoachFragment.showToolTipForServiceName()
                }
            }

            Const.FRAGMENT_RIGHT -> {

            }
        }
    }

    override fun onDoneButtonClick() {

        /*var flag = true
        kotlin.run runBlock@{

            mergeBusSharedViewModel.samePNRSeatModelMediatorLiveData.value?.forEach { samePNRSeatModel ->


                *//*mergeBusSharedViewModel.currentMultiHopArray.value?.forEach { multiHopSeatDetail ->
                    if (multiHopSeatDetail.pnr?.equals(samePNRSeatModel.pnr) == true) {
                        if(samePNRSeatModel.seatShiftList.all { seatShiftModel ->
                                seatShiftModel.newSeat != null
                            }) {
                            flag = true
                            return@runBlock
                        }
                    }
                }*//*
                if(
                    samePNRSeatModel.seatShiftList.all{ it.newSeat == null}
                    ||
                    samePNRSeatModel.seatShiftList.all{ it.newSeat != null}
                    ) {
                    flag = true
                } else {
                    flag = false
                    return@runBlock

                }

            }
        }*/


        if (!mergeBusSharedViewModel.isPartialShiftingDone()) {

            leftCoachFragment.hideMultiHopLayout()
            mergeBusSharedViewModel.removeNonPairedSeatsFromHashMap()

            //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            //mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(null, null)


            /*if(mergeBusSharedViewModel.currentSelectedSeat.value != null) {
                mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(mergeBusSharedViewModel.currentSelectedSeat.value!!)
            }*/

            if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                callMergeServiceDetailsRightCoachApi()
            } else {
                mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            }



        } else {
            requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
        }
    }

    override fun onCancelButtonClick() {

        /*var flag = true
        kotlin.run runBlock@{

            mergeBusSharedViewModel.samePNRSeatModelMediatorLiveData.value?.forEach { samePNRSeatModel ->

                mergeBusSharedViewModel.currentMultiHopArrayLiveData.value?.forEach { multiHopSeatDetail ->
                    if (multiHopSeatDetail.pnr?.equals(samePNRSeatModel.pnr) == true) {
                        if(samePNRSeatModel.seatShiftList.any { seatShiftModel ->
                                seatShiftModel.newSeat == null
                            }) {
                            flag = false
                            return@runBlock
                        }
                    }
                }
            }
        }*/

        if (!mergeBusSharedViewModel.isPartialShiftingDone()) {

            leftCoachFragment.hideMultiHopLayout()
            mergeBusSharedViewModel.removeNonPairedSeatsFromHashMap()

            //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            //mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(null, null)


            /*if(mergeBusSharedViewModel.currentSelectedSeat.value != null) {
                mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach(mergeBusSharedViewModel.currentSelectedSeat.value!!)
            }*/

            if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                callMergeServiceDetailsRightCoachApi()
            } else {
                mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            }



        } else {
            requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
        }

        /*if (!mergeBusSharedViewModel.isPartialShiftingDone()) {
            *//*if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                callMergeServiceDetailsRightCoachApi()
            } else {
                mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            }*//*

            leftCoachFragment.hideMultiHopLayout()

            mergeBusSharedViewModel.removeNonPairedSeatsFromHashMap()

            mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
            mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(null, null)

        } else {
            requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
        }*/
    }

    override fun onServiceDetailsClicked(
        fragmentPosition: Int,
        serviceDetailsModel: ServiceDetailsModel?
    ) {

        serviceDetailsModel?.body?.let {
            binding.bottomSheetServiceDetails.serviceNameTV.text =
                "${getString(R.string.service_details)} - ${it.number}"
            binding.bottomSheetServiceDetails.bookedSeats.text =
                it.coachDetails?.bookedSeatsByUser ?: ""
            binding.bottomSheetServiceDetails.sourceTV.text = it.origin?.name ?: ""
            binding.bottomSheetServiceDetails.destinationTV.text = it.destination?.name ?: ""
            binding.bottomSheetServiceDetails.seatsAvailable.text =
                (it.availableSeats ?: "").toString()
        }
        val bottomSheetBehaviorServiceDetails =
            BottomSheetBehavior.from(binding.bottomSheetServiceDetails.bottomSheet)
        bottomSheetBehaviorServiceDetails.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehaviorServiceDetails.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        binding.bottomSheetServiceDetails.btnOk.setOnClickListener {
            bottomSheetBehaviorServiceDetails.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun callLeftCoachServiceDetailsApi() {
        if (requireContext().isNetworkAvailable()) {
            showProgressBar()
            mergeBusSharedViewModel.getServiceDetailsLeftCoach(
                reservationId = reservationId,
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                originId = originId,
                destinationId = destinationId,
                operatorApiKey = operator_api_key,
                locale = mergeBusSharedViewModel.locale.value ?: "en",
                excludePassengerDetails = false
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun callMergeServiceDetailsRightCoachApi(
        originId: String = rightCoachExactRouteService.originId ?: "",
        destinationId: String = rightCoachExactRouteService.destinationId ?: ""
    ) {
        if (requireContext().isNetworkAvailable()) {
            binding.progressBar.visible()

            val mergeServiceDetailsRequest = MergeServiceDetailsRequest(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                resId = rightCoachExactRouteService.reservationId,
                originId = originId,
                destinationId = destinationId,
                excludePassengerDetails = excludePassengerDetails,
                locale = mergeBusSharedViewModel.locale.value ?: "en"
            )

            mergeBusSharedViewModel.getMergeServiceDetailsRightCoach(
                mergeServiceDetailsRequest
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun setUpObserver() {

        mergeBusSharedViewModel.serviceDetailsLeftCoach.observe(requireActivity()) {

            hideProgressBar()

            if (it != null) {
                when (it.code) {
                    200 -> {
                        leftCoachFragment =
                            CoachFragment.newInstance(Const.FRAGMENT_LEFT, it, "#2043C2")

                        addFragment(leftCoachFragment, R.id.fragmentContainerLeft)

                        /*mergeBusSharedViewModel.generateSeatList(
                            it.body?.coachDetails?.seatDetails ?: mutableListOf()
                        )*/
                        if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.code == null) {
                            if (requireContext().isNetworkAvailable()) {
                                callMergeServiceDetailsRightCoachApi()
                            } else {
                                requireContext().noNetworkToast()
                            }
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message)
                    }
                }
            }


        }

        mergeBusSharedViewModel.mergeServiceDetailsRightCoach.observe(requireActivity()) {

            hideProgressBar()

            if (it != null) {
                if (it.code == 200) {

                    binding.doubleArrow.visible()

                    setupToolbar()

                    /*var selectedLegendColor: String? = null

                    mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.legendDetails?.forEach {
                        if(it.colorLegend.toString() == "Selected") { //Don't move this text to string file because it is crating issue. The if condition always remains false and therefore the selectedLegendColor always remains null.
                            selectedLegendColor = it.color
                        }
                    }*/

                    rightCoachFragment = CoachFragment.newInstance(
                        Const.FRAGMENT_RIGHT,
                        it,
                        "#2043C2"
                    )
                    addFragment(rightCoachFragment, R.id.fragmentContainerRight)
                    mergeBusSharedViewModel.setIsRightCoachServiceApiCalled(true)

                    if (mergeBusSharedViewModel.isSeatListGeneratedForFirstTime.value == false) {
                        mergeBusSharedViewModel.generateSeatListNew(
                            mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.coachDetails?.seatDetails
                                ?: mutableListOf()
                        )

                        mergeBusSharedViewModel.setIsSeatListGeneratedForFirstTime(true)
                    }


                    /*Handler(Looper.getMainLooper()).postDelayed( {
                        //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
                        if(leftCoachFragment.isMultiHopLayoutVisible()) {
                            mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()

                        }
                    }, 500)*/
                    if (::leftCoachFragment.isInitialized) {
                        leftCoachFragment.showToolTipForServiceName()
                    }

/*
                    Handler(Looper.getMainLooper()).postDelayed( {
                        //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
                        if (mergeBusSharedViewModel.recommendedSeatsRightCoach.value != null) {
                            val response = mergeBusSharedViewModel.recommendedSeatsRightCoach.value
                            if (response?.seats?.isNotEmpty() == true) {
                                val recommendedSeatsList = mutableListOf<String>()
                                response.seats.forEach { seat ->
                                    seat?.recommendedSeats?.forEach {
                                        if (it != null) {
                                            recommendedSeatsList.add(it)
                                        }
                                    }

                                }
                                rightCoachFragment.setRecommendedSeats(recommendedSeatsList)
                            }

                            mergeBusSharedViewModel.clearRecommendedSeatResponse()
                        }
                    }, 500)
*/


                } else if (it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else {
                    requireContext().toast(it.message ?: "")
                }

            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        mergeBusSharedViewModel.recommendedSeatsRightCoach.observe(requireActivity()) {

            hideProgressBar()

            if (it != null) {
                if (it.code == 200) {
                    if (::rightCoachFragment.isInitialized) {
                        /*if (it.seats?.isNotEmpty() == true) {
                            val recommendedSeatsList = mutableListOf<String>()
                            it.seats.forEach { seat ->
                                seat?.recommendedSeats?.forEach {
                                    if (it != null) {
                                        recommendedSeatsList.add(it)
                                    }
                                }

                            }
                            rightCoachFragment.setRecommendedSeats(recommendedSeatsList)
                        }*/

                        if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {
                            callMergeServiceDetailsRightCoachApi(
                                originId = mergeBusSharedViewModel.currentSelectedSeat.value?.passengerDetails?.originId.toString(),
                                destinationId = mergeBusSharedViewModel.currentSelectedSeat.value?.passengerDetails?.destinationId.toString()
                            )
                        }

                    }
                } else if (it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else if (it.code == 404) {
                    showAddBpDpPopup(it.message)

                } else {
                    requireContext().toast(it.message ?: "")
                    clearLastSelectedSeatSelection()
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                clearLastSelectedSeatSelection()
            }
        }

        mergeBusSharedViewModel.selectSpecificSeatLeftCoach.observe(requireActivity()) {
            if (::leftCoachFragment.isInitialized) {
                if (it != null) {
                    leftCoachFragment.selectSeat(it)
                }
            }
        }


        mergeBusSharedViewModel.selectSpecificSeatRightCoach.observe(requireActivity()) {
            if (::rightCoachFragment.isInitialized) {
                if (it != null) {
                    rightCoachFragment.selectSeat(it)
                }
            }
        }

        mergeBusSharedViewModel.deSelectSpecificSeatLeftCoach.observe(requireActivity()) {
            if (::leftCoachFragment.isInitialized) {
                if (it != null) {
                    leftCoachFragment.deSelectSeat(it)
                }
            }
        }

        mergeBusSharedViewModel.deSelectSpecificSeatRightCoach.observe(requireActivity()) {
            if (::rightCoachFragment.isInitialized) {
                if (it != null) {
                    rightCoachFragment.deSelectSeat(it)
                }
            }
        }


        mergeBusSharedViewModel.addBpDpToService.observe(requireActivity()) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        context?.toast(it.message)
                        if(mergeBusSharedViewModel.currentSelectedSeat.value != null) {
                            callRecommendedSeatsRightCoachApi(mergeBusSharedViewModel.currentSelectedSeat.value!!)
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message ?: "")
                        clearLastSelectedSeatSelection()
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
                clearLastSelectedSeatSelection()
            }

        }

        mergeBusSharedViewModel.samePNRSeatModelMediatorLiveData.observe(requireActivity()) {
            val ticketShiftedToAdapter = TicketShiftedToAdapter(requireContext(), it)
            binding.bottomSheet.rvTicketShiftedTo.adapter = ticketShiftedToAdapter

            var bookedSeatsCount = 0
            var shiftedSeatsCount = 0
            it.forEach { samePNRSeatModel ->
                bookedSeatsCount += samePNRSeatModel.seatShiftList.size
                samePNRSeatModel.seatShiftList.forEach { seatShiftModel ->
                    if (seatShiftModel.newSeat != null) {
                        shiftedSeatsCount += 1
                    }
                }
            }
            val tvSeatsShiftedToText =
                getString(
                    R.string.all_seats_shifted_total_shifted_to_service_number,
                    shiftedSeatsCount,
                    bookedSeatsCount,
                    mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.number
                )
            binding.bottomSheet.tvSeatsShiftedTo.text = tvSeatsShiftedToText
            binding.mergeBusCoachToolBar.selectedTV.text =
                "$shiftedSeatsCount/$bookedSeatsCount ${getString(R.string.selected)}"

            /*it.forEach { samePNRSeatModel ->
                samePNRSeatModel.seatShiftList.forEach { seatShiftModel ->

                    mergeBusSharedViewModel.currentMultiHopArray.value?.forEach { multiHopSeatDetail ->
                        multiHopSeatDetail.seat_details?.forEach { seatDetail ->
                            if (seatDetail.number?.equals(seatShiftModel.oldSeat.number) == true) {

                            }

                        }
                    }
                }
            }*/

            var showDoneButton = false

            kotlin.run runBlock@{

                it.forEach { samePNRSeatModel ->

                    mergeBusSharedViewModel.currentMultiHopArrayLiveData.value?.forEach { multiHopSeatDetail ->
                        if (multiHopSeatDetail.pnr?.equals(samePNRSeatModel.pnr) == true) {
                            if (samePNRSeatModel.seatShiftList.all { seatShiftModel ->
                                    seatShiftModel.newSeat != null
                                }) {
                                showDoneButton = true
                                return@runBlock
                            }
                        }
                    }
                }
            }

            mergeBusSharedViewModel.toggleDoneAndGoBackButtonVisibility(showDoneButton)

            /*kotlin.run runBlock@{
                mergeBusSharedViewModel.currentMultiHopArray.value?.forEach { multiHopSeatDetail ->
                    multiHopSeatDetail.seat_details?.forEach { seatDetail ->
                        mergeBusSharedViewModel.shiftSeatsHashMapLiveData.value?.forEach { mapItem ->
                            if (mapItem.key.number?.equals(seatDetail.number) == true) {
                                if (mapItem.key.isSelected && mapItem.value != null) {
                                    mergeBusSharedViewModel.toggleDoneAndGoBackButtonVisibility(true)
                                } else {
                                    mergeBusSharedViewModel.toggleDoneAndGoBackButtonVisibility(false)
                                    return@runBlock
                                }
                            }
                        }
                    }
                }
            }*/
        }

        mergeBusSharedViewModel.toggleDoneAndGoBackButtonVisibility.observe(requireActivity()) {
            leftCoachFragment.toggleDoneAndGoBackButtonVisibility(it)
        }

        mergeBusSharedViewModel.drawBorderAroundPNRGroupLeftCoach.observe(requireActivity()) {
            if (::leftCoachFragment.isInitialized) {
                if (it != null) {
                    leftCoachFragment.setBorderAroundSelectedPNRGroup(it.passengerDetails?.ticketNo, it)
                }
            }
        }

        mergeBusSharedViewModel.toastMessage.observe(requireActivity()) {
            requireContext().toast(it)
        }

        mergeBusSharedViewModel.mergeBusSeatMappingRedirection.observe(requireActivity()) {

            hideProgressBar()

            it?.getContentIfNotHandled()?.let {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            findNavController().navigate(R.id.action_mergeCoachFragment_to_reviewSeatShiftingFragment)
                        }

                        401 -> {
                            (activity as BaseActivity).showUnauthorisedDialog()
                        }

                        else -> {
                            requireContext().toast(it.message)
                        }
                    }
                } else {
                    requireContext().toast(getString(R.string.server_error))
                }
            }

        }


        mergeBusSharedViewModel.removeSelectedPNRGroupBorderLeftCoach.observe(requireActivity()) {
            if (::leftCoachFragment.isInitialized) {
                leftCoachFragment.removeBorderAroundSelectedPNRGroup()
            }
        }

        mergeBusSharedViewModel.multistationSeatData.observe(requireActivity()) {
            if (it != null) {
                when (it.code) {
                    200 -> {
                        if (::leftCoachFragment.isInitialized) {
                            mergeBusSharedViewModel.updateSampleData()
                            mergeBusSharedViewModel.addSeatNumberToMultiHopSeatNumberApiCalledList(
                                it.body.selected_seat_number
                            )
                            mergeBusSharedViewModel.setCurrentMultiHopArray(it.body.multi_hop_seat_detail)
                            leftCoachFragment.setMultiHopAdapter(
                                mergeBusSharedViewModel.currentMultiHopArrayLiveData.value
                                    ?: mutableListOf()
                            )

                            if (mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.isMultihopEnable == true) {

                                callMergeServiceDetailsRightCoachApi(
                                    originId = it.body.multi_hop_seat_detail.get(0).seat_details?.get(0)?.passengerDetails?.originId.toString(),
                                    destinationId = it.body.multi_hop_seat_detail.get(0).seat_details?.get(0)?.passengerDetails?.destinationId.toString(),
                                )
                                //mergeBusSharedViewModel.checkForPreviouslyShiftedSeatsAndUpdateUI()
                            }
                        }
                    }

                    401 -> {
                        (activity as BaseActivity).showUnauthorisedDialog()
                    }

                    else -> {
                        requireContext().toast(it.message)
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        mergeBusSharedViewModel.callMultistationApi.observe(requireActivity()) {
            if (it?.isNotEmpty() == true) {
                callMultiStationSeatDetailApi(
                    reservationId = mergeBusSharedViewModel.reservationIdLeftCoach.value ?: "",
                    seatNumber = it
                )
            }

        }
    }

    private fun showAddBpDpPopup(message: String?) {
        DialogUtils().dialogAddBpDpValidation(context = requireContext(),
            title = requireContext().getString(R.string.different_bp_dp),
            message = message ?: "",
            addBpDpCallBack = { pnrNumber ->
                addBpDpDialog()
            },
            onCancelClick = {
                requireContext().toast("Previous selected seats will be deselected")
                clearLastSelectedSeatSelection()
            }
        )
    }

    private fun addBpDpDialog() {
        DialogUtils().dialogAddBpDp(
            context = requireContext(),
            title = "${
                requireContext().getString(
                    R.string.add_new_bp_dp_for
                )
            } ${mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.number}",
            hitAddBpDpApi = { pnrNumber, boardingTime, droppingTime ->
                addBpDpApi(boardingTime, droppingTime)
            },
            mergeBusSharedViewModel.recommendedSeatsRightCoach.value,
            onCancelClick = {
                requireContext().toast("Previous selected seats will be deselected")
                clearLastSelectedSeatSelection()
            }
        )
    }

    private fun clearLastSelectedSeatSelection() {
        mergeBusSharedViewModel.deSelectAllSeatsWithSamePNR(mergeBusSharedViewModel.currentSelectedPNR.value)
        deSelectLastSelectedSeatMultiHop()
    }
    private fun deSelectLastSelectedSeatMultiHop() {
        if (leftCoachFragment.isMultiHopLayoutVisible()) {
            if (mergeBusSharedViewModel.currentSelectedSeat.value != null) {
                mergeBusSharedViewModel.deSelectAllSeatsWithSamePNRMultiHop(
                    mergeBusSharedViewModel.currentSelectedSeat.value!!
                )

                leftCoachFragment.setMultiHopAdapter(
                    mergeBusSharedViewModel.currentMultiHopArrayLiveData.value ?: mutableListOf()
                )
            }
        }
    }

    private fun addBpDpApi(boardingTime: String, droppingTime: String) {
        if (requireContext().isNetworkAvailable()) {
            val recommendedSeatsRequest = AddBpDpToServiceRequest(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                resId = rightCoachExactRouteService.reservationId,
                pnr_number = mergeBusSharedViewModel.currentSelectedSeat.value?.passengerDetails?.ticketNo ?: "",
                boardingTime = boardingTime,
                dept_time = droppingTime,
            )

            mergeBusSharedViewModel.addBpDpToService(
                recommendedSeatsRequest
            )
        }
    }


    override fun onResume() {
        super.onResume()

        getRemovedSeatDetails()
    }

    private fun getRemovedSeatDetails() {
        if (mergeBusSharedViewModel.removedPassengersList.value?.isNotEmpty() == true) {
            mergeBusSharedViewModel.removedPassengersList.value?.forEach { removedLeftSideSeatNumber ->

                var seat: SeatDetail? = null
                mergeBusSharedViewModel._shiftSeatsHashMap.forEach {
                    if (it.key.number.equals(removedLeftSideSeatNumber, true)) {
                        seat = it.key
                        return@forEach
                    }
                }

                if (seat != null) {
                    mergeBusSharedViewModel.setCurrentSelectedSeatAndPNR(
                        removedLeftSideSeatNumber,
                        seat!!
                    )

                    mergeBusSharedViewModel.deSelectAllSeatsWithSamePNR(mergeBusSharedViewModel.currentSelectedPNR.value)
                }

            }

        }

    }

    private fun callRecommendedSeatsRightCoachApi(seatDetail: SeatDetail) {
        if (requireContext().isNetworkAvailable()) {

            showProgressBar()

            val recommendedSeatsRequest = RecommendedSeatsRequest(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                resId = rightCoachExactRouteService.reservationId,
                pnr = seatDetail.passengerDetails?.ticketNo ?: "",
                originId = rightCoachExactRouteService.originId,
                destinationId = rightCoachExactRouteService.destinationId,
                excludePassengerDetails = excludePassengerDetails,
                locale = mergeBusSharedViewModel.locale.value ?: "en"
            )

            mergeBusSharedViewModel.getMergeBusRecommendedSeats(
                recommendedSeatsRequest
            )
        } else {
            requireContext().noNetworkToast()
        }
    }

    private fun callSeatMappingApi() {
        if (requireContext().isNetworkAvailable()) {

            if (mergeBusSharedViewModel.isPartialShiftingDone()) {
                requireContext().toast(getString(R.string.partial_shifting_is_not_allowed))
                return
            }

            val seatShiftMapList = mergeBusSharedViewModel.getSeatShiftMapForSeatMappingApi()

            if (seatShiftMapList.isEmpty()) {
                requireContext().toast(getString(R.string.either_a_pnr_group_is_partially_shifted_or_none_of_the_pnr_group_is_shifted))
                return
            }


            val mergeBusSeatMappingRequest = MergeBusSeatMappingRequest(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                newResId = rightCoachExactRouteService.reservationId?.toInt(),
                oldResId = reservationId.toInt(),
                seatShiftMap = seatShiftMapList
            )

            mergeBusSharedViewModel.mergeBusSeatMapping(mergeBusSeatMappingRequest)

            showProgressBar()

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

    private fun callMultiStationSeatDetailApi(reservationId: String, seatNumber: String) {
        if (requireContext().isNetworkAvailable()) {
            mergeBusSharedViewModel.multistationPassengerDataApi(
                apiKey = mergeBusSharedViewModel.loginModel.value?.api_key ?: "",
                reservationId = reservationId,
                seatNumber = seatNumber,
                isBima = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.isBima
                    ?: false,
                apiType = multistation_seat_details_api
            )
        } else {
            requireContext().noNetworkToast()
        }
    }


}