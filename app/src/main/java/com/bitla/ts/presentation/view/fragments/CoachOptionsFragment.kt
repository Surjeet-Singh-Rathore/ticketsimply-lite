package com.bitla.ts.presentation.view.fragments

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.sharedPref.*
import com.google.android.flexbox.*


class CoachOptionsFragment() : Fragment(), OnItemClickListener {

    private var shortOptionsList = mutableListOf<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CoachShortOptionsAdapter // You need to create your adapter class
    private var privilegeResponseModel: PrivilegeResponseModel? = null
    private var isEditReservation: Boolean? = false
    private var isAgentLogin: Boolean = false
    private lateinit var singleMenuClickListener: SingleMenuClickListener

    companion object {
        private const val ARG_IS_SERVICE_BLOCKED = "is_service_blocked"

        fun newInstance(isServiceBlocked: Boolean): CoachOptionsFragment {
            val fragment = CoachOptionsFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_SERVICE_BLOCKED, isServiceBlocked)
            fragment.arguments = args
            return fragment
        }
    }

    private var isServiceBlocked: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SingleMenuClickListener) {
            singleMenuClickListener = context
        } else {
            throw ClassCastException("$context must implement SingleMenuClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coach_options, container, false)
        recyclerView = view.findViewById(R.id.rvCoachOptions)
        super.onCreate(savedInstanceState)
        isServiceBlocked = arguments?.getBoolean(ARG_IS_SERVICE_BLOCKED) ?: false

        getPref()

        return view
    }

    private fun getPref() {
        privilegeResponseModel = (activity as BaseActivity).getPrivilegeBase()
        if (privilegeResponseModel?.isEditReservation != null) {
            isEditReservation = privilegeResponseModel?.isEditReservation
        }
        isAgentLogin = privilegeResponseModel?.isAgentLogin ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assuming your adapter needs some data to display,
        // you should initialize it here with your data.
        shortOptionsList = getData() // Replace getData() with your method to retrieve data

        adapter = CoachShortOptionsAdapter(shortOptionsList,this) // Initialize your adapter with the data

//        val spanCount = if (shortOptionsList.size < 4)
//            1

//        else 2
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = FlexboxLayoutManager(context) // Change the number 2 to your desired number of columns
//        recyclerView.layoutManager = GridLayoutManager(context,3) // Change the number 2 to your desired number of columns
//        val recyclerView = context.findViewById(android.R.id.recyclerview) as RecyclerView
        
        // Flexbox Layout
        recyclerView.adapter = adapter
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        recyclerView.setLayoutManager(layoutManager)
   
    }

    private fun getData(): MutableList<String> {
        val optionsList = mutableListOf<String>()
        if (isEditReservation == true)
            optionsList.add(getString(R.string.modify_reservation))
       
        if (privilegeResponseModel?.allowToConfigureSeatWiseFare == true)
            optionsList.add(getString(R.string.seat_wise_fare))
       
        if (!isServiceBlocked && privilegeResponseModel?.singlePageChartBlockUnblock == true)
            optionsList.add(getString(R.string.block))
        
        if (isServiceBlocked && privilegeResponseModel?.singlePageChartBlockUnblock == true)
            optionsList.add(getString(R.string.unblock))

        if ((PreferenceUtils.getLogin().role.equals(requireContext().getString(R.string.role_field_officer), true)
            && privilegeResponseModel?.boLicenses?.allowToUpdateVehicleExpenses == true)
            || (privilegeResponseModel?.allowUpdateDetailsOptionInReservationChart == true))
        {
            optionsList.add(getString(R.string.update_details_option))
        }
        
        if (privilegeResponseModel?.notifyOption == true)
            optionsList.add(getString(R.string.send_sms))
        
        if (privilegeResponseModel?.showViewChartLinkInTheSearchResults == true && !isAgentLogin)
            optionsList.add(getString(R.string.pick_up_chart))

        return optionsList
    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {
            when (shortOptionsList[position]) {
                getString(R.string.modify_reservation) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.modify_reservation))
                }
                getString(R.string.seat_wise_fare) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.seat_wise_fare))
                }
                getString(R.string.block) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.block))
                }
                getString(R.string.unblock) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.unblock))
                }
                getString(R.string.update_details_option) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.update_details_option))
                }
                getString(R.string.send_sms) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.send_sms))
                }
                getString(R.string.pick_up_chart) -> {
                    singleMenuClickListener.onOptionMenuClick(getString(R.string.pick_up_chart))
                }
            }
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }
}
