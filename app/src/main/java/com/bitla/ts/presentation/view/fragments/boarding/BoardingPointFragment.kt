package com.bitla.ts.presentation.view.fragments.boarding

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.FragmentListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.FragmentBoardingPointBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.presentation.adapter.BoardingListAdapter
import com.bitla.ts.utils.common.droppingPointList
import com.bitla.ts.utils.common.getPrivilegeDataFromFile
import com.bitla.ts.utils.sharedPref.PREF_BOARDING_STAGE_DETAILS
import com.bitla.ts.utils.sharedPref.PREF_PICKUP_DROPOFF_CHARGES_ENABLED
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import gone
import onChange
import timber.log.Timber
import visible
import java.util.*
import kotlin.collections.ArrayList


class BoardingPointFragment : Fragment(),
    OnItemClickListener {

    companion object {
        val tag: String = BoardingPointFragment::class.java.simpleName
        private var boardingList = arrayListOf<StageDetail>()
        private var droppingList = mutableListOf<StageDetail>()
        private var viewpagerBoardingDestination: ViewPager? = null

        fun newInstance(bundle: Bundle, viewPager: ViewPager): BoardingPointFragment {
            val fragment = BoardingPointFragment()
            try {

                boardingList = bundle.getSerializable("boardingPointsList") as ArrayList<StageDetail>
                droppingList = bundle.getSerializable("droppingPointsList") as ArrayList<StageDetail>

                if (droppingList.isNotEmpty()) {
                    val stage: StageDetail = boardingList[0]
                    Timber.d("stageId-new ${droppingList.size}")
                    //setBoardingPoint(stage)
                }

                viewpagerBoardingDestination = viewPager
            } catch (e: Exception) {
                Timber.d("exceptionMessage ${e.message}")
            }
            return fragment
        }
    }

    private lateinit var binding: FragmentBoardingPointBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var boardingListAdapter: BoardingListAdapter
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private lateinit var fragmentListener: FragmentListener
    private var filterdNames = mutableListOf<StageDetail>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentListener = activity as FragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Error in retrieving data. Please try again")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentListener.sendData(0)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBoardingPointBinding.inflate(inflater, container, false)
//            initTab()
        binding.headerText.text = getString(R.string.select_boarding_point).uppercase(Locale.getDefault())
        setMyBookingsAdapter()
        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

        binding.etSearch.onChange { text ->
            boardingListAdapter.filter.filter(text)
            if (boardingListAdapter.itemCount == 0) {
                binding.clPickupAddress.gone()
                binding.btnPickupNext.gone()
            } else {
                binding.clPickupAddress.visible()
                binding.btnPickupNext.visible()
            }
        }

        return binding.root
    }

    private fun setMyBookingsAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvselectboardingPoint.layoutManager = layoutManager
        boardingListAdapter = BoardingListAdapter(requireActivity(), this)
        boardingListAdapter.addData(boardingList)
        binding.rvselectboardingPoint.adapter = boardingListAdapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == BoardingListAdapter.tag) {
                if (boardingList.isNotEmpty()) {

                    var index = boardingList.indexOfFirst {
                        it.id == position
                    }
                    val isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false)
                    val pickupChargesList = boardingList.map { it.pickupCharge }

                    if (pickupChargesList.isNotEmpty() && isPickupDropoffChargesEnabled == true && privilegeResponseModel?.country.equals("Vietnam")) {
                        binding.clPickupAddress.visible()
                        if (!boardingList[index].distance.isNullOrEmpty()) {
                            binding.tvPickupAddressNote.text = getString(
                                    R.string.address_note,
                                    getString(R.string.pickup_address),
                                    boardingList[index].distance
                                )
                        } else {
                            binding.tvPickupAddressNote.text = getString(R.string.empty)
                        }
                        binding.btnPickupNext.visible()
                    } else {
                        binding.clPickupAddress.gone()
                        binding.btnPickupNext.gone()
                    }

                    if (binding.btnPickupNext.isVisible) {
                        binding.btnPickupNext.setOnClickListener {
                            fragmentListener.sendData(index)
                            fragmentListener.sendPickupDropOffDetails(
                                binding.etPickupAddress.text?.toString()?.trim() ?: "",
                                boardingList[index].pickupCharge ?: "0.0",
                                BoardingPointFragment.tag
                            )
                            fragmentListener.selectedPoint(
                                boardingList[index].name!!,
                                BoardingPointFragment.tag,
                                boardingList[index].id.toString()
                            )

                            binding.etPickupAddress.addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable) {
                                    fragmentListener.sendPickupDropOffDetails(
                                        binding.etPickupAddress.text?.toString()?.trim() ?: "",
                                        boardingList[index].pickupCharge ?: "0.0",
                                        BoardingPointFragment.tag
                                    )
                                }

                                override fun beforeTextChanged(
                                    s: CharSequence, start: Int, count: Int, after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence, start: Int, before: Int, count: Int
                                ) {
                                }
                            })

                            if (droppingList.size >= 1) {
                                viewpagerBoardingDestination?.currentItem = 1
                            }
                        }
                    } else {
                        fragmentListener.sendData(index)
                        fragmentListener.selectedPoint(
                            boardingList[index].name!!,
                            BoardingPointFragment.tag,
                            boardingList[index].id.toString()
                        )

                        // to automatic open "dropping point"
                        if (droppingList.size > 1) {
                            viewpagerBoardingDestination?.currentItem = 1
                        }
                    }

                    PreferenceUtils.putObject(boardingList[index], PREF_BOARDING_STAGE_DETAILS)
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {

        filterdNames = mutableListOf()
        for (s in boardingList) {

            if (s.name != null && s.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filterdNames.add(s)
            }
        }
        if (::boardingListAdapter.isInitialized) boardingListAdapter.addData(filterdNames)
    }
}