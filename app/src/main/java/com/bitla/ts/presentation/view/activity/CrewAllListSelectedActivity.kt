package com.bitla.ts.presentation.view.activity

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bitla.ts.R
import com.bitla.ts.app.base.*
import com.bitla.ts.data.*
import com.bitla.ts.data.listener.*
import com.bitla.ts.databinding.*
import com.bitla.ts.domain.pojo.all_coach.response.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.employees_details.response.*
import com.bitla.ts.domain.pojo.login_model.*
import com.bitla.ts.domain.pojo.privilege_details_model.response.main_model.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.viewModel.*
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.constants.*
import com.bitla.ts.utils.dialog.*
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
import java.io.*
import java.util.*


class CrewAllListSelectedActivity : BaseActivity(), OnItemClickListener, OnItemPassData,
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    android.widget.SearchView.OnQueryTextListener,
    DialogSingleButtonListener {

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private lateinit var crewAllListSelectedBinding: ActivityCrewAllListSelectedBinding
    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var crewAdapter: CrewSelectAllCoachItemAdapter
    private lateinit var driverCrewSelectItemAdapter: DriverCrewSelectItemAdapter
    private var privilegeResponseModel: PrivilegeResponseModel?= null
    private var selectionType: Int = 0
    private var resID: String = ""
    private var getKeyFromCrewDetails: String = ""
    private var employeesList: MutableList<Employee> = mutableListOf()
    private var coachList: MutableList<AllCoach> = mutableListOf()
    private var tempList = arrayListOf<AllCoach>()
    private var tempListCrewDetails = arrayListOf<Employee>()
    private var driverCrewListFilter = arrayListOf<Employee>()
    private var driverCrewId: String = ""
    private var driverCrewName: String = ""
    private var finalCoachList = mutableListOf<AllCoach>()
    private var isValidateBusCrewUpdationForCoachs = false
    private var locale: String? = ""
    private var country: String? = null

    override fun isInternetOnCallApisAndInitUI() {
        getPrefs()
    }

    override fun initUI() {
        crewAllListSelectedBinding = ActivityCrewAllListSelectedBinding.inflate(layoutInflater)
        val view = crewAllListSelectedBinding.root

        getPrefs()
        crewAllListSelectedBinding.etSearch.setOnQueryTextListener(this)

        driverCrewSelectItemAdapter = DriverCrewSelectItemAdapter(this, this)

        crewAllListSelectedBinding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }

        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(crewAllListSelectedBinding.root)
        }
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                    showToast(it)
                }
            }
        }
    }

    private fun setEmployeesDetailsObserver() {
        pickUpChartViewModel.employeesDetailsResponse.observe(this) {

            if (::crewAllListSelectedBinding.isInitialized)
                crewAllListSelectedBinding.progressBarList.gone()
            tempListCrewDetails.clear()
            driverCrewListFilter.clear()

            try {
                if (it != null) {

                    when (it.code) {
                        200 -> {
                            employeesList = it.employees

                            tempListCrewDetails.addAll(employeesList)

                            if (tempListCrewDetails.size == 0) {
                                if (::crewAllListSelectedBinding.isInitialized)
                                    crewAllListSelectedBinding.NoResult.visible()

                            } else {
                                for (i in 0 until employeesList.size) {

                                    when {
                                        tempListCrewDetails[i].employeeType == "DRIVER"
                                                && getKeyFromCrewDetails == getString(R.string.driver_1) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }

                                        tempListCrewDetails[i].employeeType == "DRIVER"
                                                && getKeyFromCrewDetails == getString(R.string.driver_2) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }

                                        tempListCrewDetails[i].employeeType == "DRIVER"
                                                && getKeyFromCrewDetails == getString(R.string.driver_3) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }
                                        tempListCrewDetails[i].employeeType == "COLLECTION_PERSON"
                                                && getKeyFromCrewDetails == getString(R.string.collection_person) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }


                                        tempListCrewDetails[i].employeeType == "CONDUCTOR"
                                                && getKeyFromCrewDetails == getString(R.string.conductor2) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }

                                        tempListCrewDetails[i].employeeType == "CONTRACTOR"
                                                && getKeyFromCrewDetails == getString(R.string.contractor2) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }

                                        tempListCrewDetails[i].employeeType == "CLEANER"
                                                && getKeyFromCrewDetails == getString(R.string.cleaner2) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }

                                        tempListCrewDetails[i].employeeType == "CHECKING_INSPECTOR"
                                                && getKeyFromCrewDetails == getString(R.string.checking_inspector) -> {
                                            driverCrewListFilter.add(tempListCrewDetails[i])
                                        }
                                    }
                                }
                                if (::crewAllListSelectedBinding.isInitialized) {
                                    crewAllListSelectedBinding.NoResult.gone()
                                    setDriverCrewAdapter(driverCrewListFilter)
                                }
                            }

                        }
                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }
                        else -> {
                            it.result?.message?.let { it1 -> toast(it1) }
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                Timber.d("An error occurred at setEmployeesDetailsObserver(): ${t.message}")
            }
        }
    }

    private fun callEmployeesDetailsApi() {
        if (this.isNetworkAvailable()) {

            pickUpChartViewModel.getEmployeesDetails(
                apiKey = loginModelPref.api_key,
                apiType = employee_details_method_name,
                locale = locale.toString()
            )
        } else this.noNetworkToast()
    }

    private fun setAllCoachObserver() {

        pickUpChartViewModel.allCoachResponse.observe(this) {
            if (::crewAllListSelectedBinding.isInitialized)
                crewAllListSelectedBinding.progressBarList.gone()
            tempList.clear()

            try {
                if (it != null) {
                    when (it.code) {
                        200 -> {
                            coachList = it.allCoaches

                            if (coachList.size == 0) {
                                if (::crewAllListSelectedBinding.isInitialized)
                                    crewAllListSelectedBinding.NoResult.visible()

                            } else {
                                tempList.addAll(coachList)
                                if (::crewAllListSelectedBinding.isInitialized) {
                                    setCrewAllCoachAdapter(tempList)
                                    crewAllListSelectedBinding.NoResult.gone()
                                }
                            }

                        }
                        401 -> {
                            /*DialogUtils.unAuthorizedDialog(
                                this,
                                "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
                                this
                            )*/
                            showUnauthorisedDialog()

                        }
                        else -> {
                            it.result?.message?.let { it1 -> toast(it1) }
                        }
                    }
                } else {
                    toast(getString(R.string.server_error))
                }
            } catch (t: Throwable) {
                toast(getString(R.string.opps))
                Timber.d("An error occurred at setAllCoachObserver(): ${t.message}")
            }
        }
    }

    private fun callAllCoachApi() {
        if (this.isNetworkAvailable()) {
            pickUpChartViewModel.getAllCoach(
                apiKey = loginModelPref.api_key,
                reservationId = resID.toString(),
                locale = locale ?: "",
                apiType = all_coach_method_name
            )
        } else this.noNetworkToast()
    }

    @SuppressLint("SetTextI18n")
    private fun getPrefs() {
        bccId = PreferenceUtils.getBccId()
        loginModelPref = PreferenceUtils.getLogin()
        locale = PreferenceUtils.getlang()

        resID = intent.getLongExtra(getString(R.string.res_id), 0).toString()
        isValidateBusCrewUpdationForCoachs = intent.getBooleanExtra("isValidateBusCrewUpdationForCoachs", false)
        getKeyFromCrewDetails = intent.getStringExtra(getString(R.string.crewDetailsKey)).toString()

        if (getPrivilegeBase() != null) {
             privilegeResponseModel = getPrivilegeBase() as PrivilegeResponseModel

            if (!privilegeResponseModel?.country.isNullOrEmpty()) {
                country = privilegeResponseModel?.country
            }
        }

        if (country != null && country.equals(INDIA, true)) {
            if (::crewAllListSelectedBinding.isInitialized) {
                if (getKeyFromCrewDetails== getString(R.string.conductor2)){
                    crewAllListSelectedBinding.header.text = getString(R.string.select) + " " + getString(R.string.conductor2) + " " +getString(R.string.from_list)
                } else {
                    crewAllListSelectedBinding.header.text = getString(R.string.select) + " " + getKeyFromCrewDetails + " " +getString(R.string.from_list)
                }
            }
        } else {
            if (::crewAllListSelectedBinding.isInitialized) {
                crewAllListSelectedBinding.header.text = getString(R.string.select) + " " + getKeyFromCrewDetails + " " + getString(R.string.from_list)
            }
        }


        if (isValidateBusCrewUpdationForCoachs && (getKeyFromCrewDetails != getString(R.string.coach))) {
            if (intent.getBundleExtra("bundle") != null) {
                val employeeDetailsAllCoach = intent.getBundleExtra("bundle")
                    ?.getSerializable("employeeArrayList") as ArrayList<Employee?>
                val temp = mutableListOf<Employee>()
                Timber.d("Employeeeee ${temp}")
                employeeDetailsAllCoach.forEach {
                    if (it != null) {
                        temp.add(it)
                    }
                }
                employeesList = temp
                setDriverCrewAdapter(temp)
            }
        } else {
            if (getKeyFromCrewDetails == getString(R.string.coach)) {
                callAllCoachApi()
                setAllCoachObserver()

            } else {
                callEmployeesDetailsApi()
                setEmployeesDetailsObserver()
            }
        }
    }

    private fun setCrewAllCoachAdapter(allCoachList: MutableList<AllCoach>) {
        crewAllListSelectedBinding.progressBarList.gone()
        finalCoachList = allCoachList

        Timber.d("All Coach List $finalCoachList")

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        allCoachList.sortBy { it.name.lowercase(Locale.getDefault()) }
        crewAllListSelectedBinding.rvCrewExpenses.layoutManager = layoutManager
        crewAdapter = CrewSelectAllCoachItemAdapter(this, allCoachList, this)
        crewAllListSelectedBinding.rvCrewExpenses.adapter = crewAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDriverCrewAdapter(employeeList: MutableList<Employee>) {
        if (employeeList.size < 1) {
            crewAllListSelectedBinding.NoResult.visible()
        } else {
            crewAllListSelectedBinding.NoResult.gone()
        }
        crewAllListSelectedBinding.progressBarList.gone()
        Timber.d("EmployeeList List $employeeList")

        sortListData(employeeList)

        if (::driverCrewSelectItemAdapter.isInitialized) {
            layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            crewAllListSelectedBinding.rvCrewExpenses.layoutManager = layoutManager
            crewAllListSelectedBinding.rvCrewExpenses.adapter = driverCrewSelectItemAdapter
            driverCrewSelectItemAdapter.addData(employeeList)
        }

        if (::crewAdapter.isInitialized) {
            crewAdapter.notifyDataSetChanged()
        }
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
    }

    override fun onClickOfItem(data: String, position: Int) {
        try {
            if (isValidateBusCrewUpdationForCoachs) {
                if (getKeyFromCrewDetails == getString(R.string.coach)) {
                    val intent = Intent()
                    driverCrewId = finalCoachList[position].id
                    driverCrewName = data
                    val arrayList = ArrayList<Employee>()

                    finalCoachList[position].employees?.forEach {
                        if (it != null) {
                            arrayList.add(it)
                        }
                    }

                    Timber.d("$arrayList")
                    val bundle = Bundle()
                    bundle.putSerializable("coachListArrayList", arrayList as Serializable)
                    intent.putExtra("coachListBundle", bundle)
                    intent.putExtra(getString(R.string.idFromCrewDetails), driverCrewId)
                    intent.putExtra(getString(R.string.nameFromCrewDetails), driverCrewName)
                    intent.putExtra(getString(R.string.crewDetailsKey), getKeyFromCrewDetails)
                    intent.putExtra("ALLCOACH", true)

                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val intent = Intent()
                    driverCrewId = position.toString()
                    driverCrewName = data

                    intent.putExtra(getString(R.string.idFromCrewDetails), driverCrewId)
                    intent.putExtra(getString(R.string.nameFromCrewDetails), driverCrewName)
                    intent.putExtra(getString(R.string.crewDetailsKey), getKeyFromCrewDetails)
                    intent.putExtra("ALLCOACH", false)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {

                if (::crewAllListSelectedBinding.isInitialized) {

                    if (getKeyFromCrewDetails == getString(R.string.coach)) {
                        try {
                            val intent = Intent()
                            driverCrewId = finalCoachList[position].id
                            driverCrewName = data
                            intent.putExtra(getString(R.string.idFromCrewDetails), driverCrewId)
                            intent.putExtra(getString(R.string.nameFromCrewDetails), driverCrewName)
                            intent.putExtra(
                                getString(R.string.crewDetailsKey),
                                getKeyFromCrewDetails
                            )
                            setResult(RESULT_OK, intent)
                            finish()
                        } catch (e: Exception) {
                            Timber.d("exceptionMsg ${e.message}")
                        }
                    } else {
                        val intent = Intent()
                        driverCrewId = position.toString()
                        driverCrewName = data
                        intent.putExtra(getString(R.string.idFromCrewDetails), driverCrewId)
                        intent.putExtra(getString(R.string.nameFromCrewDetails), driverCrewName)
                        intent.putExtra(getString(R.string.crewDetailsKey), getKeyFromCrewDetails)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun sortListData(employeesList: MutableList<Employee> = mutableListOf()) {
//        val sortedList= employeesList.sortedWith(compareBy({ it.name.toLowerCase() }))
        employeesList.sortWith { lhs, rhs ->
            when {
                lhs.name.lowercase() < rhs.name.lowercase() -> {
                    -1
                }
                else -> {
                    0
                }
            }
        }
        Timber.d("sortedList:, ${employeesList}\n")
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onItemData(view: View, str1: String, str2: String) {
    }

    override fun onItemDataMore(view: View, str1: String, str2: String, str3: String) {
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        if (::crewAllListSelectedBinding.isInitialized) {
            if (getKeyFromCrewDetails == getString(R.string.coach)) {
                try {
                    tempList.clear()
                    driverCrewListFilter.clear()

                    val searchText = query?.lowercase(Locale.getDefault())
                    if (searchText != null && searchText.isNotEmpty() == true) {
                        if (getKeyFromCrewDetails == getString(R.string.coach)) {
                            for (i in 0..coachList.size.minus(1)) {
                                if (coachList[i].name.lowercase(Locale.getDefault())
                                        .contains(searchText)
                                ) {

                                    tempList.add(coachList[i])
                                    tempList.sortBy { it.name.lowercase() }
                                }
                            }
                            if (::crewAllListSelectedBinding.isInitialized)
                                crewAllListSelectedBinding.rvCrewExpenses.adapter?.notifyDataSetChanged()

                        }
                    } else {
                        if (getKeyFromCrewDetails == getString(R.string.coach)) {
                            callAllCoachApi()
                            tempList.addAll(coachList)
                            if (::crewAllListSelectedBinding.isInitialized)
                                crewAllListSelectedBinding.rvCrewExpenses.adapter?.notifyDataSetChanged()

                        }

                    }
                    return false
                } catch (e: Exception) {
                    Timber.d("exceptionMsg ${e.message}")
                }
            }else{
               // driverCrewSelectItemAdapter.filter?.filter(query)
                filterDriverList(query)
            }
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (::crewAllListSelectedBinding.isInitialized) {
            if (getKeyFromCrewDetails == getString(R.string.coach)) {
                try {
                    tempList.clear()
                    driverCrewListFilter.clear()

                    val searchText = newText?.lowercase(Locale.getDefault())
                    if (searchText != null && searchText.isNotEmpty() == true) {
                        if (getKeyFromCrewDetails == getString(R.string.coach)) {
                            for (i in 0..coachList.size.minus(1)) {
                                if (coachList[i].name.lowercase(Locale.getDefault())
                                        .contains(searchText)
                                ) {

                                    tempList.add(coachList[i])
                                    tempList.sortBy { it.name.lowercase() }
                                }
                            }
                            if (::crewAllListSelectedBinding.isInitialized)
                                crewAllListSelectedBinding.rvCrewExpenses.adapter?.notifyDataSetChanged()

                        }
                    } else {
                        if (getKeyFromCrewDetails == getString(R.string.coach)) {
                            callAllCoachApi()
                            tempList.addAll(coachList)
                            if (::crewAllListSelectedBinding.isInitialized)
                                crewAllListSelectedBinding.rvCrewExpenses.adapter?.notifyDataSetChanged()

                        }

                    }
                    return false
                } catch (e: Exception) {
                    Timber.d("exceptionMsg ${e.message}")
                }
            }else{
               // driverCrewSelectItemAdapter.filter?.filter(newText)
                filterDriverList(newText)
            }
        }
        return false
    }

    override fun onSingleButtonClick(str: String) {
        if (str == getString(R.string.unauthorized)) {
            //clearAndSave(requireContext())
            PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun filterDriverList(query: String?) {
        try {
            if (query != null && query.isNotEmpty()) {
                coachList.clear()
                tempListCrewDetails.clear()

                val searchText = query.lowercase(Locale.getDefault())
                if (searchText != null && searchText.isNotEmpty()) {
                    for (i in 0..driverCrewListFilter.size.minus(1)) {
                        if (driverCrewListFilter[i].name.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            tempListCrewDetails.add(driverCrewListFilter[i])
                            tempListCrewDetails.sortBy { it.name.lowercase() }
                        }
                    }
                    setDriverCrewAdapter(tempListCrewDetails)

                }
            }else
            {
                setDriverCrewAdapter(driverCrewListFilter)
            }
        } catch (e: Exception) {
            Timber.d("exceptionMsg ${e.message}")
        }
    }
}