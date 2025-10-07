package com.bitla.ts.presentation.view.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.listener.OnItemPassData
import com.bitla.ts.databinding.ActivityInterCityAgentBinding
import com.bitla.ts.domain.pojo.available_routes.BoardingPointDetail
import com.bitla.ts.domain.pojo.available_routes.DropOffDetail
import com.bitla.ts.domain.pojo.destination_list.City
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.FrequentAgentSearchAdapter
import com.bitla.ts.presentation.adapter.InterCityAgentAdapter
import com.bitla.ts.presentation.viewModel.RecentSearchViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.AGENT_FROM_ALL_DROPPING_POINTS
import com.bitla.ts.utils.constants.DROPPING_SELECTION
import com.bitla.ts.utils.constants.FREQUENT_SEARCH
import com.bitla.ts.utils.constants.IS_FROM_AGENT
import com.bitla.ts.utils.constants.MergeBus.MERGE_BUS_SELECT_DESTINATION
import com.bitla.ts.utils.constants.MergeBus.MERGE_BUS_SELECT_DESTINATION_ID
import com.bitla.ts.utils.constants.REDIRECT_FROM
import com.bitla.ts.utils.sharedPref.AGENT_SELECTED_BOARDING_DETAIL
import com.bitla.ts.utils.sharedPref.AGENT_SELECTED_DESTINATION
import com.bitla.ts.utils.sharedPref.AGENT_SELECTED_DESTINATION_ID
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.sharedPref.SELECTED_BOARDING_DETAIL
import gone
import isNetworkAvailable
import kotlinx.coroutines.launch
import noNetworkToast
import onChange
import org.koin.androidx.viewmodel.ext.android.viewModel
import toast
import visible


class InterCityAgentActivity : BaseActivity(), OnItemPassData {
    private var lastSelectedCity: Int = -1
    private lateinit var binding: ActivityInterCityAgentBinding
    private var cityList: MutableList<City> = mutableListOf()
    private var filteredCityList: MutableList<City> = mutableListOf()
    private var interCityAgentAdapter: InterCityAgentAdapter? = null
    private var frequentAgentSearchAdapter: FrequentAgentSearchAdapter? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val recentSearchViewModel by viewModel<RecentSearchViewModel<Any?>>()
    private var resId: String? = null
    private var fromCityText: String? = null
    private var subtitle: String? = null
    private var sourceId: String? = ""
    private var locale: String? = ""
    private var loginModelPref: LoginModel = LoginModel()
    private var agentFromAllDroppingPoints = false
    private var isFromMergeBus:Boolean=false
    override fun initUI() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityAgentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        getPref()
        init()
        onClickListener()
        if(isFromMergeBus){
            callDestinationListApiMergeBus()
      }else{
            callDestinationListApi()
        }
        setObserver()
    }

    private fun callDestinationListApiMergeBus() {
        showProgress()
        if (isNetworkAvailable()) {
            recentSearchViewModel.getDestinationList(
                loginModelPref.api_key,
                sourceId ?: ""
            )
        } else
            noNetworkToast()
    }

    private fun onClickListener() {
        binding.toolbarFromOrigin.setOnClickListener(this)
        binding.toolbarImageLeft.setOnClickListener(this)
        binding.tvAllDroppingPoints.setOnClickListener(this)
    }

    private fun getPref() {
        getIntentValues()
        sourceId = PreferenceUtils.getSourceId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun getIntentValues() {
        if (intent.hasExtra(getString(R.string.res_id)))
            resId = intent.getLongExtra(getString(R.string.res_id), 0).toString()
        if (intent.hasExtra(getString(R.string.from_city)))
            fromCityText = intent.getStringExtra(getString(R.string.from_city))
        if (intent.hasExtra(getString(R.string.nav_header_subtitle)))
            subtitle = intent.getStringExtra(getString(R.string.nav_header_subtitle))
        if (intent.hasExtra(AGENT_FROM_ALL_DROPPING_POINTS))
            agentFromAllDroppingPoints =
                intent.getBooleanExtra(AGENT_FROM_ALL_DROPPING_POINTS, false)

        if (intent.hasExtra(getString(R.string.is_from_mergebus)))
            isFromMergeBus =
                intent.getBooleanExtra(getString(R.string.is_from_mergebus), false)
    }

    private fun setObserver() {
        recentSearchViewModel.destinationListWithOrigin.observe(this) {
            if (it != null) {
                hideProgress()
                if (!it.cityList.isNullOrEmpty()) {
                    it.cityList.forEach {
                        if (it.dropping_point != null) {
                            val droppingList = it.dropping_point as MutableList<DropOffDetail>
                            cityList.add(City(droppingList, it.city.id, it.city.name))
                        }else{
                            if(isFromMergeBus){
                                cityList.add(City(mutableListOf(), it.city.id, it.city.name))
                            }
                        }
                    }
                    setBoardingListAdapter()
                    frequentSearchAdapter()

                    if (it.boardingDetails != null) {
                        val boardingPointDetail = BoardingPointDetail(
                            address = "",
                            id = (it.boardingDetails.boarding_stage_id ?: ""),
                            landmark = "",
                            name = it.boardingDetails.boarding_stage_name ?: "",
                            time = it.boardingDetails.time ?: "",
                            distance = it.boardingDetails.distance ?: ""
                        )
                        PreferenceUtils.putObject(boardingPointDetail, SELECTED_BOARDING_DETAIL)
                        PreferenceUtils.putObject(boardingPointDetail, AGENT_SELECTED_BOARDING_DETAIL)
                    }
                } else
                    showNoDataPage()
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    private fun callDestinationListApi() {
        showProgress()
        if (isNetworkAvailable()) {
            recentSearchViewModel.destinationListWithOrigin(
                loginModelPref.api_key,
                sourceId ?: "",
                resId
            )
        } else
            noNetworkToast()
    }

    private fun showProgress() {
        binding.includeProgress.progressBar.visible()
        binding.mainLayout.gone()
        binding.noResultLayout.gone()
    }

    private fun hideProgress() {
        binding.includeProgress.progressBar.gone()
        binding.noResultLayout.gone()
        binding.mainLayout.visible()
    }

    private fun showNoDataPage() {
        binding.mainLayout.gone()
        binding.noResultLayout.visible()
    }

    override fun isInternetOnCallApisAndInitUI() {
        init()
    }

    private fun init() {
        if (!agentFromAllDroppingPoints)
        {
            binding.recentCitySearch.gone()
            binding.fromLayout.visible()
            binding.toolbarImageLeft.gone()
            binding.tvFromOrigin.text = fromCityText ?: ""
            binding.tvSubTitle.text = subtitle ?: ""
        }else {
            binding.recentCitySearch.visible()
            binding.fromLayout.gone()
            binding.toolbarImageLeft.visible()
        }


       lifecycleScope.launch {
           binding.etSearch.hint = getString(R.string.search_dropping_points)
           binding.etSearch.onChange { text ->
               filteredCityList.clear()
               cityList.forEach { city ->
                   if(!isFromMergeBus){
                   val droppingList = mutableListOf<DropOffDetail>()
                   city.dropping_point.forEach {
                       if (it.name.contains(text, true)) {
                           droppingList.add(it)
                           if (!filteredCityList.contains(City(droppingList, city.id, city.name)))
                               filteredCityList.add(City(droppingList, city.id, city.name))
                       }
                   }}else{
                       val droppingList = mutableListOf<DropOffDetail>()
                           if (city.name.contains(text, true) && !filteredCityList.contains(City(droppingList, city.id, city.name))) {
                               filteredCityList.add(City(droppingList, city.id, city.name))
                           }
                       }

               }
               interCityAgentAdapter?.filterList(filteredCityList)
           }
       }
    }

    private fun frequentSearchAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recentCitySearch.layoutManager = layoutManager
        frequentAgentSearchAdapter = FrequentAgentSearchAdapter(this, this, cityList, lastSelectedCity)
        binding.recentCitySearch.adapter = frequentAgentSearchAdapter
    }

    private fun setBoardingListAdapter(
    ) {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManager
        interCityAgentAdapter = InterCityAgentAdapter(this, this, cityList,isFromMergeBus)
        binding.rvCity.adapter = interCityAgentAdapter
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.toolbar_image_left -> onBackPressedDispatcher.onBackPressed()
            R.id.toolbarFromOrigin -> onBackPressedDispatcher.onBackPressed()
            R.id.tvAllDroppingPoints-> {
                if (agentFromAllDroppingPoints) {
                    agentFromAllDroppingPoints = false
                    val intent = Intent()
                    intent.putExtra(AGENT_FROM_ALL_DROPPING_POINTS, agentFromAllDroppingPoints)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onItemData(view: View, str1: String, str2: String) {
        if (view.tag == FREQUENT_SEARCH) {
            val selectedCityIndex = cityList.indexOfFirst { it.id == str2 }
            cityList[selectedCityIndex].isSelectedCity = selectedCityIndex != -1
            if (selectedCityIndex != -1 && selectedCityIndex < cityList.size) {
                val y : Int = binding.rvCity.getChildAt(selectedCityIndex)?.y?.toInt()?:0
                binding.nestedScrollView.post {
                    binding.nestedScrollView.fling(0)
                    binding.nestedScrollView.smoothScrollTo(0, y)
                }
            }
        } else if (view.tag == DROPPING_SELECTION) {
            val cityId = str2.substringAfter(":")
            val selectedCityIndex = cityList.indexOfFirst { it.id.substringAfter(":") == cityId }
            if (selectedCityIndex != -1)
            {
                val selectedCity = cityList[selectedCityIndex].name
                PreferenceUtils.putString(AGENT_SELECTED_DESTINATION, "$str1,$selectedCity")
            }else {
                PreferenceUtils.putString(AGENT_SELECTED_DESTINATION, str1)
            }

            PreferenceUtils.putString(AGENT_SELECTED_DESTINATION_ID, str2)


            if(isFromMergeBus){
                val intent = Intent()
                intent.putExtra(MERGE_BUS_SELECT_DESTINATION_ID, str2)
                intent.putExtra(MERGE_BUS_SELECT_DESTINATION, str1)
                setResult(RESULT_OK, intent)
                finish()
            }else{
            if (agentFromAllDroppingPoints) {
                val intent = Intent()
                intent.putExtra(AGENT_FROM_ALL_DROPPING_POINTS, agentFromAllDroppingPoints)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                val intent = Intent(this, NewCoachActivity::class.java)
                intent.putExtra(IS_FROM_AGENT,true)
                intent.putExtra(REDIRECT_FROM, BusDetailsActivity.TAG)
                startActivity(intent)
            }
        }}
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }
}