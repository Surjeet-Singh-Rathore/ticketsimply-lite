package com.bitla.ts.presentation.view.dashboard.ViewReservationFragments

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.collection_details.*
import com.bitla.ts.domain.pojo.collection_details.request.*
import com.bitla.ts.domain.pojo.collection_details.trip_collection.BranchBooking
import com.bitla.ts.domain.pojo.collection_details.trip_collection.TripCollectionCategoryData
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.PrivilegeResponseModel
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.adapter.tripCollection.TripCollectionAdapter
import com.bitla.ts.presentation.view.activity.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import toast
import visible


class CollectionFragment(private var isTripCollection: Boolean = false) : Fragment(),
    OnItemClickListener, DialogSingleButtonListener {
    private var privileges: PrivilegeResponseModel? = null
    private lateinit var binding: FragmentCollectionBinding
    private lateinit var collectionByAgentAdapter: CollectionByAgentAdapter
    private var searchList = mutableListOf<NestedItems>()

    //    private var agentName = mutableListOf<com.bitla.ts.domain.pojo.collection_details.Result>()
    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String = ""
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()


    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var locale: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCollectionBinding.inflate(inflater, container, false)

        getPref()
        startShimmerEffect()
        if (isTripCollection) {
            getTripCollectionApi()
        } else {
            collectionDetailsAPI()
        }
        initRefreshListner()
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    requireContext().showToast(it)
                }
            }
        }
        return binding.root

    }


    private fun initRefreshListner() {
        binding.refreshLayout.setOnRefreshListener {
            startShimmerEffect()
            if(isTripCollection){
                getTripCollectionApi()
            }else
            {
                collectionDetailsAPI()
            }

        }
    }


    private fun setTripCollectionAgentAdapter(resultList: ArrayList<TripCollectionCategoryData>?) {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvAgentlist.layoutManager = layoutManager
      val  tripCollectionAdapter =
            TripCollectionAdapter(requireActivity(), this, resultList, privileges)
        binding.rvAgentlist.adapter = tripCollectionAdapter
    }


    private fun setCollectionAgentAdapter(
        agent: ArrayList<CollectionSummary>
    ) {

        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvAgentlist.layoutManager = layoutManager
        collectionByAgentAdapter =
            CollectionByAgentAdapter(requireActivity(), this, agent, privileges)
        binding.rvAgentlist.adapter = collectionByAgentAdapter
    }

    private fun getPref() {
        locale = PreferenceUtils.getlang()
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        privileges = (activity as BaseActivity).getPrivilegeBase()
//        resID = PreferenceUtils.getPreference(PREF_RESERVATION_ID, 0L).toString()
        resID = PreferenceUtils.getString("reservationid") ?: ""

    }


    private fun collectionDetailsAPI() {

        if (requireContext().isNetworkAvailable()) {

            pickUpChartViewModel.collectionDetailsAPI(
                ReqBody(
                    api_key = loginModelPref.api_key,
                    reservation_id = resID.toString(),
                    locale = locale
                ),
                collection_Details_method_name
            )

            resendOtpAndQrCodeObserver()
        } else requireContext().noNetworkToast()
    }


    private fun getTripCollectionApi() {

        if (requireContext().isNetworkAvailable()) {

            pickUpChartViewModel.tripCollectionDetailsAPI(
                ReqBody(
                    api_key = loginModelPref.api_key,
                    reservation_id = resID.toString(),
                    locale = locale
                ),
                collection_Details_method_name
            )

            setTripCollectionObserver()
        } else requireContext().noNetworkToast()
    }


    private fun resendOtpAndQrCodeObserver() {

        pickUpChartViewModel.collectionDetailsResponse.observe(viewLifecycleOwner) {

            Timber.d("reservationblock ${it}")

            if (it != null) {
                binding.refreshLayout.isRefreshing = false
                stopShimmerEffect()

                when (it.code) {
                    200 -> {
                        val collectionList = arrayListOf<CollectionSummary>()
                        binding.rvAgentlist.visible()

                        binding.NoResult.gone()
                        binding.textView9.visible()
                        val resultList = it.collection_summary
                        try {
                            resultList.forEach {
                                collectionList.add(it)
                            }
                        } catch (e: Exception) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace()
                            }
                        }
                        setCollectionAgentAdapter(collectionList)
                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/

                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        binding.NoResult.visible()
                        binding.textView9.gone()
                        binding.rvAgentlist.gone()

                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> requireContext().toast(it1) }
                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }


    private fun setTripCollectionObserver() {

        pickUpChartViewModel.tripCollectionDetailsResponse.observe(viewLifecycleOwner) {

            Timber.d("reservationblock ${it}")

            if (it != null) {
                binding.refreshLayout.isRefreshing = false
                stopShimmerEffect()
                when (it.code) {
                    200 -> {
//                        val collectionList = arrayListOf<CollectionSummary>()
                        binding.rvAgentlist.visible()
                        binding.NoResult.gone()
                        binding.textView9.gone()



                        var resultList:ArrayList<TripCollectionCategoryData>?= arrayListOf()

                        if(it.branchBooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="Branch Bookings"
                            data.collectionDetails=it.branchBooking
                            resultList?.add(data)
                        }
                        if(it.onlineAgentBooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="Online Agent Bookings"
                            data.collectionDetails=it.onlineAgentBooking
                            resultList?.add(data)
                        }

                        if(it.apiAgentBooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="Online Api Bookings"
                            data.collectionDetails=it.apiAgentBooking
                            resultList?.add(data)
                        }

                        if(it.ebooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="E-Bookings"
                            data.collectionDetails=it.ebooking
                            resultList?.add(data)

                        }


                        if(it.offlineBranchBooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="Offline Branch Bookings"
                            data.collectionDetails=it.offlineBranchBooking
                            resultList?.add(data)
                        }

                        if(it.offlineAgentBooking?.isNotEmpty()==true){
                            val data=TripCollectionCategoryData()
                            data.categoryName="Offline Agent Bookings"
                            data.collectionDetails=it.offlineAgentBooking
                            resultList?.add(data)
                        }

                        setTripCollectionAgentAdapter(resultList)

//                        val resultList = it.collection_summary
//                        try {
//                            resultList.forEach {
//                                collectionList.add(it)
//                            }
//                        } catch (e: Exception) {
//                            if (BuildConfig.DEBUG) {
//                                e.printStackTrace()
//                            }
//                        }
//
//                        setCollectionAgentAdapter(collectionList)

                    }

                    401 -> {
                        /*DialogUtils.unAuthorizedDialog(
                            requireContext(),
                            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                            this
                        )*/

                        (activity as BaseActivity).showUnauthorisedDialog()

                    }

                    else -> {
                        binding.NoResult.visible()
                        binding.textView9.gone()
                        binding.rvAgentlist.gone()

//                        if (it.result?.message != null) {
//                            it.result.message.let { it1 -> requireContext().toast(it1) }
//                        }
                    }
                }
            } else {
                requireContext().toast(getString(R.string.server_error))
            }
        }
    }


    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onButtonClick(view: Any, dialog: Dialog) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    private fun startShimmerEffect() {
        binding.shimmerBookTicket.visible()
        binding.NoResult.gone()
//        binding.myBookingBookTicketContainer.gone()
        binding.shimmerBookTicket.startShimmer()
    }

    /*
     * this method to used for stop Shimmer Effect
     * */
    private fun stopShimmerEffect() {
        binding.shimmerBookTicket.gone()
//        binding.myBookingBookTicketContainer.visible()
        if (binding.shimmerBookTicket.isShimmerStarted) {
            binding.shimmerBookTicket.stopShimmer()
        }
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}