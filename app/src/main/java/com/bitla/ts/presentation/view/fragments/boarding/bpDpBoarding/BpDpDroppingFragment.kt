package com.bitla.ts.presentation.view.fragments.boarding.bpDpBoarding

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
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.BpDPDroppingListAdapter
import com.bitla.ts.utils.sharedPref.*
import gone
import onChange
import timber.log.Timber
import visible
import java.util.*
import kotlin.collections.ArrayList


class BpDpDroppingFragment : Fragment(), OnItemClickListener {

    companion object {
        val tag: String = BpDpDroppingFragment::class.java.simpleName
        var droppings = mutableListOf<DropOffDetail>()
        var sourceKey = ""

        fun newInstance(bundle: Bundle): BpDpDroppingFragment {
            val fragment = BpDpDroppingFragment()
            droppings = bundle.getSerializable("droppingPointsList") as ArrayList<DropOffDetail>
            sourceKey = bundle.getString("sourceKey") ?: ""
            return fragment
        }
    }

    private lateinit var binding: FragmentDroppingBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var droppingListAdapter: BpDPDroppingListAdapter
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private lateinit var fragmentListener: FragmentListener
    private var droppingList = mutableListOf<DropOffDetail>()

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
        droppingListAdapter =
            BpDPDroppingListAdapter(requireActivity(), droppingList, this)
        binding.rvSelectDroppingPoint.adapter = droppingListAdapter
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
        if (view.tag != null) {
            if (view.tag == BpDPDroppingListAdapter.tag) {
                var index = droppingList.indexOfFirst {
                    it.id.toInt() == position
                }
                val isPickupDropoffChargesEnabled =
                    PreferenceUtils.getPreference(PREF_PICKUP_DROPOFF_CHARGES_ENABLED, false)

                if (isPickupDropoffChargesEnabled == true && sourceKey.equals("") && privilegeResponseModel?.country.equals("Vietnam")) {
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
                    fragmentListener.selectedPoint(
                        droppingList[index].name!!,
                        BpDpDroppingFragment.tag,
                        droppingList[index].id.toString()
                    )
                    binding.btnDropoffNext.setOnClickListener {
                        fragmentListener.sendPickupDropOffDetails(
                            binding.etDropoffAddress.text?.toString()?.trim() ?: "",
                            "0.0",
                            BpDpDroppingFragment.tag
                        )
                    }
                } else {
                    fragmentListener.selectedPoint(
                        droppingList[index].name,
                        BpDpDroppingFragment.tag,
                        droppingList[index].id.toString()
                    )
                    if (droppingList.isNotEmpty()) {
                        PreferenceUtils.putObject(droppingList[index], PREF_DROPPING_STAGE_DETAILS)
                    }
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


}