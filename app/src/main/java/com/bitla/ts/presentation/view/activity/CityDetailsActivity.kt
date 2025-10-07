package com.bitla.ts.presentation.view.activity

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.BuildConfig
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.sharedPref.*
import com.bitla.ts.utils.showToast
import gone
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.*
import visible
import java.util.*

class CityDetailsActivity : BaseActivity(), OnItemClickListener {

    override fun initUI() {
        binding = ActivityCityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }
    }

    private var isCheckingInspector = false
    private val cityDetailViewModel by viewModel<CityDetailViewModel<Any?>>()
    private var cityOriginList = arrayListOf<String?>()
    private var cityDestinationList = arrayListOf<String?>()
    private var cityOriginId = arrayListOf<Int?>()
    private var cityDestinationId = arrayListOf<Int?>()

    private var demoList = mutableMapOf<Int?, String?>()
    private var originDemoList = mutableMapOf<Int?, String?>()
    private var demoList2 = mutableMapOf<Int?, String?>()
    private var destinationDemoList2 = mutableMapOf<Int?, String?>()


    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var citylistAdapter: CityListAdapter
    val preforigin = PreferenceUtils.getPreference("selectedCityOrigin", "")
    var prefOriginCityId = PreferenceUtils.getPreference("selectedCityIdOrigin", "")
    var prefdestination = PreferenceUtils.getPreference("selectedCityDestination", "")
    var prefDestinationCityId = PreferenceUtils.getPreference("selectedCityIdDestination", "")
    var prefTravelSelection = PreferenceUtils.getPreference("TravelSelection", "none")


    private val TAG: String = CityDetailsActivity::class.java.simpleName

    private lateinit var binding: ActivityCityDetailsBinding
    private var locale: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("qwertyasdfg: ${preforigin},${prefOriginCityId}, destination ${prefdestination}, ${prefDestinationCityId}")
        init()
    }

    override fun isInternetOnCallApisAndInitUI() {
        init()
    }

    override fun onClickOfNavMenu(position: Int) {
    }


    private fun init() {

        if (intent.getBooleanExtra("is_checking_inspector", false)) {
            isCheckingInspector = true
        }

        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
        if (prefTravelSelection == "OriginCity") {
            binding.toolbarHeaderText.text = resources.getString(R.string.from_city)
        } else if (prefTravelSelection == "DestinationCity") {
            binding.toolbarHeaderText.text = resources.getString(R.string.to_city)
        }

        getPref()
        setCityDetailsObserver()
        callCityDetailsApi()

        binding.allCity.setOnClickListener {
            if (prefTravelSelection == "OriginCity") {
                val intent = Intent()
                intent.putExtra("CityOriginCityName", getString(R.string.all))
                intent.putExtra("selectedCityDestination", prefdestination)
                intent.putExtra("selectedCityIdOrigin", "0")
                intent.putExtra("selectedCityIdDestination", prefDestinationCityId)
                intent.putExtra("TravelSelection", prefTravelSelection)
                intent.putExtra("is_checking_inspector", isCheckingInspector)
                setResult(RESULT_OK, intent)
                finish()

            } else if (prefTravelSelection == "DestinationCity") {
                val intent = Intent()
                intent.putExtra("CityOriginCityName", preforigin)
                intent.putExtra("selectedCityDestination", getString(R.string.all))
                intent.putExtra("selectedCityIdOrigin", prefOriginCityId)
                intent.putExtra("selectedCityIdDestination", "0")
                intent.putExtra("TravelSelection", prefTravelSelection)
                intent.putExtra("is_checking_inspector", isCheckingInspector)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        clickListener()

        lifecycleScope.launch {
            cityDetailViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    private fun clickListener() {

    }

    private fun setCityAdapter(citylist: MutableMap<Int?, String?>) {
        val final = citylist.toList().sortedBy { (_, value) -> value?.lowercase(Locale.getDefault()) }.toMap()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCity.layoutManager = layoutManager
        citylistAdapter =
            CityListAdapter(this, this, final)
        binding.rvCity.adapter = citylistAdapter
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    override fun onClickOfItem(data: String, position: Int) {

        val cityOriginName: String? = originDemoList.getValue(position)
        val cityDestinationName: String? = destinationDemoList2.getValue(position)
        val cityOriginID: Int = position
        val cityDestinationID: Int = position

        val getSelctedOriginCityName = "$cityOriginName"
        val getSelctedDestinationCityID = "$cityDestinationID"
        val getSelctedDestinationCityName = "$cityDestinationName"
        val getSelctedOriginCityID = "$cityOriginID"

        if (prefTravelSelection == "OriginCity") {
            val intent = Intent()
            intent.apply {
                putExtra("CityOriginCityName", getSelctedOriginCityName)
                putExtra("selectedCityDestination", prefdestination)
                putExtra("selectedCityIdOrigin", getSelctedOriginCityID)
                putExtra("selectedCityIdDestination", prefDestinationCityId)
                putExtra("TravelSelection", prefTravelSelection)
            }
            setResult(RESULT_OK, intent)
            finish()
        } else if (prefTravelSelection == "DestinationCity") {
            val intent = Intent()
            intent.apply {
                putExtra("CityOriginCityName", preforigin)
                putExtra("selectedCityDestination", getSelctedDestinationCityName)
                putExtra("selectedCityIdOrigin", prefOriginCityId)
                putExtra("selectedCityIdDestination", getSelctedDestinationCityID)
                putExtra("TravelSelection", prefTravelSelection)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onMenuItemClick(
        itemPosition: Int,
        menuPosition: Int,
        busData: Result
    ) {
    }

    private fun callCityDetailsApi() {
        cityDetailViewModel.cityDetailAPI(
            loginModelPref.api_key,
            response_format,
            locale!!,
            city_Details_method_name
        )
    }

    private fun setCityDetailsObserver() {
        cityDetailViewModel.cityDetailResponse.observe(this) {
            Timber.d("LoadingState ${it}")
            if (it != null) {
                if (!it.result.isNullOrEmpty()) {
                    for (i in 0..it.result.size.minus(1)) {

                        demoList += Pair(it.result[i].id, it.result[i].name)
                        demoList2 += Pair(it.result[i].id, it.result[i].name)
                        originDemoList += Pair(it.result[i].id, it.result[i].name)
                        destinationDemoList2 += Pair(it.result[i].id, it.result[i].name)

                        cityOriginList.add(it.result[i].name)
                        cityDestinationList.add(it.result[i].name)
                        cityOriginId.add(it.result[i].id)
                        cityDestinationId.add(it.result[i].id)

                    }
                }
            }
            binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    demoList2.clear()
                    demoList.clear()

                    val searchText = newText?.lowercase(Locale.getDefault())

                    if (searchText?.isNotEmpty() == true && searchText != "") {
                        if (prefTravelSelection == "OriginCity") {
                            if (!cityOriginList.isNullOrEmpty()) {
                                for (i in 0..cityOriginList.size.minus(1)) {
                                    if (cityOriginList[i]?.lowercase(Locale.getDefault())?.contains(searchText) == true
                                    ) {
                                        demoList += Pair(cityOriginId[i], cityOriginList[i])
                                        demoList2 += Pair(cityOriginId[i], cityOriginList[i])
                                        if (demoList.values.contains(prefdestination) && !prefDestinationCityId.isNullOrEmpty()) {
                                            demoList.remove(prefDestinationCityId?.toInt())
                                        }
                                    }
                                }
                            }

                            demoList.toList().sortedBy { (_, value) -> value?.lowercase(Locale.getDefault()) }.toMap()
                            setCityAdapter(demoList)
//                            binding.rvCity.adapter!!.notifyDataSetChanged()
                        } else if (prefTravelSelection == "DestinationCity") {
                            for (i in 0..cityDestinationList.size.minus(1)) {
                                if (cityDestinationList[i]?.lowercase(Locale.getDefault())
                                        ?.contains(searchText) == true
                                ) {
                                    demoList += Pair(cityDestinationId[i], cityDestinationList[i])
                                    demoList2 += Pair(
                                        cityDestinationId[i],
                                        cityDestinationList[i]
                                    )
                                    if (demoList2.values.contains(preforigin)) {
                                        demoList2.remove(prefOriginCityId?.toInt())
                                    }
                                }
                            }
                            demoList2.toList().sortedBy { (_, value) -> value?.lowercase(Locale.getDefault()) }
                                .toMap()
                            setCityAdapter(demoList2)

//                            binding.rvCity.adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        if (prefTravelSelection == "OriginCity") {
                            demoList.clear()
                            if(!it.result.isNullOrEmpty()){
                                for (i in 0..it.result.size.minus(1)) {
                                    demoList += Pair(it.result[i].id, it.result[i].name)
                                }
                            }
                            setCityAdapter(demoList)
                        } else if (prefTravelSelection == "DestinationCity") {

                            try {
                                demoList2.clear()
                                if(!it.result.isNullOrEmpty()) {
                                    for (i in 0..it.result.size.minus(1)) {
                                        demoList2 += Pair(it.result[i].id, it.result[i].name)
                                    }
                                }
                                setCityAdapter(demoList2)

                            }catch (e: Exception){
                                if(BuildConfig.DEBUG){
                                    e.printStackTrace()
                                }
                            }

                        }
                    }
                    return false
                }
            })

            if (prefTravelSelection == "OriginCity") {
                if (preforigin.toString() == getString(R.string.all) || preforigin.toString() == "" || preforigin.toString() == "0") {
                    binding.selectedTick.visible()
                    Timber.d("origin : $preforigin ,dest $prefdestination")

                    if (prefDestinationCityId.toString() == "") {
                        setCityAdapter(demoList)
                    } else {
                        if (demoList.values.contains(prefdestination.toString())) {
                            if (prefDestinationCityId.toString() == "" || prefDestinationCityId == "0") {
                                setCityAdapter(demoList)
                            } else {
                                demoList.remove(prefDestinationCityId?.toInt())
                                setCityAdapter(demoList)
                            }
                        } else {
                            if (prefDestinationCityId.toString() == "" || prefDestinationCityId == "0") {
                                demoList2 += Pair(
                                    prefDestinationCityId?.toInt(),
                                    prefdestination.toString()
                                )
                                setCityAdapter(demoList)
                            }
                        }
                    }
                } else if (demoList.values.contains(prefdestination.toString())) {
                    if (prefDestinationCityId.toString() == "" || prefDestinationCityId == "0") {
                        setCityAdapter(demoList)
                    } else {
                        demoList.remove(prefDestinationCityId?.toInt())
                        setCityAdapter(demoList)
                    }
                } else {
                    binding.selectedTick.gone()

                    if (prefDestinationCityId.toString() == "" || prefDestinationCityId == "0") {
                        setCityAdapter(demoList)
                    } else {
                        demoList2 += Pair(
                            prefDestinationCityId?.toInt(),
                            prefdestination.toString()
                        )
                        setCityAdapter(demoList)
                    }
                }
            }
            if (prefTravelSelection == "DestinationCity") {
                if (prefdestination.toString() == getString(R.string.all) || prefdestination.toString() == "") {
                    binding.selectedTick.visible()
                    if (prefOriginCityId.toString() == "") {
                        setCityAdapter(demoList2)
                    } else if (demoList2.values.contains(preforigin.toString())) {
                        Timber.d("preforigincity: ${prefOriginCityId}")

                        if (prefOriginCityId.toString() == "" || prefDestinationCityId == "0") {

                            setCityAdapter(demoList2)
                        } else {
                            demoList2.remove(prefOriginCityId?.toInt())

                            setCityAdapter(demoList2)
                        }
                    } else {
                        if (prefOriginCityId.toString() == "" || prefDestinationCityId == "0") {
                            setCityAdapter(demoList2)
                        } else {
                            demoList += Pair(prefOriginCityId!!.toInt(), preforigin.toString())
                            setCityAdapter(demoList2)
                        }
                    }
                } else if (demoList2.values.contains(preforigin.toString())) {
                    binding.selectedTick.gone()
                    if (prefOriginCityId.toString() == "" || prefDestinationCityId == "0") {
                        setCityAdapter(demoList2)
                    } else {
                        demoList2.remove(prefOriginCityId?.toInt())
                        setCityAdapter(demoList2)
                    }
                } else {
                    if (prefOriginCityId.toString() == "" || prefDestinationCityId == "0") {
                        setCityAdapter(demoList2)
                    } else {
                        demoList += Pair(prefOriginCityId!!.toInt(), preforigin.toString())
                        setCityAdapter(demoList2)
                    }
                }
            }
        }
    }
}