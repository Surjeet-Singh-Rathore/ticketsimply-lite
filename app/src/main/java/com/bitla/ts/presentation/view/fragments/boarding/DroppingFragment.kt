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
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.FragmentListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.FragmentDroppingBinding
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.domain.pojo.service_details_response.StageDetail
import com.bitla.ts.presentation.adapter.BoardingListAdapter
import com.bitla.ts.utils.sharedPref.*
import gone
import onChange
import timber.log.Timber
import visible
import java.util.*
import kotlin.collections.ArrayList


class DroppingFragment : Fragment(), OnItemClickListener {

    companion object {
        val tag: String = DroppingFragment::class.java.simpleName
        var droppings = mutableListOf<StageDetail>()

        fun newInstance(bundle: Bundle): DroppingFragment {
            val fragment = DroppingFragment()
            droppings = bundle.getSerializable("droppingPointsList") as ArrayList<StageDetail>
            return fragment
        }
    }

    private lateinit var binding: FragmentDroppingBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var droppingListAdapter: BoardingListAdapter
    private lateinit var fragmentListener: FragmentListener
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var droppingList = mutableListOf<StageDetail>()
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
        droppingList = droppings
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDroppingBinding.inflate(inflater, container, false)
        binding.headerText.text = getString(R.string.select_dropping_point).uppercase(Locale.getDefault())
        setMyBookingsAdapter()
        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()

        binding.etSearch.onChange { text ->
            droppingListAdapter.filter.filter(text)
            if (droppingListAdapter.itemCount == 0) {
                binding.clDropoffAddress.gone()
                binding.btnDropoffNext.gone()
            } else {
                binding.clDropoffAddress.visible()
                binding.btnDropoffNext.visible()
            }
        }


        return binding.root
    }

    private fun setMyBookingsAdapter() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectDroppingPoint.layoutManager = layoutManager
        droppingListAdapter = BoardingListAdapter(requireActivity(), this)
        droppingListAdapter.addData(droppingList)
        binding.rvSelectDroppingPoint.adapter = droppingListAdapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == BoardingListAdapter.tag) {
                var index = droppingList.indexOfFirst {
                    it.id == position
                }
                val isPickupDropoffChargesEnabled = PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false)
                val dropoffChargesList = droppingList.map { it.dropoffCharge }

                if (dropoffChargesList.isNotEmpty() && isPickupDropoffChargesEnabled == true && privilegeResponseModel?.country.equals("Vietnam")) {
                    binding.clDropoffAddress.visible()
                    if (!droppingList[index].distance.isNullOrEmpty()) {
                        binding.tvDropoffAddressNote.text = getString(
                                R.string.address_note,
                                getString(R.string.dropoff_address),
                                droppingList[index].distance
                            )
                    } else {
                        binding.tvDropoffAddressNote.text = getString(R.string.empty)
                    }
                    binding.btnDropoffNext.visible()
                } else {
                    binding.clDropoffAddress.gone()
                    binding.btnDropoffNext.gone()
                }

                if (binding.btnDropoffNext.isVisible) {
                    binding.btnDropoffNext.setOnClickListener{
                        fragmentListener.sendPickupDropOffDetails(
                            binding.etDropoffAddress.text?.toString()?.trim() ?: "",
                            droppingList[index].dropoffCharge ?: "0.0",
                            DroppingFragment.tag
                        )
                        fragmentListener.selectedPoint(
                            droppingList[index].name!!,
                            DroppingFragment.tag,
                            droppingList[index].id.toString()
                        )
                    }
                } else {
                    fragmentListener.selectedPoint(
                        droppingList[index].name!!,
                        DroppingFragment.tag,
                        droppingList[index].id.toString()
                    )
                }

                if (droppingList.isNotEmpty()) {
                    PreferenceUtils.putObject(droppingList[index], PREF_DROPPING_STAGE_DETAILS)
                }
            }
        }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    @SuppressLint("DefaultLocale")
    private fun search(text: String) {

        filterdNames = mutableListOf()
        for (s in droppings) {

            if (s.name != null && s.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filterdNames.add(s)
            }
        }
        if (::droppingListAdapter.isInitialized) droppingListAdapter.addData(filterdNames)
    }

}