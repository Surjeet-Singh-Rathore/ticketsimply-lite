package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.os.*
import android.text.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.domain.pojo.recent_search.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.*
import gone
import timber.log.*
import toast
import visible
import java.util.*

class InterCityActivity : BaseActivity(), OnItemPassData {
    private lateinit var binding: ActivityInterCityBinding

    private var originList: MutableList<Origin> = mutableListOf()
    private var recentSearchList: MutableList<RecentSearch> = mutableListOf()
    private var recentSearchListOrigin: MutableList<SearchModel> = mutableListOf()
    private var tempRecentSearchListOrigin: MutableList<SearchModel> = mutableListOf()
    private var recentSearchListDestination: MutableList<SearchModel> = mutableListOf()
    private var tempOrginList: MutableList<Origin> = mutableListOf()
    private var tempstationList: MutableList<Origin> = mutableListOf()
    private var destinationStationList: ArrayList<Destination> = arrayListOf()

    private var destinationCityList: ArrayList<Destination> = arrayListOf()
    private var tempCitylist: ArrayList<String> = arrayListOf()
    private var destinationList: MutableList<Destination> = mutableListOf()
    private var citySearchList: ArrayList<SearchModel> = arrayListOf()
    private var stationSearchList: ArrayList<SearchModel> = arrayListOf()
    private var searchAdapter: InterCityAdapter? = null
    private var friquentSearchAdapter: FriquentSearchAdapter? = null
    private lateinit var layoutManagerCity: RecyclerView.LayoutManager
    private var filterdNames: MutableList<SearchModel> = mutableListOf()
    private lateinit var searchList: MutableList<SearchModel>

    private var tempCitySearchList: ArrayList<SearchModel> = arrayListOf()
    private var tempStationSearchList: ArrayList<SearchModel> = arrayListOf()
    override fun initUI() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterCityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
        init()

    }

    override fun isInternetOnCallApisAndInitUI() {
        init()
    }

    private fun init() {
        try {
            if (PreferenceUtils.getOriginCity() != null) {
                originList = PreferenceUtils.getOriginCity()!!
                originList.sortWith(compareBy<Origin> { it.name })
            }
            if (PreferenceUtils.getRecentSearch() != null) {
                recentSearchList = PreferenceUtils.getRecentSearch()!!
            }
            Timber.d("recentSearchList: $recentSearchList")

            if (PreferenceUtils.getInterDestinationCity() != null) {
                destinationList = PreferenceUtils.getInterDestinationCity()!!
                Timber.d("recentSearchList12: ${recentSearchList.size}")

                destinationList.sortWith(compareBy<Destination> { it.name })
            }

            val citySelectionType: String =
                intent.getStringExtra(getString(R.string.CITY_SELECTION_TYPE))!!


            if (citySelectionType == getString(R.string.SOURCE_SELECTION)) {
                recentSearchListOrigin.clear()
                var prefOrgin = PreferenceUtils.getString("recentOrigin")
                Timber.d("testclick: $prefOrgin")
                binding.toolbarHeaderText.text = getString(R.string.selectSource)
                searchList = mutableListOf()
                //searchList = originList
                for (i in 0..originList.size.minus(1)) {
                    var cityModel = SearchModel()
                    cityModel.id = originList[i].id.toString()
                    cityModel.name = originList[i].name
                    searchList.add(cityModel)
                }
                recentSearchList.forEach {
                    val searchModel = SearchModel()
                    searchModel.id = it.origin_id
                    searchModel.name = it.origin_name
                    recentSearchListOrigin.add(searchModel)

                    val searchModelDestination = SearchModel()
                    searchModelDestination.id = it.dest_id
                    searchModelDestination.name = it.dest_name
                    recentSearchListDestination.add(searchModelDestination)
                }
                val b = recentSearchListOrigin.distinctBy { it.id } as MutableList
                //searchList = originList
                for (i in 0..originList.size.minus(1)) {
                    if (originList[i].id.toString().contains("-1")) {
                        tempOrginList.add(originList[i])
                    } else {
                        tempstationList.add(originList[i])
                    }
                }
                tempOrginList.forEach {
                    val searchModel = SearchModel()
                    searchModel.id = it.id
                    searchModel.name = it.name
                    citySearchList.add(searchModel)
                }
                tempstationList.forEach {
                    val searchModel = SearchModel()
                    searchModel.id = it.id
                    searchModel.name = it.name
                    stationSearchList.add(searchModel)
                }
                Timber.d("intercity_mapCheck5: ${citySearchList.size}: ${stationSearchList.size}")


                binding.etSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        tempCitySearchList.clear()
                        tempStationSearchList.clear()

                        for (i in 0..citySearchList.size.minus(1)) {
                            if (citySearchList[i].name.toString().lowercase()
                                    .contains(s.toString().lowercase())
                            ) {
                                tempCitySearchList.add(citySearchList[i])
                            }
                        }
                        for (i in 0..stationSearchList.size.minus(1)) {
                            if (stationSearchList[i].name.toString().lowercase()
                                    .contains(s.toString().lowercase())
                            ) {
                                tempStationSearchList.add(stationSearchList[i])
                            }
                        }
                            setCityAdapter(tempCitySearchList, tempStationSearchList, true)

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
                if (prefOrgin.isNullOrEmpty()) {
                    prefOrgin = ""
                }

                b.forEach {
                    if (!it.name.isNullOrEmpty() && it.name.toString() != "null")
                        tempRecentSearchListOrigin.add(it)
                }

                friquentSearchAdapter(tempRecentSearchListOrigin, prefOrgin, true)
                setCityAdapter(citySearchList, stationSearchList, true)
                originList.sortBy { it.name!!.lowercase() }
            } else if (citySelectionType == getString(R.string.DESTINATION_SELECTION)) {
                var prefOrgin = PreferenceUtils.getString("recentDestination")

                if (prefOrgin.isNullOrEmpty()) {
                    prefOrgin = ""
                }
//                val origin = PreferenceUtils.getString(PREF_SOURCE)
//                val originId = PreferenceUtils.getString(PREF_SOURCE_ID)
                Timber.d("originCheckSelection: ${destinationList.size}")
//                var finalId = ""

                searchList = mutableListOf()
                //searchList = destinationList
                for (i in 0..destinationList.size.minus(1)) {
                    var cityModel = SearchModel()
                    cityModel.id = destinationList[i].id.toString()
                    cityModel.name = destinationList[i].name
                    searchList.add(cityModel)
                }
                binding.toolbarHeaderText.text = getString(R.string.selectDestination)
                //searchList = destinationList
                if (destinationList.isNotEmpty()) {
                    for (i in 0..destinationList.size.minus(1)) {
                        if (destinationList[i].id.toString().contains("-1")) {
                            destinationCityList.add(destinationList[i])
                        } else {
                            destinationStationList.add(destinationList[i])
                        }
                    }


                    recentSearchList.forEach {
                        val searchModel = SearchModel()
                        searchModel.id = it.origin_id
                        searchModel.name = it.origin_name
                        recentSearchListOrigin.add(searchModel)
                        val searchModelDestination = SearchModel()
                        searchModelDestination.id = it.dest_id
                        searchModelDestination.name = it.dest_name
                        recentSearchListDestination.add(searchModelDestination)
                    }
                    destinationCityList.forEach {
                        tempCitylist.add(it.name.toString())

                        val searchModel = SearchModel()
                        searchModel.id = it.id
                        searchModel.name = it.name
                        citySearchList.add(searchModel)
                    }
                    destinationStationList.forEach {
                        val searchModel = SearchModel()
                        searchModel.id = it.id
                        searchModel.name = it.name
                        stationSearchList.add(searchModel)
                    }
                    Timber.d("originCheckSelection14: , ${recentSearchList.size}")
                    friquentSearchAdapter(citySearchList, prefOrgin, false)
                    setCityAdapter(citySearchList, stationSearchList, false)
                    destinationList.sortBy { it.name!!.lowercase() }
                } else {
                    binding.mainLayout.gone()
                    binding.noResultLayout.visible()
                }
                binding.etSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        tempCitySearchList.clear()
                        tempStationSearchList.clear()

                        for (i in 0..citySearchList.size.minus(1)) {
                            if (citySearchList[i].name.toString().lowercase()
                                    .contains(s.toString().lowercase())
                            ) {
                                tempCitySearchList.add(citySearchList[i])

//                            tempCitySearchList.forEach{
//                                if (!it.id.toString().contains(citySearchList[i].id.toString())){
//                                    tempCitySearchList.add(citySearchList[i])
//                                }
//                            }
                            }
                        }
                        for (i in 0..stationSearchList.size.minus(1)) {
                            if (stationSearchList[i].name.toString().lowercase()
                                    .contains(s.toString().lowercase())
                            ) {
                                tempStationSearchList.add(stationSearchList[i])

//                            tempStationSearchList.forEach{
//                                if (!it.id.toString().contains(stationSearchList[i].id.toString())){
//                                    tempStationSearchList.add(stationSearchList[i])
//                                }
//                            }
                            }
                        }

                        setCityAdapter(tempCitySearchList, tempStationSearchList, false)
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
            } else binding.toolbarHeaderText.text = getString(R.string.empty)

        } catch (e: Exception) {
            toast(getString(R.string.opps))
        }
        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun search(text: String) {
        filterdNames = mutableListOf<SearchModel>()
        for (s in searchList) {
            if (s.name != null && s.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filterdNames.add(s)
            }
        }
        if (searchAdapter != null) searchAdapter?.filterList(filterdNames)
    }

    private fun setCityAdapter(
        cityList: ArrayList<SearchModel>,
        stationList: ArrayList<SearchModel>,
        fromOrigin: Boolean
    ) {
        layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManagerCity
        searchAdapter = InterCityAdapter(this, this, cityList, fromOrigin, stationList)
        binding.rvCity.adapter = searchAdapter
    }

    private fun friquentSearchAdapter(
        cityLists: MutableList<SearchModel>,
        selectedCity: String,
        fromOrigin: Boolean
    ) {
        layoutManagerCity = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recentCitySearch.layoutManager = layoutManagerCity
        friquentSearchAdapter =
            FriquentSearchAdapter(this, this, selectedCity, fromOrigin, cityLists)
        binding.recentCitySearch.adapter = friquentSearchAdapter
    }

    private fun returnSelectedCity(cityName: String?, cityId: String?) {

        val returnIntent = Intent()
        returnIntent.putExtra(
            getString(R.string.SELECTED_CITY_TYPE),
            binding.toolbarHeaderText.text
        )

        returnIntent.putExtra(getString(R.string.SELECTED_CITY_NAME), cityName)
        returnIntent.putExtra(getString(R.string.SELECTED_CITY_ID), cityId)

        setResult(Activity.RESULT_OK, returnIntent)
//        onBackPressed()
        finish()
    }


    override fun onItemData(view: View, str1: String, str2: String) {
        var tempVar = ""
        if (str2.contains(":")) {
            tempVar = str2.split(":")[1]
        } else {
            tempVar = str2
        }
        Timber.d("colorselected2: ${tempVar}")


        if (view.tag != null) {
            if (view.tag == true) {
                PreferenceUtils.putString("recentOrigin", tempVar)
            } else {
                PreferenceUtils.putString("recentDestination", tempVar)
            }
        }
        returnSelectedCity(str1, str2)

    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }
}