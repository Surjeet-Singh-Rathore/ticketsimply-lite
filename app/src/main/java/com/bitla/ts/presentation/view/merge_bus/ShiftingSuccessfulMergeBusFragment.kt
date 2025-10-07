package com.bitla.ts.presentation.view.merge_bus

import android.content.Intent
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.activate_deactivate_service
import com.bitla.ts.databinding.FragmentShiftingSuccessfullMergeBusBinding
import com.bitla.ts.domain.pojo.activate_deactivate_service.request.ActivateDeactivateServiceRequest
import com.bitla.ts.presentation.view.activity.OccupancyGridActivity
import com.bitla.ts.presentation.view.merge_bus.adapter.ShiftedSeatsAdapter
import com.bitla.ts.presentation.viewModel.MergeBusSharedViewModel
import com.bitla.ts.presentation.viewModel.OccupancyGridViewModel
import com.bitla.ts.utils.constants.RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE
import com.bitla.ts.utils.constants.SELECT_SERVICE_INTENT_REQUEST_CODE
import com.bitla.ts.utils.dialog.DialogUtils
import gone
import isNetworkAvailable
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class ShiftingSuccessfulMergeBusFragment : Fragment() {

    private lateinit var binding: FragmentShiftingSuccessfullMergeBusBinding
    private val mergeBusSharedViewModel by sharedViewModel<MergeBusSharedViewModel>()
    private val occupancyGridViewModel by viewModel<OccupancyGridViewModel<Any?>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShiftingSuccessfullMergeBusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setOnclickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    private fun setOnclickListeners() {
        binding.llGoToOccupancyDashboard.setOnClickListener {
            gotoOccupancyDashboard()
        }

        binding.tvShiftRemainingPassengers.setOnClickListener {
            //findNavController().navigate(R.id.action_successSeatShiftingFragment_to_mergeCoachFragment)
            restartActivity()
        }
    }

    private fun gotoOccupancyDashboard() {
        //activity?.finish()
        val intent = Intent()
        requireActivity().setResult(RESTART_OCCUPANCY_GRID_ACTIVITY_REQUEST_CODE, intent)
        requireActivity().finish()
    }

    private fun initUI() {

        mergeBusSharedViewModel.privileges = (activity as BaseActivity).getPrivilegeBase()

        binding.toolbarPassengerDetails.imgBack.gone()
        binding.toolbarPassengerDetails.toolbarHeaderText.text =
            getString(R.string.shifting_successful)
        binding.toolbarPassengerDetails.toolbarSubtitle.text = "${getString(R.string.travel_date_colon)} ${mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.travelDate}"
        binding.toolbarPassengerDetails.toolbarTicketSuccessContent.text =
            getString(R.string.seats_shifted_successfully)

        binding.toolbarPassengerDetails.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dark_blue
            )
        )
        binding.toolbarPassengerDetails.toolbarTicketSuccessContent.textSize = 18f
        binding.toolbarPassengerDetails.toolbarTicketSuccessContent.setTypeface(
            binding.toolbarPassengerDetails.toolbarTicketSuccessContent.getTypeface(),
            BOLD
        )

        val fromServiceNumber = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.number
        val fromServiceOrigin = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.origin?.name
        val fromServiceDestination = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.destination?.name
        val fromServiceDate = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.travelDate
        val fromServiceTime = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.depTime
        val fromServiceText = "$fromServiceNumber $fromServiceOrigin - $fromServiceDestination"

        val toServiceNumber = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.number
        val toServiceOrigin = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.origin?.name
        val toServiceDestination = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.destination?.name
        val toServiceDate = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.travelDate
        val toServiceTime = mergeBusSharedViewModel.mergeServiceDetailsRightCoach.value?.body?.depTime
        val toServiceText = "$toServiceNumber $toServiceOrigin - $toServiceDestination"

        binding.oldServiceNumber.text = fromServiceText
        binding.newServiceNumber.text = toServiceText

        binding.tvShiftDateTime.text = "$fromServiceDate ${requireContext().getString(R.string.at)} $fromServiceTime"
        binding.tvShiftDateTimeNew.text = "$toServiceDate ${requireContext().getString(R.string.at)} $toServiceTime"

        binding.tvShiftRemainingPassengers.text=requireContext()?.getString(R.string.shift_remaining_passengers)+" "+("("+ mergeBusSharedViewModel.mergeBusShiftPassenger.value?.remainingSeatCounts.toString()+")")

        setShiftedSeatsAdapter()

        binding.cancelService.setOnClickListener {
            showCancelServiceDialog()
        }

        setUpObserver()

    }

    private fun setShiftedSeatsAdapter() {

        val adapter = ShiftedSeatsAdapter(
            requireContext(),
            mergeBusSharedViewModel.mergeBusShiftPassenger.value?.shiftedSeats ?: mutableListOf()
        )

        binding.shiftRV.adapter = adapter
    }

    private fun showCancelServiceDialog() {
        DialogUtils.dialogCancelService(
            context = requireContext(),
            serviceName = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.number ?: "",
        ) {
            callActivateDeactivateServicesApi(mergeBusSharedViewModel.reservationIdLeftCoach.value?.toLong())
        }
    }

    private fun setUpObserver() {
        mergeBusSharedViewModel.mergeBusShiftPassenger.observe(viewLifecycleOwner) {

            hideProgressBar()

            if(it != null) {
                if(it.code == 200) {
                    if(it.notShifted.isNullOrEmpty() || it.notShifted.split(",").isEmpty()) {
                        binding.cancelService.visible()
                        binding.tvShiftRemainingPassengers.gone()
                    } else {
                        binding.cancelService.gone()
                        binding.tvShiftRemainingPassengers.visible()
                    }
                    setShiftedSeatsAdapter()
                } else if(it.code == 401) {
                    (activity as BaseActivity).showUnauthorisedDialog()
                } else {
                    requireContext().toast(it.message)
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }

        occupancyGridViewModel.activateDeactivateService.observe(viewLifecycleOwner) {

            hideProgressBar()

            if (it != null) {

                when (it.code) {
                    200 -> {

                        DialogUtils.dialogServiceCancelled(
                            context = requireContext(),
                            serviceName = mergeBusSharedViewModel.serviceDetailsLeftCoach.value?.body?.number ?: ""
                        ) {
                           gotoOccupancyDashboard()
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

    }

    private fun callActivateDeactivateServicesApi(serviceId: Long?) {
        if(requireContext().isNetworkAvailable()) {

            showProgressBar()

            val serviceItem: com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service =
                com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service(
                    serviceId = serviceId ?: 0,
                    activate = false
                )

            val serviceList =
                mutableListOf<com.bitla.ts.domain.pojo.activate_deactivate_service.request.Service?>()
            serviceList.add(serviceItem)

            val activateDeactivateServiceRequest = ActivateDeactivateServiceRequest(
                serviceList = serviceList
            )

            occupancyGridViewModel.activateDeactivateServiceApi(
                apikey = mergeBusSharedViewModel.loginModel.value?.api_key,
                activateDeactivateServiceRequest = activateDeactivateServiceRequest,
                apiType = activate_deactivate_service
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

    private fun restartActivity() {
        val intent = requireActivity().intent
        startActivity(intent)
        requireActivity().finish()
    }
}