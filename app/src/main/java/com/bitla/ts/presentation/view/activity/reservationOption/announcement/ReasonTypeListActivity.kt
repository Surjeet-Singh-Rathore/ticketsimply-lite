package com.bitla.ts.presentation.view.activity.reservationOption.announcement

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitla.ts.R
import com.bitla.ts.app.base.BaseActivity
import com.bitla.ts.data.announcement_method_name
import com.bitla.ts.data.format_type
import com.bitla.ts.data.listener.DialogSingleButtonListener
import com.bitla.ts.data.listener.OnItemClickListener
import com.bitla.ts.databinding.ActivityReasonTypeListBinding
import com.bitla.ts.domain.pojo.NestedItems
import com.bitla.ts.domain.pojo.announcement_model.ReasonTypeSubItemModel
import com.bitla.ts.domain.pojo.announcement_model.request.AnnouncementApiRequest
import com.bitla.ts.domain.pojo.announcement_model.request.ReqBody
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.login_model.LoginModel
import com.bitla.ts.presentation.adapter.ReasonTypeListAdapter
import com.bitla.ts.presentation.view.activity.LoginActivity
import com.bitla.ts.presentation.viewModel.PickUpChartViewModel
import com.bitla.ts.utils.common.edgeToEdge
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PreferenceUtils
import com.bitla.ts.utils.showToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import toast

class ReasonTypeListActivity : BaseActivity(), OnItemClickListener, SearchView.OnQueryTextListener,
    DialogSingleButtonListener {

    private lateinit var binding: ActivityReasonTypeListBinding
    private var searchList = mutableListOf<NestedItems>()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var reasonTypeAdapter: ReasonTypeListAdapter

    private val pickUpChartViewModel by viewModel<PickUpChartViewModel<Any?>>()

    private var bccId: Int? = 0
    private var loginModelPref: LoginModel = LoginModel()
    private var resID: String? = ""
    private var arraivingList: ArrayList<String> = ArrayList()
    private var delayedList: ArrayList<String> = ArrayList()
    private var currentStatusList: ArrayList<String> = ArrayList()
    private var readyToDepartList: ArrayList<String> = ArrayList()
    private var locale: String? = ""


//    val list = mutableListOf<NestedSubItems>()

    private val categoriesModels: ArrayList<ReasonTypeSubItemModel> = ArrayList()
    
    override fun isInternetOnCallApisAndInitUI() {
    }

    override fun initUI() {
        binding = ActivityReasonTypeListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 15+ (API 34)
            edgeToEdge(binding.root)
        }

        getPref()

        if (intent.getStringExtra(getString(R.string.res_id)) != null)
            resID = intent.getStringExtra(getString(R.string.res_id))

        announcementRequestApi(resID.toString())
        announcementApiResponseObserver()


        binding.toolbarImageLeft.setOnClickListener {
            onBackPressed()
        }
//        reasontype()
        binding.etSearch.setOnQueryTextListener(this)
        lifecycleScope.launch {
            pickUpChartViewModel.messageSharedFlow.collect {
                if (it.isNotEmpty()) {
                  showToast(it)
                }
            }
        }
    }

//    private fun reasontype() {
//        val list = mutableListOf<NestedSubItems>()
//        val nestedSubItems = NestedSubItems("test")
//
//        list.add(nestedSubItems)
//
//        val nestedItems = NestedItems("test", list)
//        searchList.add(nestedItems)
//        searchList.add(nestedItems)
//        searchList.add(nestedItems)
//
//
//        list.add(NestedItems( "Trending Video", it as ArrayList<MainDataModel>))
//
//        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.rvreasontype.layoutManager = layoutManager
//        reasonTypeAdapter =
//            ReasesonTypeListAdapter(this, searchList)
//        binding.rvreasontype.adapter = reasonTypeAdapter
//    }


    private fun getPref() {
        bccId = PreferenceUtils.getBccId()
        locale = PreferenceUtils.getlang()
        loginModelPref = PreferenceUtils.getLogin()
    }

    private fun announcementRequestApi(reservationId: String) {
        val announcementApiRequest = AnnouncementApiRequest(
            bccId.toString(),
            format_type,
            announcement_method_name,
            ReqBody(
                loginModelPref.api_key,
                reservationId,
                locale = locale
            )
        )

       /* pickUpChartViewModel.announcementRequestAPI(
            loginModelPref.auth_token,
            loginModelPref.api_key,
            announcementApiRequest,
            announcement_method_name
        )
*/
        pickUpChartViewModel.announcementRequestAPI(
            ReqBody(
                loginModelPref.api_key,
                reservationId,
                locale = locale
            ),
            announcement_method_name
        )
    }

    private fun announcementApiResponseObserver() {

        pickUpChartViewModel.announcementApiResponse.observe(this) {

            if (it != null) {

                when (it.code) {
                    200 -> {
                        Timber.d("announcementApiResponse $it")

                        for (i in 0 until it.reasonType.currentStatus.size) {
                            currentStatusList.add(it.reasonType.currentStatus[i])
                        }

                        for (i in 0 until it.reasonType.delayed.size) {
                            delayedList.add(it.reasonType.delayed[i])
                        }

                        for (i in 0 until it.reasonType.arraiving.size) {
                            arraivingList.add(it.reasonType.arraiving[i])
                        }


                        for (i in 0 until it.reasonType.readyToDepart.size) {
                            readyToDepartList.add(it.reasonType.readyToDepart[i])
                        }

                        categoriesModels.add(
                            ReasonTypeSubItemModel(
                                "CURRENT STATUS",
                                currentStatusList
                            )
                        )
                        categoriesModels.add(
                            ReasonTypeSubItemModel(
                                "READY TO DEPART",
                                readyToDepartList
                            )
                        )
                        categoriesModels.add(ReasonTypeSubItemModel("DELAYED", delayedList))
                        categoriesModels.add(ReasonTypeSubItemModel("ARRIVING", arraivingList))

                        setReasonTypeAdapter(categoriesModels)

                        Timber.d("announcementreasionType $categoriesModels")

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
                        if (it.result?.message != null) {
                            it.result.message.let { it1 -> toast(it1) }
                        }
                    }
                }
            } else {
                toast(getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setReasonTypeAdapter(reasonTypeList: ArrayList<ReasonTypeSubItemModel>) {
        layoutManager = LinearLayoutManager(applicationContext)
        binding.rvreasontype.layoutManager = layoutManager
        reasonTypeAdapter = ReasonTypeListAdapter(this, this)
        binding.rvreasontype.adapter = reasonTypeAdapter
        reasonTypeAdapter.addData(reasonTypeList)

        reasonTypeAdapter.notifyDataSetChanged()
    }

    override fun onClickOfNavMenu(position: Int) {
    }

    override fun onClick(view: View, position: Int) {
    }

    override fun onButtonClick(view: Any, dialog: Dialog) {
        TODO("Not yet implemented")
    }

    override fun onClickOfItem(data: String, position: Int) {

        val intent = Intent()
        intent.putExtra(getString(R.string.select_reason_type), data)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        reasonTypeAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        reasonTypeAdapter.filter.filter(newText)
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
}